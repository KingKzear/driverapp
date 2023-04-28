package com.ourdevelops.ornidsdriver.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenResponseJson {
    @SerializedName("token")
    @Expose
    private String token;


    @SerializedName("ping")
    @Expose
    private String ping;

    public void setToken(String token) {
        this.token = token;
    }
    public String getToken() {
        return token;
    }

    public void setPing(String ping) {
        this.ping = ping;
    }

    public String getPing() {
        return ping;
    }
}
