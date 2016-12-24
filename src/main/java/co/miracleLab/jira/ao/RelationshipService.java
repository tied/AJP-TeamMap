package co.miracleLab.jira.ao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.charts.Chart;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.transaction.TransactionCallback;

import co.miracleLab.jira.model.Relation;
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
		relationship.setMlajtpIssueId(issue.getId());
		relationship.setMlajtpProjectKey(project.getKey());
		relationship.setMlajtpRelationId(relation.getId());
		relationship.setMlajtpReportDate(date);
		relationship.setMlajtpUserOneId(userOne.getUsername());
		relationship.setMlajtpUserTwoId(userTwo.getUsername());
		relationship.save();
		return relationship;
	}
	
	public SavedRelationship add(long issueId,String projectId, Relation relation, Date date, String userOne, String userTwo) {
		final SavedRelationship relationship = ao.create(SavedRelationship.class);
		relationship.setMlajtpIssueId(issueId);
		relationship.setMlajtpProjectKey(projectId);
		relationship.setMlajtpRelationId(relation.getId());
		relationship.setMlajtpReportDate(date);
		relationship.setMlajtpUserOneId(userOne);
		relationship.setMlajtpUserTwoId(userTwo);
		relationship.save();
		return relationship;
	}

	public List<SavedRelationship> all() {
		return newArrayList(ao.find(SavedRelationship.class));
	}
	
	public List<SavedRelationship> allinProject(String projeckKey) {
		return newArrayList(ao.find(SavedRelationship.class, "MLAJTP_PROJECT_ID = ?", projeckKey));
	}
	
	public List<SavedRelationship> allforIssue(long issueId) {
		return newArrayList(ao.find(SavedRelationship.class, "MLAJTP_ISSUE_ID = ?", issueId));
	}
	
	public List<SavedRelationship> allforRelation(int relationId) {
		return newArrayList(ao.find(SavedRelationship.class, "MLAJTP_RELATION_ID = ?", relationId));
	}

	public void deleteMe(String id) {
		for (SavedRelationship relationship : ao.find(SavedRelationship.class, "ID = ?", Integer.parseInt(id))) {
			ao.delete(relationship);
		}
	}
	
	public SavedRelationship findRelationship(String id){
		SavedRelationship result=null;
		for (SavedRelationship relationip : ao.find(SavedRelationship.class, "ID = ?", Integer.parseInt(id))) {
			result=relationip;
		}
		return result;
	}

	public int countByIssue(long issueId) {
		return ao.count(SavedRelationship.class, "MLAJTP_ISSUE_ID = ?", issueId);
	}
	
	public int countByRelation(int relationId) {
		return ao.count(SavedRelationship.class, "MLAJTP_RELATION_ID = ?", relationId);
	}
	
}