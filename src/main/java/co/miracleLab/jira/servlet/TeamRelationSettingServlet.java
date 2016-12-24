package co.miracleLab.jira.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
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

import co.miracleLab.jira.ao.RelationService;
import co.miracleLab.jira.ao.RelationshipService;
import co.miracleLab.jira.ao.SavedRelation;
import co.miracleLab.jira.jira.webwork.WebAction;
import co.miracleLab.jira.model.ItemException;
import co.miracleLab.jira.model.Relation;

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
	private RelationshipService relationshipService;

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
		
		List<Relation> relations=new ArrayList<Relation>();
		for(SavedRelation savedRelation : relationService.allinProject(project.getKey())){
			if(savedRelation.getMlajtpOwnerId().equals(userProfile.getUsername()) || savedRelation.isMlajtpShared()){
				Relation relation;
				try {
					relation = new Relation(savedRelation);
					relations.add(relation);
				} catch (ItemException e) {}
				
			}
    	}
		context.put("relations", relations);
		context.put("relationshipService", relationshipService);
		
		context.put("baseURL", baseUrl);
		context.put("mode", "setting");
		templateRenderer.render("templates/relationSetting.vm", context, response.getWriter());
    }
    	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		UserProfile userProfile = userManager.getRemoteUser(request);
		if (userProfile == null) {
			response.getWriter().write("error");
			return;
		}

		Map<String, Object> context = new HashMap<String, Object>();

		final String title = request.getParameter("title");
		final String description = request.getParameter("description");
		final String phrase = request.getParameter("phrase");
		final String reversePhrase = request.getParameter("reversePhrase");
		final int color = Integer.parseInt(request.getParameter("color"));
		
		ApplicationUser user=getCurrentUser(request);
		WebAction action = new WebAction();
		Project project = action.returnSelectedProject();
		
		if(title==null || description==null || phrase==null || reversePhrase==null
				|| user==null || project==null){
			response.getWriter().write("error");
			return;
		}
							
		SavedRelation newRelation =relationService.add(title, description, user, project, phrase, reversePhrase ,color, false);
		try {
			Relation relation=new Relation(newRelation);
			context.put("relation", relation);
			
			context.put("baseURL", baseUrl);
			templateRenderer.render("templates/relationRow.vm", context, response.getWriter());
		} catch (ItemException e) {
			response.getWriter().write("error");
			return;		
		}
		
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException{
		UserProfile userProfile = userManager.getRemoteUser(request);
		if (userProfile == null) {
			response.getWriter().write("error");
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
			response.getWriter().write("error");
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