package com.atlassian.jira.ao;



import java.util.Date;

import net.java.ao.Entity;
import net.java.ao.Preload;

@Preload
public interface SavedRelationship extends Entity {
	int getRelationId();
    void setRelationId(int relationId);
    String getUserOneId();
    void setUserOneId(String userId);
    String getUserTwoId();
    void setUserTwoId(String userId);
    String getProjectKey();
    void setProjectKey(String projectKey);
    long getIssueId();
    void setIssueId(long issueId);
    Date getReportDate();
    void setReportDate(Date date);
}
