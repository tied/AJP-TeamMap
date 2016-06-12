package com.atlassian.jira.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.ao.RelationService;
import com.atlassian.jira.ao.SavedRelation;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.jira.jira.webwork.WebAction;
import com.atlassian.jira.model.Relation;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named("TeamRelationSettingServlet")
public class TeamRelationSettingServlet extends HttpServlet{
    private static final Logger log = LoggerFactory.getLogger(TeamRelationSettingServlet.class);
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

	@Inject
	public TeamRelationSettingServlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer templateRenderer,
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
		
		List<Relation> relations=new ArrayList<Relation>();
		for(SavedRelation savedRelation : relationService.allinProject(project.getKey())){
			if(savedRelation.getOwnerId().equals(userProfile.getUsername()) || savedRelation.isShared()){
				Relation relation= new Relation(savedRelation);
				relations.add(relation);
			}
    	}
		context.put("relations", relations);
		
		context.put("baseURL", baseUrl);
		context.put("mode", "setting");
		templateRenderer.render("templates/relationSetting.vm", context, response.getWriter());
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
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		UserProfile userProfile = userManager.getRemoteUser(request);
		if (userProfile == null) {
			redirectToLogin(request, response);
			return;
		}
		final String title = request.getParameter("title");
		final String description = request.getParameter("description");
		final String phrase = request.getParameter("phrase");
		final String reversePhrase = request.getParameter("reversePhrase");
		final int color = Integer.parseInt(request.getParameter("color"));
		ApplicationUser user=getCurrentUser(request);
		WebAction action = new WebAction();
		Project project = action.returnSelectedProject();
		SavedRelation newRelation =relationService.add(title, description, user.getUsername(), project.getKey(), phrase, reversePhrase ,color, false);
		String status="<tr id='"+newRelation.getID()+"'><td>"+title+"</td><td>"+description+"</td><td><span class='aui-lozenge aui-lozenge-"+Relation.colorBars[color]+"'>"+Relation.colorNames[color]+"</span></td><td><p>"+phrase+"</p><p>"+reversePhrase+"</p></td><td><button class='aui-button toggle-active' style='width:85%'>Off</button></td><td>0</td><td>"+user.getDisplayName()+"</td><td><button class='aui-button del-relation' ><i class='fa fa-trash-o' aria-hidden='true'></i></button></td></tr>";
        response.getWriter().write(status);
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException{
		UserProfile userProfile = userManager.getRemoteUser(request);
		if (userProfile == null) {
			redirectToLogin(request, response);
			return;
		}
		String status="success";
		String dId=request.getParameter("id"); 
    	if(dId!=null)
    		relationService.deleteMe(dId);
    	else
    		status="error";
		response.getWriter().write(status);
	}
	
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException{
		UserProfile userProfile = userManager.getRemoteUser(request);
		if (userProfile == null) {
			redirectToLogin(request, response);
			return;
		}
		String status="error";
		String dId=request.getParameter("id"); 
		String toggleString=request.getParameter("toggle");
		boolean toggle=false;
		if(toggleString!=null )
			toggle=true;
    	if(dId!=null && toggle==true){
    		relationService.toggleMe(dId);
    		status="success";
    	}
		response.getWriter().write(status);
	}

}