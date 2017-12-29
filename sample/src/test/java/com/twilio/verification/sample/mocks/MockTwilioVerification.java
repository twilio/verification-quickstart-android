package com.twilio.verification.sample.mocks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.authy.commonandroid.external.TwilioException;
import com.twilio.verification.TwilioVerification;
import com.twilio.verification.external.VerificationException;
import com.twilio.verification.external.VerificationStatus;
import com.twilio.verification.external.Via;

/**
 * Created by jsuarez on 4/26/17.
 */

public class MockTwilioVerification extends TwilioVerification {
    private final VerificationStatus.State startState;
    private final VerificationStatus.State checkState;
    private final Context context;

    public MockTwilioVerification(@NonNull Context context, VerificationStatus.State startState, VerificationStatus.State checkState) {
        super(context);
        this.context = context;
        this.startState = startState;
        this.checkState = checkState;
    }

    @Override
    public void startVerification(@NonNull String jwtToken, @Nullable Via via) {
        VerificationStatus.Builder builder = new VerificationStatus.Builder(startState);
        if (startState == VerificationStatus.State.ERROR) {
            VerificationException verificationException = new VerificationException(new TwilioException("Mock exception", TwilioException.UNKNOWN_ERROR));
            builder.setVerificationException(verificationException);
        }
        context.sendBroadcast(builder.build().getIntent());
        return;
    }

    @Override
    public void checkVerificationPin(String pin) {
        VerificationStatus.Builder builder = new VerificationStatus.Builder(checkState);
        if (checkState == VerificationStatus.State.ERROR) {
            VerificationException verificationException = new VerificationException(new TwilioException("Mock exception", TwilioException.UNKNOWN_ERROR));
            builder.setVerificationException(verificationException);
        }
        context.sendBroadcast(builder.build().getIntent());
        return;
    }
}
