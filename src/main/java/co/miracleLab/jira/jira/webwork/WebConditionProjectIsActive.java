package co.miracleLab.jira.jira.webwork;

import java.util.Map;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.PluginParseException;

public class WebConditionProjectIsActive implements com.atlassian.plugin.web.Condition {

	@Override
	public void init(Map<String, String> params) throws PluginParseException {
		
	}

	@Override
	public boolean shouldDisplay(Map<String, Object> context) {
		WebAction webAction= new WebAction();
		Project currentProject=webAction.returnSelectedProject();
		if(currentProject!=null)
			return true;
		return false;
	}

}
