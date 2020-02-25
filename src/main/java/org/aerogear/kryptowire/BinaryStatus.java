package org.aerogear.kryptowire;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BinaryStatus {
    private int score;
    private boolean complete;
    private String submittedAt;

    public BinaryStatus() {}

    public BinaryStatus(int score, boolean complete, String submittedAt) {
        this.score = score;
        this.complete = complete;
        this.submittedAt = submittedAt;
    }

    public static BinaryStatus fromJSONObject(JSONObject obj) {
        int score = obj.getInt("threat_score");
        boolean complete = obj.getBoolean("complete");
        String submittedAt = obj.getString("submitted_at");

        return new BinaryStatus(score, complete, submittedAt);
    }

    public static BinaryStatus notReady() {
        return new BinaryStatus(0, false, "");
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public String getSubmittedAt() throws ParseException {
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        Date d = dt.parse(submittedAt.replace("T", " "));
        return d.toString();
    }

    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }
}
