package com.twilio.verification.sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.auth0.android.jwt.JWT;
import com.twilio.verification.TwilioVerification;
import com.twilio.verification.external.VerificationStatus;
import com.twilio.verification.external.Via;
import com.twilio.verification.sample.network.TokenServerApi;
import com.twilio.verification.sample.network.TokenServerResponse;
import com.twilio.verification.sample.settings.SettingsActivity;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by jsuarez on 3/23/17.
 */

public class VerificationPresenter {

    private final Context context;
    private VerificationView view;
    private TwilioVerification twilioVerification;
    private TokenServerApi tokenServerApi;
    private Via via;
    private JWT jwtToken;

    public VerificationPresenter(Context context) {
        twilioVerification = new TwilioVerification(context);
        this.context = context;

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        HttpUrl tokenEndpoint = getTokenEndpoint(context);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                // A baseUrl is required although we pass the complete url when calling the endpoint
                .baseUrl(tokenEndpoint.scheme() + "://" + tokenEndpoint.host() + ":" + tokenEndpoint.port())
                .client(httpClient.build())
                .build();

        tokenServerApi = retrofit.create(TokenServerApi.class);
    }

    private HttpUrl getTokenEndpoint(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String string = sharedPref.getString(SettingsActivity.TOKEN_ENDPOINT_PREF, context.getString(R.string.default_endpoint));
        return HttpUrl.parse(string);
    }

    public void attachView(VerificationView phoneVerificationView) {
        this.view = phoneVerificationView;
    }

    public void detachView() {
        this.view = null;
    }

    private boolean isViewAttached() {
        return view != null;
    }


    private void updateState(VerificationStatus verificationStatus) {
        VerificationStatus.State state = verificationStatus.getState();
        switch (state) {
            case STARTED:
            case AWAITING_VERIFICATION:
                view.updateView(VerificationUIModel.inProgress(via));
                break;
            case SUCCESS:
                view.updateView(VerificationUIModel.success());
                break;
            case ERROR:
                view.updateView(VerificationUIModel.failure(verificationStatus.getVerificationException().getMessage()));
                break;
        }
    }

    public void startVerification(String phoneNumber, final Via via) {
        this.via = via;
        view.updateView(VerificationUIModel.inProgress(via));

        if (jwtToken == null || jwtToken.isExpired(0)) {
            getTokenAndStartVerification(phoneNumber, via);
        } else {
            twilioVerification.startVerification(
                    jwtToken.toString(),
                    via);
        }
    }

    private void getTokenAndStartVerification(final String phoneNumber, final Via via) {
        tokenServerApi.getToken(getTokenEndpoint(context).toString(), phoneNumber).enqueue(new Callback<TokenServerResponse>() {
            @Override
            public void onResponse(Call<TokenServerResponse> call, Response<TokenServerResponse> tokenServerResponse) {
                if (tokenServerResponse != null && tokenServerResponse.body() != null) {
                    jwtToken = new JWT(tokenServerResponse.body().getJwtToken());
                    twilioVerification.startVerification(
                            jwtToken.toString(),
                            via);
                    return;
                }

                onFailure(null, new Exception("Invalid server response"));
            }

            @Override
            public void onFailure(Call<TokenServerResponse> call, Throwable exception) {
                if (exception != null && isViewAttached()) {
                    view.updateView(VerificationUIModel.failure(exception.getMessage()));
                }
            }
        });
    }

    public void verificationStatusUpdated(VerificationStatus verificationStatus) {
        if (isViewAttached()) {
            updateState(verificationStatus);
        }
    }

    public void verificationPinEntered(String verificationPin) {
        twilioVerification.checkVerificationPin(verificationPin);
    }

    public TwilioVerification getTwilioVerification() {
        return twilioVerification;
    }

    public void setTwilioVerification(TwilioVerification twilioVerification) {
        this.twilioVerification = twilioVerification;
    }

    public TokenServerApi getTokenServerApi() {
        return tokenServerApi;
    }

    public void setTokenServerApi(TokenServerApi tokenServerApi) {
        this.tokenServerApi = tokenServerApi;
    }
}
