package com.atlassian.jira.context;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.ao.RelationService;
import com.atlassian.jira.ao.RelationshipService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.jira.webwork.WebAction;
import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.collect.MapBuilder;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.ContextProvider;

@Named("IssuePanelRelationContextProvider")
public class IssuePanelRelationContextProvider  implements ContextProvider
{
    private Long itemCount = 0L;
    private String issueKey="lol";
    private String projectKey="lol2";
    private String usern="jola";
    @ComponentImport
	private final ActiveObjects ao;
    private RelationshipService relationshipService;
    private RelationService relationService;
    
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
    	Project project=action.getSelectedProjectObject();
    	ApplicationUser user=action.getLoggedInUser();
    	if(context.get("issue")!=null){
    		Issue issue=(Issue) context.get("issue");
    		issueKey=issue.getKey();
    	}
    		
    	projectKey=project.getKey();
    	usern=user.getId().toString();
    	Map<String, Object> newContext=MapBuilder.<String, Object>newBuilder()
    			.add("atl.gh.issue.details.tab.count", itemCount)
    			.add("relationPanelSelectedIssue", issueKey)
    			.add("relationPanelSelectedProject", projectKey)
    			.add("relationPanelUser", usern)
    			.add("sizeofContext", relationService.count())
    			.toMap();
        return newContext;
                
    }

}