package co.miracleLab.jira.ao;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.*;

@Preload
public interface SavedRelation extends Entity {
	String getMlajtpTitle();
    void setMlajtpTitle(String title);
    String getMlajtpOwnerId();
    void setMlajtpOwnerId(String userId);
    String getMlajtpProjectId();
    void setMlajtpProjectId(String projectId);
    @StringLength(value=StringLength.UNLIMITED)
    String getMlajtpPhrase();
    void setMlajtpPhrase(String phrase);
    @StringLength(value=StringLength.UNLIMITED)
    String getMlajtpReversePhrase();
    void setMlajtpReversePhrase(String phrase);
    @StringLength(value=StringLength.UNLIMITED)
    String getMlajtpDescription();
    void setMlajtpDescription(String description);
    int getMlajtpColor();
    void setMlajtpColor(int color);
    boolean isMlajtpShared();
    void setMlajtpShared(boolean shared);
    
}
