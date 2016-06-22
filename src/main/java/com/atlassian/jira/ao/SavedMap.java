package com.atlassian.jira.ao;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.*;

@Preload
public interface SavedMap extends Entity {
	String getTitle();
    void setTitle(String title);
    String getOwnerId();
    void setOwnerId(String userId);
    String getProjectId();
    void setProjectId(String projectId);
    @StringLength(value=StringLength.UNLIMITED)
    String getDescription();
    void setDescription(String description);
    int getRelationId();
    void setRelationId(int relationId);
    boolean isShared();
    void setShared(boolean shared);
    
}
