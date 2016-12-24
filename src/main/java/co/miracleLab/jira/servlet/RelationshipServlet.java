package co.miracleLab.jira.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.avatar.Avatar;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.atlassian.upm.api.license.entity.PluginLicense;

import co.miracleLab.jira.ao.RelationService;
import co.miracleLab.jira.ao.RelationshipService;
import co.miracleLab.jira.ao.SavedRelation;
import co.miracleLab.jira.ao.SavedRelationship;
import co.miracleLab.jira.model.Relation;
import co.miracleLab.jira.model.Relationship;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Named("RelationshipServlet")
public class RelationshipServlet extends HttpServlet{
    private static final Logger log = LoggerFactory.getLogger(RelationshipServlet.class);
    @ComponentImport
	private final UserManager userManager;
	@ComponentImport
	private final LoginUriProvider loginUriProvider;
	@ComponentImport
	private final TemplateRenderer templateRenderer;
	@ComponentImport
	private final ActiveObjects ao;
	private String baseUrl;
	private RelationService relationService;
	private RelationshipService relationshipService;
	@ComponentImport
	private final PluginLicenseManager licenseManager;
    
	@Inject
	public RelationshipServlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer templateRenderer,
			ActiveObjects ao, PluginLicenseManager licenseManager) {
		this.userManager = userManager;
		this.loginUriProvider = loginUriProvider;
		this.templateRenderer = templateRenderer;
		baseUrl=ComponentAccessor.getApplicationProperties().getString(APKeys.JIRA_BASEURL);
		this.ao=ao;
		relationService=new RelationService(ao);
		relationshipService=new RelationshipService(ao);
		this.licenseManager=licenseManager;
	}
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		UserProfile userProfile = userManager.getRemoteUser(request);
		if (userProfile == null) {
			redirectToLogin(request, response);
			return;
		}
		response.sendRedirect(baseUrl);
    }
	
	@Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		UserProfile userProfile = userManager.getRemoteUser(request);
		if (userProfile == null) {
			redirectToLogin(request, response);
			return;
		}
		try{
			final String relationshipId = request.getParameter("id");
	    	relationshipService.deleteMe(relationshipId);
	    	response.getWriter().write("success");
	    }catch (Exception e) {
			response.getWriter().write("error");
		}
    }
	
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
		UserProfile userProfile = userManager.getRemoteUser(request);
		if (userProfile == null) {
			redirectToLogin(request, response);
			return;
		}
		if(!LicenseCheck()){
			response.getWriter().write("licenseError");
			return;
		}
		try{
			Map<String, Object> context = new HashMap<String, Object>();
			final String projectKey = request.getParameter("title");
			final long issueId = Long.parseLong(request.getParameter("IssueId"));
			final String firstUser = request.getParameter("firstUser");
			final String secondUser = request.getParameter("secondUser");
			final String relationId = request.getParameter("relationId");
			
			SavedRelation relationRecord=relationService.findRelation(relationId);
			Relation relation;
			if(relationRecord==null){
				response.getWriter().write("error");
				return;
			}
				
			relation=new Relation(relationRecord);
			SavedRelationship newRelationship = relationshipService.add(issueId, projectKey, relation, new Date(), firstUser, secondUser);
			Relationship relationship=new Relationship(newRelationship, relationService);
			context.put("relationship", relationship);
			context.put("avatarSize", Avatar.Size.SMALL);
			context.put("avatarService", ComponentAccessor.getAvatarService());
			
			context.put("baseURL", baseUrl);
			templateRenderer.render("templates/aRelationship.vm", context, response.getWriter());
		}catch (Exception e) {
			response.getWriter().write("error");
		}
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
	
	private boolean LicenseCheck(){
		if (licenseManager.getLicense().isDefined()) {
			PluginLicense pluginLicense = licenseManager.getLicense().get();
			if (pluginLicense.getError().isDefined())
				return false;
			else
				return true;
		}else
		return false;
	}

}