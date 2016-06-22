package com.atlassian.jira.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.atlassian.jira.ao.RelationService;
import com.atlassian.jira.ao.SavedRelationship;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;

public class Relationship {
	private int id;
	private Project project;
	private Issue issue;
	private Relation relation;
	private ApplicationUser firstUser;
	private ApplicationUser secondUser;
	private Date date;
	private UserManager userManager=ComponentAccessor.getUserManager();
	private ProjectManager projectManager=ComponentAccessor.getProjectManager();
	private IssueManager issueManager=ComponentAccessor.getIssueManager();
	public Relationship(SavedRelationship savedRelationship,RelationService relationService) throws ItemException{
		try {
		this.setId(savedRelationship.getID());
		this.setProject(projectManager.getProjectObjByKeyIgnoreCase(savedRelationship.getProjectKey()));
		this.setIssue(issueManager.getIssueObject(savedRelationship.getIssueId()));
		this.setFirstUser(userManager.getUserByName(savedRelationship.getUserOneId()));
		this.setSecondUser(userManager.getUserByName(savedRelationship.getUserTwoId()));
		this.setDate(savedRelationship.getReportDate());
		Relation aRelation=new Relation(relationService.findRelation(Integer.toString(savedRelationship.getRelationId())));
		if(aRelation.isShared())
			this.setRelation(aRelation);
		else
			throw new Exception();
		} catch (Exception e){
			ItemException ie=new ItemException();
			throw ie;
		}
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	public Issue getIssue() {
		return issue;
	}
	public void setIssue(Issue issue) {
		this.issue = issue;
	}
	public Relation getRelation() {
		return relation;
	}
	public void setRelation(Relation relation) {
		this.relation = relation;
	}
	public ApplicationUser getFirstUser() {
		return firstUser;
	}
	public void setFirstUser(ApplicationUser firstUser) {
		this.firstUser = firstUser;
	}
	public ApplicationUser getSecondUser() {
		return secondUser;
	}
	public void setSecondUser(ApplicationUser secondUser) {
		this.secondUser = secondUser;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getDateString() {
		DateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy");
		return df.format(this.date);
	}
	
	public String getDateString(String format) {
		DateFormat df = new SimpleDateFormat(format);
		return df.format(this.date);
	}
	

}
