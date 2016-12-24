package co.miracleLab.jira.jira.webwork;

import java.util.Map;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.plugin.ProjectPermissionKey;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.PluginParseException;

public class WebConditionUserIsProjectAdmin implements com.atlassian.plugin.web.Condition {

	@Override
	public void init(Map<String, String> params) throws PluginParseException {
		
	}

	@Override
	public boolean shouldDisplay(Map<String, Object> context) {
		WebAction webAction= new WebAction();
		ApplicationUser user=webAction.getLoggedInUser();
		Project currentProject=webAction.returnSelectedProject();
		if(currentProject==null)
			return false;
		if(user==null)
			return false;
		PermissionManager permissionManager=ComponentAccessor.getPermissionManager();
		if (permissionManager.hasPermission(new ProjectPermissionKey("ADMINISTER_PROJECTS"), currentProject, user))
			  return true;
		return false;
	}

}
