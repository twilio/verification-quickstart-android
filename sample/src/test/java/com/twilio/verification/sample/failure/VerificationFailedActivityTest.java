package com.twilio.verification.sample.failure;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.twilio.verification.external.VerificationStatus;
import com.twilio.verification.sample.R;
import com.twilio.verification.sample.mocks.MockTokenServerApi;
import com.twilio.verification.sample.mocks.MockTwilioVerification;
import com.twilio.verification.sample.success.VerificationSuccessfulActivity;
import com.twilio.verification.sample.views.ProgressBarButton;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowIntent;
import org.w3c.dom.Text;

import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by jsuarez on 12/28/17.
 */
@RunWith(RobolectricTestRunner.class)
public class VerificationFailedActivityTest {
    private VerificationFailedActivity verificationFailedActivity;

    @Before
    public void setUp() throws Exception {
        verificationFailedActivity = Robolectric.setupActivity(VerificationFailedActivity.class);
        verificationFailedActivity.getVerificationPresenter().setTwilioVerification(new MockTwilioVerification(verificationFailedActivity, VerificationStatus.State.AWAITING_VERIFICATION, VerificationStatus.State.ERROR));
        verificationFailedActivity.getVerificationPresenter().setTokenServerApi(new MockTokenServerApi(true));
    }

    @Test
    public void testPhoneNumberValidationSMS() {

        // Test entering invalid phone number
        TextView phoneNumberView = (TextView) verificationFailedActivity.findViewById(R.id.phone_number);
        phoneNumberView.setText("1 234 567 8900+");

        // Click verify via SMS
        ProgressBarButton verifyViaSMSButton = (ProgressBarButton) verificationFailedActivity.findViewById(R.id.verify_via_sms_button);
        verifyViaSMSButton.getButton().performClick();

        // Assert correct error is displayed
        assertEquals(phoneNumberView.getError().toString(), verificationFailedActivity.getString(R.string.invalid_phone_number));

    }

    @Test
    public void testPhoneNumberValidationCall() {

        // Test entering invalid phone number
        TextView phoneNumberView = (TextView) verificationFailedActivity.findViewById(R.id.phone_number);
        phoneNumberView.setText("1 234 567 8900+");

        // Click verify via SMS
        ProgressBarButton verifyViaCallButton = (ProgressBarButton) verificationFailedActivity.findViewById(R.id.verify_via_call_button);
        verifyViaCallButton.getButton().performClick();

        // Assert correct error is displayed
        assertEquals(phoneNumberView.getError().toString(), verificationFailedActivity.getString(R.string.invalid_phone_number));
    }

    @Test
    public void testsSMSSuccessfulTransition() {
        successTransitionForButton(R.id.verify_via_sms_button);
    }

    @Test
    public void testCallSuccessTransition() {
        successTransitionForButton(R.id.verify_via_call_button);
    }

    @Test
    public void testCallErrorTransition() {
        errorTransitionForButton(R.id.verify_via_call_button);
    }

    @Test
    public void testSmsErrorTransition() {
        errorTransitionForButton(R.id.verify_via_sms_button);
    }

    private void errorTransitionForButton(int buttonId) {
        // Set error mocks
        verificationFailedActivity.getVerificationPresenter().setTwilioVerification(new MockTwilioVerification(verificationFailedActivity, VerificationStatus.State.AWAITING_VERIFICATION, VerificationStatus.State.ERROR));

        // Input correct number
        TextView phoneNumberView = (TextView) verificationFailedActivity.findViewById(R.id.phone_number);
        phoneNumberView.setText("12345678900");

        // Click verify button
        ProgressBarButton verifyViaCallButton = (ProgressBarButton) verificationFailedActivity.findViewById(buttonId);
        verifyViaCallButton.getButton().performClick();

        // Enter verification code
        TextView verificationCodeView = (TextView) verificationFailedActivity.findViewById(R.id.verification_code);
        verificationCodeView.setText("1234");

        // Assert that we remain on the error activity and that an error message is displayed
        Intent startedIntent = shadowOf(verificationFailedActivity).getNextStartedActivity();
        assertNull(startedIntent);
        TextView title = (TextView) verificationFailedActivity.findViewById(R.id.verify_title);

        assertEquals(title.getText().toString(), verificationFailedActivity.getText(R.string.verify_failed_title_label).toString());
        assertEquals(verificationCodeView.getVisibility(), View.GONE);
    }

    private void successTransitionForButton(int buttonId) {
        // Set success mocks
        verificationFailedActivity.getVerificationPresenter().setTwilioVerification(new MockTwilioVerification(verificationFailedActivity, VerificationStatus.State.AWAITING_VERIFICATION, VerificationStatus.State.SUCCESS));

        // Input correct number
        TextView phoneNumberView = (TextView) verificationFailedActivity.findViewById(R.id.phone_number);
        phoneNumberView.setText("12345678900");

        // Click verify button
        ProgressBarButton verifyViaCallButton = (ProgressBarButton) verificationFailedActivity.findViewById(buttonId);
        verifyViaCallButton.getButton().performClick();

        // Enter verification code
        TextView verificationCodeView = (TextView) verificationFailedActivity.findViewById(R.id.verification_code);
        verificationCodeView.setText("1234");

        // Assert that the correct activity was launched
        Intent startedIntent = shadowOf(verificationFailedActivity).getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);
        assertEquals(VerificationSuccessfulActivity.class, shadowIntent.getIntentClass());
    }
}