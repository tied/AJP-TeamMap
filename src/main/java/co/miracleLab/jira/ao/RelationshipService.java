package com.atlassian.jira.ao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.charts.Chart;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.model.Relation;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.transaction.TransactionCallback;

import net.java.ao.EntityManager;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

public class RelationshipService {
	private final ActiveObjects ao;

	public RelationshipService(ActiveObjects ao) {
		this.ao = checkNotNull(ao);
	}

	public SavedRelationship add(Issue issue,Project project, Relation relation, Date date, ApplicationUser userOne, ApplicationUser userTwo) {
		final SavedRelationship relationship = ao.create(SavedRelationship.class);
		relationship.setIssueId(issue.getId());
		relationship.setProjectKey(project.getKey());
		relationship.setRelationId(relation.getId());
		relationship.setReportDate(date);
		relationship.setUserOneId(userOne.getUsername());
		relationship.setUserTwoId(userTwo.getUsername());
		relationship.save();
		return relationship;
	}
	
	public SavedRelationship add(long issueId,String projectId, Relation relation, Date date, String userOne, String userTwo) {
		final SavedRelationship relationship = ao.create(SavedRelationship.class);
		relationship.setIssueId(issueId);
		relationship.setProjectKey(projectId);
		relationship.setRelationId(relation.getId());
		relationship.setReportDate(date);
		relationship.setUserOneId(userOne);
		relationship.setUserTwoId(userTwo);
		relationship.save();
		return relationship;
	}

	public List<SavedRelationship> all() {
		return newArrayList(ao.find(SavedRelationship.class));
	}
	
	public List<SavedRelationship> allinProject(String projeckKey) {
		return newArrayList(ao.find(SavedRelationship.class, "PROJECT_ID like ?", projeckKey));
	}
	
	public List<SavedRelationship> allforIssue(long issueId) {
		return newArrayList(ao.find(SavedRelationship.class, "Issue_ID like ?", issueId));
	}
	
	public List<SavedRelationship> allforRelation(int relationId) {
		return newArrayList(ao.find(SavedRelationship.class, "RELATION_ID like ?", relationId));
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

	public int countByIssue(long issueId) {
		return ao.count(SavedRelationship.class, "Issue_ID like ?", issueId);
	}
	
	public int countByRelation(int relationId) {
		return ao.count(SavedRelationship.class, "RELATION_ID like ?", relationId);
	}
	
}