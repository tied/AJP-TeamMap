package co.miracleLab.jira.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.bc.JiraServiceContext;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.filter.SearchRequestService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.bc.user.search.AssigneeService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.search.SearchRequestManager;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.plugin.ProjectPermissionKey;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.query.Query;
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
import co.miracleLab.jira.model.TeamMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
   	private SearchRequestService searchRequestService;
   	
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
		searchRequestService=ComponentAccessor.getComponent(SearchRequestService.class);
	}
   	
   	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		UserProfile userProfile = userManager.getRemoteUser(request);
		if (userProfile == null) {
			response.getWriter().write("error");
			return;
		}
		ApplicationUser user=getCurrentUser(request);
		String jqlQuery = request.getParameter("query");
		SearchService.ParseResult parseResult =searchService.parseQuery(user, jqlQuery);
		
		String status="error";
		if (parseResult.isValid()) {
			status="ok";
			Query query = parseResult.getQuery();
			request.getSession().setAttribute("requestedQuery", query);
		}
		response.getWriter().write(status);
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
		
		ApplicationUser user=getCurrentUser(request);
		String selectedFilterParam=request.getParameter("filter");
		String selectedRelationParam=request.getParameter("relation");
		String jqlMode=request.getParameter("jql-mode");
		
		Query query =null;
		if(jqlMode!=null){
			Object queryObject= request.getSession().getAttribute("requestedQuery");
			if(queryObject!=null){
				query=(Query) queryObject;
				context.put("requestedQuery", query);
			}			
		}
		
		SearchRequest selectedFilter=null;
		if(selectedFilterParam!=null){
			JiraServiceContext ctx = new JiraServiceContextImpl(user);
			selectedFilter=searchRequestService.getFilter(ctx, Long.parseLong(selectedFilterParam));
			if(selectedFilter!=null)
				context.put("selectedFilter", selectedFilter);
		}
		
		TeamMap teamMap=null;
		if(selectedFilter!=null){
			teamMap=new TeamMap(selectedFilter,searchService, relationService, relationshipService);
			try {
				teamMap.initiate();
			} catch (SearchException e) {
				teamMap=null;
			} catch (ItemException e) {
				teamMap=null;
			}
		} else if(query!=null){
			teamMap=new TeamMap(query,searchService, relationService, relationshipService);
			try {
				teamMap.initiate();
			} catch (SearchException e) {
				teamMap=null;
			} catch (ItemException e) {
				teamMap=null;
			}
		}
		
		if(teamMap!=null){
			context.put("issuesCount", teamMap.getIssuesCount());
			context.put("totalRelationshipsCount", teamMap.getTotalRelationShipsCount());
			context.put("filterRelationBar", teamMap.getFilterRelationBar());
			context.put("relations", teamMap.getRelations());
			context.put("relationOccurence", teamMap.getRelationOccurence());
		}
		
		Relation selectedRelation=null;
		if(teamMap!=null && selectedRelationParam!=null){
			try {
				selectedRelation= new Relation(relationService.findRelation(selectedRelationParam));
			} catch (ItemException e) {
				selectedRelation=null;
			}
		}
		
		if(selectedRelation!=null){
			context.put("selectedRelation", selectedRelation);
			teamMap.setRelation(selectedRelation);
			teamMap.generateRelationships();
			context.put("relationUserBar",teamMap.getRelationUserBar());
		} else if (teamMap!=null) {
			if (teamMap.getRelations() != null) {
				if (teamMap.getRelations().size() > 0) {
					selectedRelation = teamMap.getRelations().get(0);
					context.put("selectedRelation", selectedRelation);
					teamMap.setRelation(selectedRelation);
					teamMap.generateRelationships();
					context.put("relationUserBar", teamMap.getRelationUserBar());
				}
			}
		}
		
		if(teamMap!=null){
			if(teamMap.getRelationships()!=null && teamMap.getUserList()!=null){
				teamMap.generateEmptyMap();
				teamMap.fillMap();
				context.put("userList", teamMap.getUserList());
				context.put("teamMap", teamMap.getTeamMap());
			}
		}
		
		WebAction action=new WebAction();
		Project project = action.returnSelectedProject();
		context.put("project", project);
		
		List<SearchRequest> filters=getProjectFilters(project);
		
		
		context.put("filters", filters);
		
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
	
	private List<ApplicationUser> getProjectAssignableUsers(Project project) {
		AssigneeService assigneeService = ComponentAccessor.getComponent(AssigneeService.class);
		Collection<ApplicationUser> assignableUsers=assigneeService.findAssignableUsers("", project);
		List<ApplicationUser> assignableUsersList=new ArrayList<ApplicationUser>();
		if(assignableUsers!=null)
			assignableUsersList=new ArrayList<ApplicationUser>(assignableUsers);
		return assignableUsersList;
	}
	 private List<SearchRequest> getProjectFilters(Project project){
		 SearchRequestService searchRequestService=ComponentAccessor.getComponent(SearchRequestService.class);
			List<ApplicationUser> projectUsers=getProjectAssignableUsers(project);
			List<SearchRequest> filters=new ArrayList<SearchRequest>();
			for(ApplicationUser appUser : projectUsers){
				Collection<SearchRequest> userFilters=searchRequestService.getNonPrivateFilters(appUser);
				if(userFilters!=null)
					filters.addAll(userFilters);
			}
		return filters;
	 }

}