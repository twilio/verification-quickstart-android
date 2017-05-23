package com.twilio.verification.sample.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jsuarez on 3/24/17.
 */

public class TokenServerResponse {

    @SerializedName("jwt_token")
    private String jwtToken;

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
