package org.aerogear.kryptowire;

import hudson.FilePath;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;

public class KryptowireServiceTest extends Mockito {

    String endpoint = "http://endpoint";
    String token = "12345";

    public KryptowireServiceTest() {}

    public String getDummyFile(String path) throws Exception {
        return IOUtils.toString(this.getClass().getResource(path), "UTF-8");
    }

    @Test
    public void submitTest() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);

        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpClient.execute(any(HttpPost.class), any(ResponseHandler.class)))
                .thenReturn(getDummyFile("/data/submit.json"));

        KryptowireServiceImpl kws = new KryptowireServiceImpl(endpoint, token, httpClient);
        FilePath apkPath = new FilePath(new File(this.getClass()
                .getResource("/myapp-fake.apk").getFile()));
        JSONObject res = kws.submit("android", apkPath);

        Assert.assertEquals("android", res.getString("platform"));
        Assert.assertEquals("911dafa8-2920-4478-8050-65d7c094efa7",
                res.getString("uuid"));
    }

    @Test
    public void getResultTest() throws  Exception {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);

        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpClient.execute(any(HttpGet.class), any(ResponseHandler.class)))
                .thenReturn(getDummyFile("/data/results.json"));

        KryptowireServiceImpl kws = new KryptowireServiceImpl(endpoint, token, httpClient);
        JSONObject res = kws.getResult("911dafa8-2920-4478-8050-65d7c094efa7");
        Assert.assertEquals(40, res.getInt("threat_score"));
    }

    @Test
    public void getResultErrorTest() throws  Exception {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);

        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpClient.execute(any(HttpGet.class), any(ResponseHandler.class)))
                .thenReturn(getDummyFile("/data/results.json"));

        KryptowireServiceImpl kws = new KryptowireServiceImpl(endpoint, token, httpClient);
        JSONObject res = kws.getResult("invalid-uuid");
        Assert.assertEquals(res.toString(), "{}");
    }

    @Test
    public void checkCompletedTest() throws  Exception {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);

        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpClient.execute(any(HttpGet.class), any(ResponseHandler.class)))
                .thenReturn(getDummyFile("/data/status-complete.json"));

        KryptowireServiceImpl kws = new KryptowireServiceImpl(endpoint, token, httpClient);
        boolean res = kws.isCompleted("911dafa8-2920-4478-8050-65d7c094efa7");
        Assert.assertEquals(true, res);
    }

    @Test
    public void checkProcessingTest() throws  Exception {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);

        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpClient.execute(any(HttpGet.class), any(ResponseHandler.class)))
                .thenReturn(getDummyFile("/data/status-processing.json"));

        KryptowireServiceImpl kws = new KryptowireServiceImpl(endpoint, token, httpClient);
        boolean res = kws.isCompleted("911dafa8-2920-4478-8050-65d7c094efa7");
        Assert.assertEquals(false, res);
    }

    @Test
    public void checkNotSubmittedTest() throws  Exception {
        HttpClient httpClient = mock(HttpClient.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);

        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(httpClient.execute(any(HttpGet.class), any(ResponseHandler.class)))
                .thenReturn(getDummyFile("/data/status-not_submitted.json"));

        KryptowireServiceImpl kws = new KryptowireServiceImpl(endpoint, token, httpClient);
        boolean res = kws.isCompleted("911dafa8-2920-4478-8050-65d7c094efa7");
        Assert.assertEquals(false, res);
    }
}
