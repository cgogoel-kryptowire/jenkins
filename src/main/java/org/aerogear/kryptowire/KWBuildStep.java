package org.aerogear.kryptowire;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.model.GlobalConfiguration;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class KWBuildStep extends Builder implements SimpleBuildStep {
    public String filePath;
    public String platform;

    @DataBoundConstructor
    public KWBuildStep(@Nonnull String platform, @Nonnull String filePath) {
        this.platform = platform;
        this.filePath = filePath;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws IOException, InterruptedException {

        PrintStream logger = listener.getLogger();

        GlobalConfigurationImpl pluginConfig = GlobalConfiguration.all().get(GlobalConfigurationImpl.class);

        if (pluginConfig == null) {
            throw new RuntimeException("[Error] Could not retrieve global Kryptowire config object.");
        }

        String kwEndpoint = pluginConfig.getKwEndpoint();
        String kwApiKey = pluginConfig.getKwApiKey();
        logger.println("kwEndpoint: " + kwEndpoint);
        logger.println("kwApiKey: " + kwApiKey);

        if(StringUtils.isEmpty(kwEndpoint) || StringUtils.isEmpty(kwApiKey)) {
            throw new RuntimeException("Kryptowire plugin configuration is not set!");
        }

        //FilePath fp = getContext().get(FilePath.class).child(this.filePath
        logger.println("Passed file path: " + filePath);
        FilePath fp = new FilePath(new File(filePath + "/" + this.filePath));

        logger.println(" --- Kryptowire submit Start ---");
        logger.println("kwSubmit: " + this.platform + " : " + this.filePath);

        KryptowireService kws = new KryptowireServiceImpl(kwEndpoint,  kwApiKey);
        logger.println("Service endpoint: " + ((KryptowireServiceImpl) kws).getApiEndpoint());

        JSONObject resp = kws.submit(this.platform, fp);

        String uuid = resp.getString("uuid");
        String platform = resp.getString("platform");
        String pkg = resp.getString("package");
        String version = resp.getString("version");
        String hash = resp.getString("hash");

        logger.println("kw msg: " + resp.get("msg"));
        logger.println("kw uuid: " + uuid);
        logger.println("kw platform: " + platform);
        logger.println("kw package: " + pkg);
        logger.println("kw version: " + version);
        logger.println("kw hash: " + hash);

        BinaryInfo info = BinaryInfo.fromJSONObject(resp);
        logger.println("Creating binary info: " + info.toString());

        run.addAction(new BinaryHistoryAction(info));

        logger.println(" --- Kryptowire submit Done ---");
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Submit to Kryptowire";
        }

    }

}
