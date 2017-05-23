package com.twilio.verification.sample.mocks;

import android.support.annotation.NonNull;
import android.util.Base64;

import com.twilio.verification.sample.network.TokenServerApi;
import com.twilio.verification.sample.network.TokenServerResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.Url;

/**
 * Created by jsuarez on 4/26/17.
 */

public class MockTokenServerApi implements TokenServerApi {

    private final boolean success;

    public MockTokenServerApi(boolean success) {
        this.success = success;
    }

    @NonNull
    public static String getMockTokenPayload(Calendar expirationDate) throws JSONException {
        JSONObject payload = new JSONObject();
        payload.put("exp", expirationDate.getTimeInMillis() / 1000);
        String encodedPayload = Base64.encodeToString(payload.toString().getBytes(Charset.forName("UTF-8")), Base64.DEFAULT);
        String fakeHeader = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        String fakeSignature = "TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ";
        StringBuilder jwt = new StringBuilder();
        jwt.append(fakeHeader);
        jwt.append(".");
        jwt.append(encodedPayload);
        jwt.append(".");
        jwt.append(fakeSignature);
        return jwt.toString();
    }

    @Override
    public Call<TokenServerResponse> getToken(@Url String url, @Field("phone_number") String phoneNumber) {
        return new Call<TokenServerResponse>() {
            @Override
            public Response<TokenServerResponse> execute() throws IOException {

                if (success) {
                    TokenServerResponse tokenServerResponse = null;
                    try {
                        tokenServerResponse = getTokenServerResponse();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return Response.success(tokenServerResponse);
                }

                throw new IOException("Fake error");
            }

            @Override
            public void enqueue(Callback<TokenServerResponse> callback) {
                if (success) {
                    try {
                        callback.onResponse(this, Response.success(getTokenServerResponse()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                callback.onFailure(this, new IOException("Fake error"));
            }

            @Override
            public boolean isExecuted() {
                return false;
            }

            @Override
            public void cancel() {

            }

            @Override
            public boolean isCanceled() {
                return false;
            }

            @Override
            public Call<TokenServerResponse> clone() {
                return null;
            }

            @Override
            public Request request() {
                return null;
            }
        };
    }

    @NonNull
    private TokenServerResponse getTokenServerResponse() throws JSONException {
        TokenServerResponse tokenServerResponse = new TokenServerResponse();
        tokenServerResponse.setJwtToken(getMockTokenPayload(Calendar.getInstance()));
        return tokenServerResponse;
    }
}
