package com.atlassian.jira.ao;

import java.sql.SQLException;
import java.util.List;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.charts.Chart;
import com.atlassian.sal.api.transaction.TransactionCallback;

import net.java.ao.EntityManager;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

public class RelationshipService {
	private final ActiveObjects ao;

	public RelationshipService(ActiveObjects ao) {
		this.ao = checkNotNull(ao);
	}

	public SavedRelationship add() {
		final SavedRelationship relationship = ao.create(SavedRelationship.class);
		
		relationship.save();
		return relationship;
	}

	public List<SavedRelationship> all() {
		return newArrayList(ao.find(SavedRelationship.class));
	}
	
	public List<SavedRelationship> allinProject(String projeckKey) {
		return newArrayList(ao.find(SavedRelationship.class, "PROJECT_ID like ?", projeckKey));
	}
	
	public List<SavedRelationship> allforIssue(String issueId) {
		return newArrayList(ao.find(SavedRelationship.class, "Issue_ID like ?", issueId));
	}

	public void deleteMe(String id) {
		for (SavedRelationship relationship : ao.find(SavedRelationship.class, "ID like ?", id)) {
			ao.delete(relationship);
		}
	}
	
	public SavedRelationship findRelationship(String id){
		SavedRelationship result=null;
		for (SavedRelationship relationip : ao.find(SavedRelationship.class, "ID like ?", id)) {
			result=relationip;
		}
		return result;
	}
	
}