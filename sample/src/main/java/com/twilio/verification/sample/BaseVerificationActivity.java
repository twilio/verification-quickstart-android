package com.twilio.verification.sample;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twilio.verification.TwilioVerification;
import com.twilio.verification.external.Via;
import com.twilio.verification.sample.utils.KeyboardHelper;
import com.twilio.verification.sample.utils.MessageHelper;

/**
 * Created by jsuarez on 4/25/17.
 */

public abstract class BaseVerificationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, VerificationView {
    protected static final String TAG = VerificationActivity.class.getSimpleName();
    protected static final int PHONE_PICKER_RESULT_CODE = 1000;

    protected MessageHelper messageHelper;
    protected View rootView;
    protected EditText phoneNumber;
    protected VerificationPresenter verificationPresenter;

    private VerificationBrReceiver verificationBrReceiver;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageHelper = new MessageHelper();
        initData();
        initGoogleApiClient();
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        initBaseViews();
        initViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHONE_PICKER_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                Credential cred = data.getParcelableExtra(Credential.EXTRA_KEY);
                phoneNumber.setText(cred.getId());
                validateAndStartVerification(Via.SMS);
            } else {
                // Show keyboard
                phoneNumber.requestFocus();
                KeyboardHelper.showKeyboard(this, phoneNumber);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        verificationPresenter.attachView(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TwilioVerification.CURRENT_STATUS);
        registerReceiver(verificationBrReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        messageHelper.dismiss();
        verificationPresenter.detachView();
        unregisterReceiver(verificationBrReceiver);
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "GoogleApiClient Connected");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "GoogleApiClient is suspended with cause code: " + cause);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "GoogleApiClient failed to connect: " + connectionResult);
    }

    protected void validateAndStartVerification(Via via) {
        String phoneNumberText = phoneNumber.getText().toString();
        if (!Patterns.PHONE.matcher(phoneNumberText).matches()) {
            phoneNumber.setError(getString(R.string.invalid_phone_number));
            return;
        }

        verificationPresenter.startVerification(phoneNumberText, via);
    }

    protected void initData() {
        verificationPresenter = new VerificationPresenter(this);
        verificationBrReceiver = new VerificationBrReceiver(verificationPresenter);
    }

    protected void initBaseViews() {
        rootView = findViewById(android.R.id.content);
        phoneNumber = (EditText) findViewById(R.id.phone_number);


        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber.setError(null);
                String phoneNumberText = phoneNumber.getText().toString();
                if (TextUtils.isEmpty(phoneNumberText)) {
                    attemptToFetchPhoneNumber();
                }
            }
        });
    }

    private void initGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();
    }

    protected void showErrorMessage(String message) {
        KeyboardHelper.hideKeyboard(this);
        messageHelper.show(rootView, message);
    }

    protected void attemptToFetchPhoneNumber() {
        KeyboardHelper.hideKeyboard(this);
        CredentialPickerConfig credentialPickerConfig = new CredentialPickerConfig.Builder()
                .setShowCancelButton(true)
                .build();
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .setHintPickerConfig(credentialPickerConfig)
                .build();

        PendingIntent intent =
                Auth.CredentialsApi.getHintPickerIntent(googleApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), PHONE_PICKER_RESULT_CODE, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Could not start hint picker Intent", e);
        }
    }

    public VerificationPresenter getVerificationPresenter() {
        return verificationPresenter;
    }

    public void setVerificationPresenter(VerificationPresenter verificationPresenter) {
        this.verificationPresenter = verificationPresenter;
    }

    abstract protected void initViews();

    abstract public void updateView(VerificationUIModel verificationUIModel);
}
