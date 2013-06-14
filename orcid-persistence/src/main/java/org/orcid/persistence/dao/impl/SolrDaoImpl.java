/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.dao.impl;

import static schema.constants.SolrConstants.*;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.orcid.persistence.dao.SolrDao;
import org.orcid.persistence.solr.entities.OrcidSolrDocument;
import org.orcid.persistence.solr.entities.OrcidSolrResult;
import org.orcid.persistence.solr.entities.OrcidSolrResults;
import org.springframework.dao.NonTransientDataAccessResourceException;

import schema.constants.SolrConstants;

public class SolrDaoImpl implements SolrDao {

    @Resource(name = "solrServer")
    private SolrServer solrServer;

    @Resource(name = "solrServerReadOnly")
    private SolrServer solrServerReadOnly;

    private static final String[] SCORE_AND_INDIVIDUAL_FIELDS = new String[] { "score", ORCID, GIVEN_NAMES, FAMILY_NAME, EMAIL_ADDRESS,
            AFFILIATE_PRIMARY_INSTITUTION_NAMES, AFFILIATE_INSTITUTION_NAME, AFFILIATE_PAST_INSTITUTION_NAMES, CREDIT_NAME, OTHER_NAMES };

    @Override
    public void persist(OrcidSolrDocument orcidSolrDocument) {
        try {
            solrServer.addBean(orcidSolrDocument);
            solrServer.commit();
        } catch (SolrServerException se) {
            throw new NonTransientDataAccessResourceException("Error persisting to SOLR Server", se);
        } catch (IOException ioe) {
            throw new NonTransientDataAccessResourceException("IOException when persisting to SOLR", ioe);
        }

    }

    @Override
    public void removeOrcids(List<String> orcids) {
        try {
            solrServer.deleteById(orcids);
            solrServer.commit();
        } catch (SolrServerException se) {
            throw new NonTransientDataAccessResourceException("Error deleting orcids from SOLR Server", se);
        } catch (IOException ioe) {
            throw new NonTransientDataAccessResourceException("Error deleting orcids from SOLR Server", ioe);
        }
    }

    @Override
    public OrcidSolrResult findByOrcid(String orcid) {
        OrcidSolrResult orcidSolrResult = null;
        SolrQuery query = new SolrQuery();
        query.setQuery(SolrConstants.ORCID + ":" + orcid).setFields("score", "*");
        ;
        try {
            QueryResponse queryResponse = solrServerReadOnly.query(query);
            if (!queryResponse.getResults().isEmpty()) {
                SolrDocument solrDocument = queryResponse.getResults().get(0);
                orcidSolrResult = new OrcidSolrResult();
                orcidSolrResult.setRelevancyScore((Float) solrDocument.get("score"));
                orcidSolrResult.setOrcid((String) solrDocument.get("orcid"));
                orcidSolrResult.setPublicProfileMessage((String) solrDocument.getFieldValue("public-profile-message"));
            }
        } catch (SolrServerException se) {
            String errorMessage = MessageFormat.format("Error when attempting to retrieve orcid {0}", new Object[] { orcid });
            throw new NonTransientDataAccessResourceException(errorMessage, se);
        }

        return orcidSolrResult;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public OrcidSolrResults findByDocumentCriteria(String queryString, Integer start, Integer rows) {
        OrcidSolrResults orcidSolrResults = new OrcidSolrResults();
        List<OrcidSolrResult> orcidSolrResultsList = new ArrayList<>();
        orcidSolrResults.setResults(orcidSolrResultsList);
        SolrQuery query = new SolrQuery(queryString).setFields(SCORE_AND_INDIVIDUAL_FIELDS);
        if (start != null)
            query.setStart(start);
        if (rows != null)
            query.setRows(rows);
        try {
            QueryResponse queryResponse = solrServerReadOnly.query(query);
            for (SolrDocument solrDocument : queryResponse.getResults()) {
                OrcidSolrResult orcidSolrResult = new OrcidSolrResult();
                orcidSolrResult.setRelevancyScore((Float) solrDocument.getFieldValue("score"));
                orcidSolrResult.setOrcid((String) solrDocument.getFieldValue("orcid"));
                orcidSolrResult.setEmail((String) solrDocument.getFieldValue("email"));
                orcidSolrResult.setGivenNames((String) solrDocument.getFieldValue("given-names"));
                orcidSolrResult.setFamilyName((String) solrDocument.getFieldValue("family-name"));
                orcidSolrResult.setCurrentPrimaryInstitutionAffiliationNames((Collection) solrDocument.getFieldValues("current-primary-institution-affiliation-name"));
                orcidSolrResult.setCurrentInstitutionAffiliationNames((Collection) solrDocument.getFieldValues("current-institution-affiliation-name"));
                orcidSolrResult.setPastInstitutionAffiliationNames((Collection) solrDocument.getFieldValues("past-institution-affiliation-name"));
                orcidSolrResult.setCreditName((String) solrDocument.getFieldValue("credit-name"));
                orcidSolrResult.setOtherNames((Collection) solrDocument.getFieldValues("other-names"));
                orcidSolrResult.setPublicProfileMessage((String) solrDocument.getFieldValue("public-profile-message"));
                orcidSolrResultsList.add(orcidSolrResult);
            }
            orcidSolrResults.setNumFound(queryResponse.getResults().getNumFound());

        } catch (SolrServerException se) {
            throw new NonTransientDataAccessResourceException("Error retrieving from SOLR Server", se);
        }

        return orcidSolrResults;
    }

    @Override
    public OrcidSolrResults findByDocumentCriteria(Map<String, List<String>> queryMap) {
        OrcidSolrResults orcidSolrResults = new OrcidSolrResults();
        List<OrcidSolrResult> orcidSolrResultsList = new ArrayList<>();
        orcidSolrResults.setResults(orcidSolrResultsList);
        SolrQuery solrQuery = new SolrQuery();
        for (Map.Entry<String, List<String>> entry : queryMap.entrySet()) {
            String queryKey = entry.getKey();
            List<String> queryVals = entry.getValue();
            solrQuery.add(queryKey, queryVals.get(0));
        }

        try {
            QueryResponse queryResponse = solrServerReadOnly.query(solrQuery.setFields(SCORE_AND_INDIVIDUAL_FIELDS));
            for (SolrDocument solrDocument : queryResponse.getResults()) {
                OrcidSolrResult orcidSolrResult = new OrcidSolrResult();
                orcidSolrResult.setRelevancyScore((Float) solrDocument.get("score"));
                orcidSolrResult.setOrcid((String) solrDocument.get("orcid"));
                orcidSolrResultsList.add(orcidSolrResult);
            }
            orcidSolrResults.setNumFound(queryResponse.getResults().getNumFound());

        } catch (SolrServerException se) {
            throw new NonTransientDataAccessResourceException("Error retrieving from SOLR Server", se);
        }

        return orcidSolrResults;
    }

}
