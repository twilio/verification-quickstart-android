package com.twilio.verification.sample.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by jsuarez on 3/24/17.
 */

public interface TokenServerApi {

    @POST()
    @FormUrlEncoded
    Call<TokenServerResponse> getToken(@Url String url, @Field("phone_number") String phoneNumber);
}
