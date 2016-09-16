package com.atlassian.jira.ao;

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
		relation.setTitle(title);
		relation.setDescription(description);
		relation.setOwnerId(user.getUsername());
		relation.setProjectId(project.getKey());
		relation.setPhrase(phrase);
		relation.setReversePhrase(reversePhrase);
		relation.setShared(shared);
		relation.setColor(color);
		relation.save();
		return relation;
	}

	public List<SavedRelation> all() {
		return newArrayList(ao.find(SavedRelation.class));
	}
	
	public List<SavedRelation> allinProject(String projeckKey) {
		return newArrayList(ao.find(SavedRelation.class, "PROJECT_ID like ?", projeckKey));
	}
	
	public List<SavedRelation> allActiveInProject(String projeckKey) {
		return newArrayList(ao.find(SavedRelation.class, "PROJECT_ID like ? AND SHARED like ?", projeckKey,true));
	}

	public void deleteMe(String id) {
		for (SavedRelation relation : ao.find(SavedRelation.class, "ID like ?", id)) {
			ao.delete(relation);
		}
	}

	public void toggleMe(String id) {
		for (SavedRelation relation : ao.find(SavedRelation.class, "ID like ?", id)) {
			if(relation.isShared())
				relation.setShared(false);
			else
				relation.setShared(true);
			relation.save();
		}
	}
	
	public SavedRelation findRelation(String string){
		SavedRelation result=null;
		for (SavedRelation relation : ao.find(SavedRelation.class, "ID like ?", string)) {
			result=relation;
		}
		return result;
	}
	
	public int count(){
		return ao.count(SavedRelation.class);
	}
	
}