package org.aerogear.kryptowire;

import hudson.FilePath;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public interface KryptowireService {
    enum AnalysisStatus {
        COMPLETE, PROCESSING, NOT_SUBMITTED
    }

    JSONObject submit(String platform, FilePath filePath, String externalId) throws IOException, InterruptedException;

    JSONObject getResult(String uuid) throws IOException, InterruptedException;

    AnalysisStatus getStatus(String hash) throws IOException, InterruptedException;

    boolean isCompleted(String hash) throws IOException, InterruptedException;

    void downloadReport(String hash, String type, File targetFile) throws IOException, InterruptedException;
}
