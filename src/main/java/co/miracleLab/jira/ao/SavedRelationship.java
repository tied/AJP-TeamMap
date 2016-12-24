package co.miracleLab.jira.ao;



import java.util.Date;

import net.java.ao.Entity;
import net.java.ao.Preload;

@Preload
public interface SavedRelationship extends Entity {
	int getMlajtpRelationId();
    void setMlajtpRelationId(int relationId);
    String getMlajtpUserOneId();
    void setMlajtpUserOneId(String userId);
    String getMlajtpUserTwoId();
    void setMlajtpUserTwoId(String userId);
    String getMlajtpProjectKey();
    void setMlajtpProjectKey(String projectKey);
    long getMlajtpIssueId();
    void setMlajtpIssueId(long issueId);
    Date getMlajtpReportDate();
    void setMlajtpReportDate(Date date);
}
