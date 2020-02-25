package org.aerogear.kryptowire.org.aerogear.kryptowire.integration;

import hudson.EnvVars;
import hudson.slaves.EnvironmentVariablesNodeProperty;
import org.aerogear.kryptowire.GlobalConfigurationImpl;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.*;
import org.jvnet.hudson.test.BuildWatcher;
import org.jvnet.hudson.test.JenkinsRule;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.jenkinsci.plugins.workflow.job.*;
import org.omg.CORBA.Environment;
import sun.net.www.http.HttpClient;

public class KWSubmitStepTest extends Mockito {
    @ClassRule public static BuildWatcher bw = new BuildWatcher();

    @Rule public JenkinsRule j = new JenkinsRule();

    String endpoint = System.getenv("KW_ENDPOINT");
    String token = System.getenv("KW_API_KEY");
    String platform = System.getenv("KW_PLATFORM");
    String binaryPath = System.getenv("KW_BINARY_PATH");

    public KWSubmitStepTest() {}

    public String getDummyFile(String path) throws Exception {
        return IOUtils.toString(this.getClass().getResource(path), "UTF-8");
    }

    /* @Test
    public void shouldRunSubmitFuncionWithSuccess() throws Exception {
        WorkflowJob project = j.createProject(WorkflowJob.class);
        project.setDefinition(new CpsFlowDefinition(getDummyFile("/data/Jenkinsfile.groovy")));
        j.jenkins.getDescriptorByType(GlobalConfigurationImpl.class).setKwApiKey(token);
        j.jenkins.getDescriptorByType(GlobalConfigurationImpl.class).setKwEndpoint(endpoint);
        j.jenkins.getDescriptorByType(GlobalConfigurationImpl.class).save();

        EnvironmentVariablesNodeProperty prop = new EnvironmentVariablesNodeProperty();
        EnvVars envVars = prop.getEnvVars();
        envVars.put("KW_PLATFORM", platform);
        envVars.put("KW_BINARY_PATH", binaryPath);
        j.jenkins.getGlobalNodeProperties().add(prop);

        WorkflowRun build = j.buildAndAssertSuccess(project);
    }*/
}
