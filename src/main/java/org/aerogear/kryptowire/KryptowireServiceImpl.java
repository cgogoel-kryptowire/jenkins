package org.aerogear.kryptowire;

import hudson.FilePath;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.auth.AuthScope;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.config.RequestConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import hudson.ProxyConfiguration;
import jenkins.model.Jenkins;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.BasicCredentialsProvider;

import java.io.*;

import java.util.logging.Logger;

public class KryptowireServiceImpl implements KryptowireService {

    private static final Logger logger = Logger.getLogger(KryptowireServiceImpl.class.getName());

    private String apiKey;
    private String apiEndpoint;
    private HttpClient httpclient;

    public KryptowireServiceImpl(String apiEndpoint, String apiKey, HttpClient httpclient)  {
        this.apiEndpoint = apiEndpoint;
        this.apiKey = apiKey;

        if (Jenkins.getInstance() != null && Jenkins.getInstance().proxy != null) {
            // Build the HTTP Client
            // Setup proxy if needed
            final ProxyConfiguration proxy = Jenkins.getInstance().proxy;
            logger.info("Using proxy configuration from Jenkins for host name: " + proxy.name);

            // Basic auth
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(proxy.name, proxy.port),
                    new UsernamePasswordCredentials(proxy.getUserName(), proxy.getPassword()));

            // Create the client builder
            HttpClientBuilder clientbuilder = HttpClients.custom();

            // Set the credentials
            clientbuilder = clientbuilder.setDefaultCredentialsProvider(credsProvider);


            // Build the client
            this.httpclient = clientbuilder.build();
        } else {
            this.httpclient = httpclient;
        }

        if (this.apiEndpoint != null) {
            if (!this.apiEndpoint.isEmpty() && !this.apiEndpoint.endsWith("/")) {
                this.apiEndpoint += "/";
            }
        }
    }

    public KryptowireServiceImpl(String apiEndpoint, String apiKey)  {
        this(apiEndpoint, apiKey, HttpClients.createDefault());
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }


    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    @Override
    public JSONObject submit(String platform, FilePath filePath, String externalId) throws IOException, InterruptedException {
        String endPointUrl = getApiEndpoint() + "/api/submit";

        RequestConfig config = this.getProxyConfig();
        HttpPost post = new HttpPost(endPointUrl);
        post.setConfig(config);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("app", filePath.read(), ContentType.APPLICATION_OCTET_STREAM, filePath.getName());
        builder.addTextBody("key", getApiKey());
        builder.addTextBody("platform", platform);
        builder.addTextBody("externalId", externalId);

        HttpEntity entity = builder.build();
        post.setEntity(entity);

        ResponseHandler<String> handler = new BasicResponseHandler();
        String responseBody = httpclient.execute(post, handler);
        return new JSONObject(responseBody);
    }

    @Override
    public AnalysisStatus getStatus(String hash) throws IOException, InterruptedException {
        String endPointUrl = getApiEndpoint() + "/api/status?key=" + getApiKey() + "&hash=" + hash;
        System.out.println("Getting status of application: " + hash);

        RequestConfig config = this.getProxyConfig();
        HttpGet get = new HttpGet(endPointUrl);
        get.setConfig(config);

        ResponseHandler<String> handler = new BasicResponseHandler();
        String responseBody = httpclient.execute(get, handler);

        JSONObject res = new JSONObject(responseBody);

        String status = res.getString("status");

        System.out.println("Status returned: " + status);

        if (status.equals("processing")) {
            return AnalysisStatus.PROCESSING;
        }

        if (status.equals("not_submitted")) {
            return AnalysisStatus.NOT_SUBMITTED;
        }

        return AnalysisStatus.COMPLETE;
    }

    @Override
    public JSONObject getResult(String uuid) throws IOException, InterruptedException {
        String endPointUrl = getApiEndpoint() + "/api/submitted-apps?key=" + getApiKey();
        System.out.println("Getting results for app: " + uuid);

        RequestConfig config = this.getProxyConfig();
        HttpGet get = new HttpGet(endPointUrl);
        get.setConfig(config);

        ResponseHandler<String> handler = new BasicResponseHandler();
        String responseBody = httpclient.execute(get, handler);

        JSONArray arr = new JSONArray(responseBody);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject item = arr.getJSONObject(i);
            if (item.isNull("uuid")) {
                continue;
            }

            if (item.getString("uuid").equals(uuid)) {
                return item;
            }
        }

        return new JSONObject("{}");
    }

    @Override
    public void downloadReport(String hash, String type, File targetFile) throws IOException, InterruptedException {
        String endPointUrl = getApiEndpoint() + "/api/results/" + type + "?key=" + getApiKey() + "&hash=" + hash;
        System.out.println("Getting report, target file: " + targetFile);
        System.out.println("Type: " + type);
        System.out.println("Endpoint: " + endPointUrl);

        RequestConfig config = this.getProxyConfig();
        HttpGet get = new HttpGet(endPointUrl);
        get.setConfig(config);

        HttpResponse response = httpclient.execute(get);

        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() >= 300) {
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            FileUtils.copyInputStreamToFile(entity.getContent(), targetFile);
        }
    }

    public boolean isCompleted(String hash) throws IOException, InterruptedException {
        return this.getStatus(hash) == AnalysisStatus.COMPLETE;
    }

    private RequestConfig getProxyConfig() {
        if (Jenkins.getInstance() == null || Jenkins.getInstance().proxy == null) {
            return null;
        }
        final ProxyConfiguration proxy = Jenkins.getInstance().proxy;
        logger.info("Setting up proxy for network request for name: " + proxy.name);

        // Create the target and proxy hosts
        HttpHost proxyHost = new HttpHost(proxy.name, proxy.port);

        // Setting the proxy
        RequestConfig.Builder reqconfigconbuilder = RequestConfig.custom();
        reqconfigconbuilder = reqconfigconbuilder.setProxy(proxyHost);
        RequestConfig config = reqconfigconbuilder.build();

        return config;
    }
}
