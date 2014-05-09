package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.CustomEmailManager;
import org.orcid.persistence.dao.CustomEmailDao;
import org.orcid.persistence.jpa.entities.CustomEmailEntity;
import org.orcid.persistence.jpa.entities.EmailType;

public class CustomEmailManagerImpl implements CustomEmailManager {

    @Resource
    private CustomEmailDao customEmailDao;
    
    @Override
    public CustomEmailEntity findByClientIdAndEmailType(String clientDetailsId, EmailType emailType) {
        return customEmailDao.findByClientIdAndEmailType(clientDetailsId, emailType);
    }

    @Override
    public boolean createCustomEmail(String clientDetailsId, EmailType emailType, String subject, String content) {
        return customEmailDao.createCustomEmail(clientDetailsId, emailType, subject, content);
    }

    @Override
    public boolean updateCustomEmail(String clientDetailsId, EmailType emailType, String subject, String content) {
        return customEmailDao.updateCustomEmail(clientDetailsId, emailType, subject, content);
    }

    @Override
    public boolean deleteCustomEmail(String clientDetailsId, EmailType emailType) {
        return customEmailDao.deleteCustomEmail(clientDetailsId, emailType);
    }
}
