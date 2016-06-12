package com.atlassian.jira.model;

import java.io.IOException;

import com.atlassian.jira.ao.SavedRelation;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;


public class Relation {
	private int Id;
	private ApplicationUser owner;
	private String title;
	private String description;
	private String phrase;
	private String reversePhrase;
	private boolean shared;
	private int color;
	public static String[] colorBars={"success","error","current","complete","moved",""};
	public static String[] colorNames={"Green","Red","Yellow","Blue","Brown","Gray"};
	
	public Relation(SavedRelation record){
		UserManager userManager=ComponentAccessor.getUserManager();
//		ProjectManager projectManager=ComponentAccessor.getProjectManager();
		setOwner(userManager.getUserByName(record.getOwnerId()));
//		setProject(projectManager.getProjectObjByKey(record.getProjectId()));
		setTitle(record.getTitle());
		setDescription(record.getDescription());
		setShared(record.isShared());
		setId(record.getID());
		setPhrase(record.getPhrase());
		setReversePhrase(record.getReversePhrase());
		setColor(record.getColor());
	}

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public ApplicationUser getOwner() {
		return owner;
	}

	public void setOwner(ApplicationUser owner) {
		this.owner = owner;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public String getReversePhrase() {
		return reversePhrase;
	}

	public void setReversePhrase(String reversePhrase) {
		this.reversePhrase = reversePhrase;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
	public String getColorBar(){
		String colorBar="";
		if(this.color>=0 && this.color<colorBars.length)
			colorBar=colorBars[this.color];
		return colorBar;
	}
	
	public String getColorName(){
		String colorName="";
		if(this.color>=0 && this.color<colorNames.length)
			colorName=colorNames[this.color];
		return colorName;
	}
}
