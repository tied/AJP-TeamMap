package co.miracleLab.jira.ao;

import java.sql.SQLException;
import java.util.List;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.charts.Chart;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.transaction.TransactionCallback;

import net.java.ao.EntityManager;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

public class RelationService {
	private final ActiveObjects ao;

	public RelationService(ActiveObjects ao) {
		this.ao = checkNotNull(ao);
	}

	public SavedRelation add(String title, String description, ApplicationUser user, Project project,
			String phrase, String reversePhrase ,int color, boolean shared) {
		final SavedRelation relation = ao.create(SavedRelation.class);
		relation.setMlajtpTitle(title);
		relation.setMlajtpDescription(description);
		relation.setMlajtpOwnerId(user.getUsername());
		relation.setMlajtpProjectId(project.getKey());
		relation.setMlajtpPhrase(phrase);
		relation.setMlajtpReversePhrase(reversePhrase);
		relation.setMlajtpShared(shared);
		relation.setMlajtpColor(color);
		relation.save();
		return relation;
	}

	public List<SavedRelation> all() {
		return newArrayList(ao.find(SavedRelation.class));
	}
	
	public List<SavedRelation> allinProject(String projeckKey) {
		return newArrayList(ao.find(SavedRelation.class, "MLAJTP_PROJECT_ID = ?", projeckKey));
	}
	
	public List<SavedRelation> allActiveInProject(String projeckKey) {
		return newArrayList(ao.find(SavedRelation.class, "MLAJTP_PROJECT_ID = ? AND MLAJTP_SHARED = ?", projeckKey,true));
	}

	public void deleteMe(String id) {
		for (SavedRelation relation : ao.find(SavedRelation.class, "ID = ?", Integer.parseInt(id))) {
			ao.delete(relation);
		}
	}

	public void toggleMe(String id) {
		for (SavedRelation relation : ao.find(SavedRelation.class, "ID = ?", Integer.parseInt(id))) {
			if(relation.isMlajtpShared())
				relation.setMlajtpShared(false);
			else
				relation.setMlajtpShared(true);
			relation.save();
		}
	}
	
	public SavedRelation findRelation(String id){
		SavedRelation result=null;
		for (SavedRelation relation : ao.find(SavedRelation.class, "ID = ?", Integer.parseInt(id))) {
			result=relation;
		}
		return result;
	}
	
	public int count(){
		return ao.count(SavedRelation.class);
	}
	
}