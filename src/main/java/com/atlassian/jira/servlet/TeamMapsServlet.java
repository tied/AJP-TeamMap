package com.atlassian.jira.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.ao.RelationService;
import com.atlassian.jira.ao.RelationshipService;
import com.atlassian.jira.ao.SavedRelation;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.jira.webwork.WebAction;
import com.atlassian.jira.model.ItemException;
import com.atlassian.jira.model.Relation;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named("TeamMapsServlet")
public class TeamMapsServlet extends HttpServlet{
    private static final Logger log = LoggerFactory.getLogger(TeamMapsServlet.class);
    @ComponentImport
   	private final UserManager userManager;
   	@ComponentImport
   	private final LoginUriProvider loginUriProvider;
   	@ComponentImport
   	private final TemplateRenderer templateRenderer;
   	@ComponentImport
   	private final IssueManager issueManager;
   	@ComponentImport
   	private final ProjectManager projectManager;
   	@ComponentImport
   	private final SearchService searchService;
   	@ComponentImport
   	private final PluginSettingsFactory pluginSettingsFactory;
   	@ComponentImport
   	private final ActiveObjects ao;
   	private String baseUrl;
   	private RelationService relationService;
   	private RelationshipService relationshipService;
   	
   	@Inject
   	public TeamMapsServlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer templateRenderer,
			PluginSettingsFactory pluginSettingsFactory, ProjectManager projectManager, SearchService searchService,
			ActiveObjects ao,IssueManager issueManager) {
		this.userManager = userManager;
		this.loginUriProvider = loginUriProvider;
		this.templateRenderer = templateRenderer;
		this.issueManager = issueManager;
		this.searchService = searchService;
		this.projectManager = projectManager;
		this.pluginSettingsFactory = pluginSettingsFactory;
		baseUrl=ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);
		this.ao=ao;
		relationService=new RelationService(ao);
		relationshipService=new RelationshipService(ao);
	}
   	
   	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		Map<String, Object> context = new HashMap<String, Object>();

    	UserProfile userProfile = userManager.getRemoteUser(request);
		if (userProfile == null) {
			redirectToLogin(request, response);
			return;
		}
		WebAction action=new WebAction();
		Project project = action.returnSelectedProject();
		context.put("project", project);
		
		context.put("baseURL", baseUrl);
		context.put("mode", "maps");
		templateRenderer.render("templates/teamMap.vm", context, response.getWriter());
    }
   	
   	private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
	}

	private URI getUri(HttpServletRequest request) {
		StringBuffer builder = request.getRequestURL();
		if (request.getQueryString() != null) {
			builder.append("?");
			builder.append(request.getQueryString());
		}
		return URI.create(builder.toString());
	}

	private ApplicationUser getCurrentUser(HttpServletRequest req) {
		com.atlassian.jira.user.util.UserManager jiraUserManager = ComponentAccessor.getUserManager();
		return jiraUserManager.getUserByName(userManager.getRemoteUser(req).getUsername());
	}

}