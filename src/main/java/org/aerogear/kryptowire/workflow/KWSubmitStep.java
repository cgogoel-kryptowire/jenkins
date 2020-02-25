package org.aerogear.kryptowire.workflow;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.tasks.BuildStep;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.model.GlobalConfiguration;
import org.aerogear.kryptowire.BinaryInfo;
import org.aerogear.kryptowire.GlobalConfigurationImpl;
import org.aerogear.kryptowire.KryptowireService;
import org.aerogear.kryptowire.KryptowireServiceImpl;
import org.aerogear.kryptowire.BinaryHistoryAction;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;
import org.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import javax.inject.Inject;


public class KWSubmitStep extends AbstractStepImpl {


    private String platform;
    private String filePath;

    public String getPlatform() {
        return platform;
    }

    @DataBoundSetter
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getFilePath() {
        return filePath;
    }

    @DataBoundSetter
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @DataBoundConstructor
    public KWSubmitStep(@Nonnull String platform, @Nonnull String filePath) {
        this.platform = platform;
        this.filePath = filePath;
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(KWSubmitExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "kwSubmit";
        }

        @Override
        public String getDisplayName() {
            return "Submit to Kryptowire";
        }

    }

    public static class KWSubmitExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {

        private static final long serialVersionUID = 1L;

        @Inject
        transient KWSubmitStep step;

        @StepContextParameter
        transient TaskListener listener;

        @StepContextParameter
        private transient Run build;

        @Override
        protected Void run() throws Exception {

            GlobalConfigurationImpl pluginConfig = GlobalConfiguration.all().get(GlobalConfigurationImpl.class);

            if (pluginConfig == null) {
                throw new RuntimeException("[Error] Could not retrieve global Kryptowire config object.");
            }

            String kwEndpoint = pluginConfig.getKwEndpoint();
            String kwApiKey = pluginConfig.getKwApiKey();

            if(StringUtils.isEmpty(kwEndpoint) || StringUtils.isEmpty(kwApiKey)) {
                throw new RuntimeException("Kryptowire plugin configuration is not set!");
            }

            FilePath fp = getContext().get(FilePath.class).child(step.filePath);

            listener.getLogger().println(" --- Kryptowire submit Start ---");
            listener.getLogger().println("kwSubmit: " + step.platform + " : " + step.filePath);

            KryptowireService kws = new KryptowireServiceImpl(kwEndpoint,  kwApiKey);

            JSONObject resp = kws.submit(step.platform, fp);

            String uuid = resp.getString("uuid");
            String platform = resp.getString("platform");
            String pkg = resp.getString("package");
            String version = resp.getString("version");
            String hash = resp.getString("hash");

            listener.getLogger().println("kw msg: " + resp.get("msg"));
            listener.getLogger().println("kw uuid: " + uuid);
            listener.getLogger().println("kw platform: " + platform);
            listener.getLogger().println("kw package: " + pkg);
            listener.getLogger().println("kw version: " + version);
            listener.getLogger().println("kw hash: " + hash);

            BinaryInfo info = BinaryInfo.fromJSONObject(resp);
            build.addAction(new BinaryHistoryAction(info));

            listener.getLogger().println(" --- Kryptowire submit Done ---");

            return null;
        }

    }
}
