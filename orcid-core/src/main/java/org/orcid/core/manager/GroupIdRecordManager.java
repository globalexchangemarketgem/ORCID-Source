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
package org.orcid.core.manager;

import org.orcid.jaxb.model.groupid.GroupIdRecord;
import org.orcid.jaxb.model.groupid.GroupIdRecords;

public interface GroupIdRecordManager {

    GroupIdRecord getGroupIdRecord(String putCode);

    GroupIdRecord createGroupIdRecord(GroupIdRecord groupIdRecord);

    GroupIdRecord updateGroupIdRecord(String putCode, GroupIdRecord groupIdRecord);

    void deleteGroupIdRecord(String putCode);

    GroupIdRecords getGroupIdRecords(String pageSize, String pageNum);

    boolean exists(String groupId);
    
    GroupIdRecord findByGroupId(String groupId);
}
