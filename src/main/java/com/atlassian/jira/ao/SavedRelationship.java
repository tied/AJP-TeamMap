package com.atlassian.jira.ao;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.*;

@Preload
public interface SavedRelationship extends Entity {
	int getRelationId();
    void setRelationId(int relationId);
    String getUserOneId();
    void setUserOneId(String userId);
    String getUserTwoId();
    void setUserTwoId(String userId);
    String getProjectId();
    void setProjectId(String projectId);
    String getIssueId();
    void setIssueId(String issueId);
    String getReportDate();
    void setReportDate(String date);
}
