package org.aerogear.kryptowire;

import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.util.logging.Logger;

public class GlobalConfigurationImplTest {
    @Rule public JenkinsRule j = new JenkinsRule();
    private static Logger log = Logger.getLogger(GlobalConfigurationImplTest.class.getName());

    public GlobalConfigurationImplTest() {}

    @Test
    public void ShouldAssertDevaultValues() throws  Exception {
        HtmlPage configPage = j.createWebClient().goTo("configure");
        HtmlForm form = configPage.getFormByName("config");
        String urlFieldValue = form.getInputByName("_.kwEndpoint").getValueAttribute();
        String keyFieldValue = form.getInputByName("_.kwApiKey").getValueAttribute();
        assertEquals("", urlFieldValue);
        assertEquals("", keyFieldValue);
    }

    @Test
    public void ShouldSaveApiToken() throws  Exception {
        HtmlPage configPage = j.createWebClient().goTo("configure");
        HtmlForm form = configPage.getFormByName("config");
        form.getInputByName("_.kwApiKey").setValueAttribute("12345");
        HtmlButton button = (HtmlButton)configPage.getElementById("yui-gen10-button");
        button.click();
        configPage.refresh();
        assertEquals("12345", form.getInputByName("_.kwApiKey").getValueAttribute());
    }

    @Test
    public void ShouldUpdateAnalysisUrl() throws Exception {
        HtmlPage configPage = j.createWebClient().goTo("configure");
        HtmlForm form = configPage.getFormByName("config");
        form.getInputByName("_.kwEndpoint").setValueAttribute("https://myendpoint.kryptowire.com");
        HtmlButton button = (HtmlButton)configPage.getElementById("yui-gen10-button");
        button.click();
        configPage.refresh();
        assertEquals("https://myendpoint.kryptowire.com", form.getInputByName("_.kwEndpoint").getValueAttribute());
    }
}
