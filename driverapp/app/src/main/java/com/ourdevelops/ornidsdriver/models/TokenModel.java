package com.ourdevelops.ornidsdriver.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.realm.RealmObject;
public class TokenModel extends RealmObject implements Serializable {

    @Expose
    @SerializedName("token_price")
    private String token_price;

    @Expose
    @SerializedName("using_date")
    private String using_date;

    public String getToken_price() {
        return token_price;
    }

    public void setToken_price(String token_price) {
        this.token_price = token_price;
    }

    public String getUsing_date() {
        return using_date;
    }

    public void setUsing_date(String using_date) {
        this.using_date = using_date;
    }
}
