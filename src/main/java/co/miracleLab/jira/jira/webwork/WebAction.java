package co.miracleLab.jira.jira.webwork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.action.JiraWebActionSupport;

public class WebAction extends JiraWebActionSupport
{
    private static final Logger log = LoggerFactory.getLogger(WebAction.class);

    @Override
    public String execute() throws Exception {

        return super.execute(); //returns SUCCESS
    }
    public Project returnSelectedProject(){
		return super.getSelectedProject();
    }
    
    public Issue returnSelectedIssue(){
    	JiraHelper jiraHelper = new JiraHelper(super.getHttpRequest());
    	Issue currentIssue = (Issue) jiraHelper.getContextParams().get("issue");
		return currentIssue;
    }
    
    public ApplicationUser returnUser(){
		return super.getLoggedInUser();
    }
}
