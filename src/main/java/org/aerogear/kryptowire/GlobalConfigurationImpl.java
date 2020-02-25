package org.aerogear.kryptowire;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import jenkins.model.GlobalConfiguration;
import org.apache.commons.validator.routines.UrlValidator;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.logging.Logger;

@Extension
public final class GlobalConfigurationImpl extends GlobalConfiguration {
    private static final Logger LOGGER = Logger.getLogger(GlobalConfigurationImpl.class.getName());
    private String kwApiKey;
    private String kwEndpoint;

    public GlobalConfigurationImpl() {
        load();
    }

    public boolean isApplicable(Class<? extends AbstractProject> aClass) {
        return true;
    }

    @Override
    public String getGlobalConfigPage() {
        return super.getGlobalConfigPage();
    }

    @DataBoundConstructor
    public GlobalConfigurationImpl(String kwApiKey, String kwEndpoint) {
        super();
        load();

        this.setKwApiKey(kwApiKey);
        this.setKwEndpoint(kwEndpoint);
    }

    public String getKwApiKey() {
        return kwApiKey;
    }

    public void setKwApiKey(String kwApiKey) {
        this.kwApiKey = kwApiKey;
    }

    public String getKwEndpoint() {
        return kwEndpoint;
    }

    public void setKwEndpoint(String kwEndpoint) {
        this.kwEndpoint = kwEndpoint;
    }

    public FormValidation doCheckKwApiKey(@QueryParameter String value) throws IOException, ServletException {
        if (StringUtils.isEmpty(value)) {
            LOGGER.info("[info] Missing Kryptowire api key field");
            return FormValidation.error("You must provide a Kryptowire api key.");
        }
        LOGGER.info("[info] Kryptowire api key validated.");
        return FormValidation.ok();

    }

    public FormValidation doCheckKwEndpoint(@QueryParameter String value) throws IOException, ServletException {
        if (StringUtils.isEmpty(value)) {
            LOGGER.info("[info] Missing Kryptowire endpoint field");
            return FormValidation.error("You must provide a Kryptowire endpoint");
        }

        String[] schemes = { "http", "https" };
        UrlValidator urlValidator = new UrlValidator(schemes);

        if (!urlValidator.isValid(value)) {
            LOGGER.info("[info] Failed to validate the Kryptowire endpoint as a valid url string.");
            return FormValidation.error("Invalid Kryptowire endpoint format");
        }

        LOGGER.info("[info] Kryptowire endpoint field validated.");
        return FormValidation.ok();
    }

    @Override
    public String getDisplayName() {
        return "Kryptowire Global Config";
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
        req.bindJSON(this, formData);

        save();

        LOGGER.info("[info] Kryptowire global configuration updated.");

        return super.configure(req, formData);
    }
}
