package com.ourdevelops.ornidsdriver.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PincodeRequestJson {

    @SerializedName("pinId")
    @Expose
    private String pinId;

    @SerializedName("phone_number")
    @Expose
    private String notelepon;

    @SerializedName("token_price")
    @Expose
    private String token_price;

    public String getNotelepon() {
        return notelepon;
    }

    public void setNotelepon(String notelepon) {
        this.notelepon = notelepon;
    }
    public void setPinid(String pinId) {
        this.pinId = pinId;
    }
    public String getPinid() {
        return pinId;
    }

    public String getToken_price() {
        return token_price;
    }

    public void setToken_price(String token_price) {
        this.token_price = token_price;
    }
}
