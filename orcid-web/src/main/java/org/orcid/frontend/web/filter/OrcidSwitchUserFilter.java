/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.InternalSSOManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.frontend.web.exception.SwitchUserAuthenticationException;
import org.orcid.jaxb.model.common_v2.OrcidType;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionByEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidSwitchUserFilter extends SwitchUserFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidSwitchUserFilter.class);
    
    @Resource
    private SourceManager sourceManager;

    @Resource
    private LocaleManager localeManager;

    @Resource
    private InternalSSOManager internalSSOManager;

    private UserDetailsService orcidUserDetailsService;

    private UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();
    
    @Resource
    private ProfileDao profileDao;

    public void setOrcidUserDetailsService(UserDetailsService userDetailsService) {
        this.orcidUserDetailsService = userDetailsService;        
        super.setUserDetailsService(userDetailsService);
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (internalSSOManager.enableCookie()) {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
            if (requiresSwitchUser(request)) {
                // Add the cookie for the delegate user
                String targetUserOrcid = request.getParameter(SPRING_SECURITY_SWITCH_USERNAME_KEY);
                if (!PojoUtil.isEmpty(targetUserOrcid)) {
                    // If it is switching back to the original user
                    if (isSwitchingBack(request)) {
                        internalSSOManager.getAndUpdateCookie(targetUserOrcid, request, response);
                    } else {
                        // If it is switching user
                        internalSSOManager.writeCookie(targetUserOrcid, request, response);
                    }
                }
            }
        }

        super.doFilter(req, res, chain);
    }

    @Override
    protected Authentication attemptSwitchUser(HttpServletRequest request) throws AuthenticationException {
        String targetUserOrcid = request.getParameter(SPRING_SECURITY_SWITCH_USERNAME_KEY);
        ProfileEntity profileEntity = sourceManager.retrieveSourceProfileEntity();
        if (OrcidType.ADMIN.equals(profileEntity.getOrcidType())) {
            return switchUser(request);
        }
        // If we are switching back to me it is OK
        if (isSwitchingBack(request)) {
            return switchUser(request);
        }
        for (GivenPermissionByEntity gpbe : profileEntity.getGivenPermissionBy()) {
            if (gpbe.getGiver().getId().equals(targetUserOrcid)) {
                return switchUser(request);
            }
        }        
        throw new SwitchUserAuthenticationException(localeManager.resolveMessage("web.orcid.switchuser.exception"));
    }

    private Authentication switchUser(HttpServletRequest request) {
        String username = request.getParameter(SPRING_SECURITY_SWITCH_USERNAME_KEY);

        if (username == null) {
            username = "";
        }
        
        LOGGER.debug("Attempt to switch to user [" + username + "]");

        UserDetails targetUser = orcidUserDetailsService.loadUserByUsername(username);
        userDetailsChecker.check(targetUser);

        // OK, create the switch user token
        UsernamePasswordAuthenticationToken targetUserRequest = createSwitchUserToken(request, targetUser);

        LOGGER.debug("Switch User Token [" + targetUserRequest + "]");


        return targetUserRequest;
    }

    private UsernamePasswordAuthenticationToken createSwitchUserToken(HttpServletRequest request, UserDetails targetUser) {
        // grant an additional authority that contains the original
        // Authentication object
        // which will be used to 'exit' from the current switched user.

        Authentication currentAuth;

        try {
            // SEC-1763. Check first if we are already switched.
            currentAuth = attemptExitUser(request);
        } catch (AuthenticationCredentialsNotFoundException e) {
            currentAuth = SecurityContextHolder.getContext().getAuthentication();
        }

        GrantedAuthority switchAuthority = new SwitchUserGrantedAuthority(ROLE_PREVIOUS_ADMINISTRATOR, currentAuth);

        // get the original authorities
        Collection<? extends GrantedAuthority> orig = targetUser.getAuthorities();

        // add the new switch user authority
        List<GrantedAuthority> newAuths = new ArrayList<GrantedAuthority>(orig);
        newAuths.add(switchAuthority);

        // create the new authentication token
        ProfileEntity profile = getProfileEntity(targetUser.getUsername());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(targetUser.getUsername(), null, newAuths);
        authentication.setDetails(toOrcidProfileUserDetails(profile));

        return authentication;
    }

    private boolean isSwitchingBack(HttpServletRequest request) {
        String targetUserOrcid = request.getParameter(SPRING_SECURITY_SWITCH_USERNAME_KEY);
        String realUser = sourceManager.retrieveRealUserOrcid();
        return targetUserOrcid.equals(realUser);
    }
    
    private ProfileEntity getProfileEntity(String username) {
        ProfileEntity profile = null;
        if (!StringUtils.isEmpty(username)) {
            profile = profileDao.find(username);            
        }
        return profile;
    }
    
    private OrcidProfileUserDetails toOrcidProfileUserDetails(ProfileEntity profileEntity) {
        String orcid = profileEntity.getId();
        Set<EmailEntity> emails = profileEntity.getEmails();
        
        for (EmailEntity email : emails) {
            if (email.getPrimary()) {
                return new OrcidProfileUserDetails(orcid, email.getId(), profileEntity.getPassword(), profileEntity.getOrcidType());
            }
        }
        return null;
    }

}
