package org.aerogear.kryptowire;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

public class BinaryInfo {
    private String pkg;
    private String uuid;
    private String platform;
    private String version;
    private String hash;

    public BinaryInfo(String pkg, String uuid, String platform, String version, String hash) {
        this.pkg = pkg;
        this.uuid = uuid;
        this.platform = platform;
        this.version = version;
        this.hash = hash;
    }

    public static BinaryInfo fromJSONObject(JSONObject obj) {
        String uuid = obj.getString("uuid");
        String platform = obj.getString("platform");
        String pkg = obj.getString("package");
        String version = obj.getString("version");
        String hash = obj.getString("hash");

        return new BinaryInfo(pkg, uuid, platform, version, hash);
    }


    public String getPkg() {
        return pkg;
    }

    public void setPkg(String pkg) {
        this.pkg = pkg;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return getPkg() + ", " + getVersion() + ", " + getUuid();
    }
}

