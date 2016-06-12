package com.atlassian.jira.jira.webwork;

import java.util.Map;

import com.atlassian.jira.project.Project;
import com.atlassian.plugin.PluginParseException;

public class WebCondition implements com.atlassian.plugin.web.Condition {

	@Override
	public void init(Map<String, String> params) throws PluginParseException {
		// TODO Auto-generated method stub
		
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
