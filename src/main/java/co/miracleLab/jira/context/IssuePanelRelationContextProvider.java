package com.atlassian.jira.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.ao.RelationService;
import com.atlassian.jira.ao.RelationshipService;
import com.atlassian.jira.ao.SavedRelation;
import com.atlassian.jira.ao.SavedRelationship;
import com.atlassian.jira.avatar.Avatar;
import com.atlassian.jira.bc.user.search.AssigneeService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.jira.webwork.WebAction;
import com.atlassian.jira.model.ItemException;
import com.atlassian.jira.model.Relation;
import com.atlassian.jira.model.Relationship;
import com.atlassian.jira.permission.ProjectPermission;
import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.plugin.ProjectPermissionKey;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.util.collect.MapBuilder;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.ContextProvider;

@Named("IssuePanelRelationContextProvider")
public class IssuePanelRelationContextProvider implements ContextProvider
{
    private Long itemCount = 0L;
    private Issue issue;
    private Project project;
    private ApplicationUser user;
    @ComponentImport
	private final ActiveObjects ao;
    private RelationshipService relationshipService;
    private RelationService relationService;
    private String baseUrl=ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);
    
    @Inject
    public IssuePanelRelationContextProvider (ActiveObjects ao){
    	this.ao=ao;
    	relationshipService=new RelationshipService(ao);
    	relationService=new RelationService(ao);
    }
    
    @Override
	public void init(Map<String, String> params) throws PluginParseException {	
	}
    
    @Override
    public Map<String, Object> getContextMap(Map context){
    	WebAction action=new WebAction();
    	user=action.getLoggedInUser();
    	if(context.get("issue")!=null){
    		issue=(Issue) context.get("issue");
    	}
    	project=issue.getProjectObject();
    	List<ApplicationUser> projectAssignableUsers=getProjectAssignableUsers(project);
    	List<Relation> relations=new ArrayList<Relation>();
		for(SavedRelation savedRelation : relationService.allActiveInProject(project.getKey())) {
			Relation relation;
			try {
				relation = new Relation(savedRelation);
				relations.add(relation);
			} catch (ItemException e){}
			
		}
		
		List<Relationship> relationships=new ArrayList<Relationship>();
		for(SavedRelationship savedRelationship : relationshipService.allforIssue(issue.getId())) {
			Relationship relationship;
			try {
				relationship = new Relationship(savedRelationship,relationService);
				relationships.add(relationship);
			} catch (ItemException e) {}
			
		}
		    	
    	Map<String, Object> newContext=MapBuilder.<String, Object>newBuilder()
    			.add("atl.gh.issue.details.tab.count", new Long(relationships.size()))
    			.add("user", user)
    			.add("project", project)
    			.add("issue", issue)
    			.add("projectUsers", projectAssignableUsers)
    			.add("relations", relations)
    			.add("avatarService", ComponentAccessor.getAvatarService())
    			.add("avatarSize", Avatar.Size.SMALL)
    			.add("relationships", relationships)
    			.add("baseURL", baseUrl)
    			.toMap();
        return newContext;
                
    }
    
    private List<ApplicationUser> getProjectAssignableUsers(Project project) {
		AssigneeService assigneeService = ComponentAccessor.getComponent(AssigneeService.class);
		Collection<ApplicationUser> assignableUsers=assigneeService.findAssignableUsers("", project);
		List<ApplicationUser> assignableUsersList=new ArrayList<ApplicationUser>(assignableUsers);
		return assignableUsersList;
	}

}