package ut.com.atlassian.jira.teamMap;

import org.junit.Test;
import com.atlassian.jira.teamMap.api.MyPluginComponent;
import com.atlassian.jira.teamMap.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}