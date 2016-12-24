package co.miracleLab.jira.model;

import java.io.IOException;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;

import co.miracleLab.jira.ao.SavedRelation;


public class Relation {
	private int Id;
	private ApplicationUser owner;
	private Project project;
	private String title;
	private String description;
	private String phrase;
	private String reversePhrase;
	private boolean shared;
	private int color;
	public static String[] colorBars={"success","error","current","complete","moved",""};
	public static String[] colorNames={"green","red","yellow","blue","brown","gray"};
	private UserManager userManager=ComponentAccessor.getUserManager();
	private ProjectManager projectManager=ComponentAccessor.getProjectManager();
	
	public Relation(SavedRelation record) throws ItemException{
		try{
			setOwner(userManager.getUserByName(record.getMlajtpOwnerId()));
			setTitle(record.getMlajtpTitle());
			setDescription(record.getMlajtpDescription());
			setShared(record.isMlajtpShared());
			setId(record.getID());
			setPhrase(record.getMlajtpPhrase());
			setReversePhrase(record.getMlajtpReversePhrase());
			setColor(record.getMlajtpColor());
			setProject(projectManager.getProjectByCurrentKeyIgnoreCase(record.getMlajtpProjectId()));
		}
		catch (Exception e){
			ItemException ie=new ItemException();
			throw ie;
		}
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Relation other = (Relation) obj;
		if (Id != other.Id)
			return false;
		return true;
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

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
}
