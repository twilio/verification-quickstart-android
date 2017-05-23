package com.twilio.verification.sample.failure;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.twilio.verification.external.Via;
import com.twilio.verification.sample.BaseVerificationActivity;
import com.twilio.verification.sample.R;
import com.twilio.verification.sample.VerificationUIModel;
import com.twilio.verification.sample.success.VerificationSuccessfulActivity;
import com.twilio.verification.sample.utils.KeyboardHelper;
import com.twilio.verification.sample.views.ProgressBarButton;

public class VerificationFailedActivity extends BaseVerificationActivity {


    public static final String EXTRA_PHONE_NUMBER = "com.twilio.verify.sample.extra.phoneNumber";

    // Data
    private String phoneNumberText;

    // UI
    private EditText verificationCode;
    private TextView verifyTitle;
    private ProgressBarButton verifyViaSmsButton;
    private ProgressBarButton verifyViaCallButton;

    public static Intent getVerificationFailedIntent(Context context, String phoneNumber) {
        Intent intent = new Intent(context, VerificationFailedActivity.class);
        intent.putExtra(EXTRA_PHONE_NUMBER, phoneNumber);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_failed);
    }

    @Override
    protected void initData() {
        super.initData();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            phoneNumberText = extras.getString(EXTRA_PHONE_NUMBER);
        }
    }

    @Override
    protected void initViews() {
        verifyTitle = (TextView) findViewById(R.id.verify_title);
        verificationCode = (EditText) findViewById(R.id.verification_code);
        verifyViaSmsButton = (ProgressBarButton) findViewById(R.id.verify_via_sms_button);
        verifyViaCallButton = (ProgressBarButton) findViewById(R.id.verify_via_call_button);

        if (!TextUtils.isEmpty(phoneNumberText)) {
            phoneNumber.setText(phoneNumberText);
        }

        verifyViaSmsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndStartVerification(Via.SMS);
                KeyboardHelper.hideKeyboard(VerificationFailedActivity.this);
                verificationCode.setText("");
            }
        });

        verifyViaCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndStartVerification(Via.CALL);
                KeyboardHelper.hideKeyboard(VerificationFailedActivity.this);
                verificationCode.setText("");
            }
        });

        verificationCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() == 4) {
                    verificationPresenter.verificationPinEntered(s.toString());
                }
            }
        });
    }

    @Override
    public void updateView(VerificationUIModel verificationUIModel) {
        VerificationUIModel.State state = verificationUIModel.getCurrentState();

        switch (state) {
            case IN_PROGRESS:
                verifyTitle.setText(R.string.verify_title_label);
                verificationCode.setVisibility(View.VISIBLE);
                showSmsProgressBar(verificationUIModel.getVia() == Via.SMS);
                showCallProgressBar(verificationUIModel.getVia() == Via.CALL);
                break;
            case SUCCESS:
                verificationCode.setVisibility(View.GONE);
                showSmsProgressBar(false);
                showCallProgressBar(false);
                startActivity(VerificationSuccessfulActivity.getIntent(this));
                break;
            case ERROR:
                verifyTitle.setText(R.string.verify_failed_title_label);
                verificationCode.setVisibility(View.GONE);
                showSmsProgressBar(false);
                showCallProgressBar(false);
                showErrorMessage(verificationUIModel.getErrorMessage());
                break;
        }

    }

    private void showSmsProgressBar(boolean enable) {
        verifyViaSmsButton.showProgressBar(enable);
    }

    private void showCallProgressBar(boolean enable) {
        verifyViaCallButton.showProgressBar(enable);
    }
}
