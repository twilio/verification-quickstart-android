package com.twilio.verification.sample;

import android.content.Intent;
import android.widget.TextView;

import com.twilio.verification.external.VerificationStatus;
import com.twilio.verification.sample.failure.VerificationFailedActivity;
import com.twilio.verification.sample.mocks.MockTokenServerApi;
import com.twilio.verification.sample.mocks.MockTwilioVerification;
import com.twilio.verification.sample.success.VerificationSuccessfulActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowIntent;

import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by jsuarez on 12/28/17.
 */
@RunWith(RobolectricTestRunner.class)
public class VerificationActivityTest {

    private VerificationActivity verificationActivity;

    @Before
    public void setUp() throws Exception {
        verificationActivity = Robolectric.setupActivity(VerificationActivity.class);
        verificationActivity.verificationPresenter.setTwilioVerification(new MockTwilioVerification(verificationActivity, VerificationStatus.State.ERROR, VerificationStatus.State.ERROR));
        verificationActivity.verificationPresenter.setTokenServerApi(new MockTokenServerApi(true));
    }

    @Test
    public void testEmptyPhoneNumberValidation() throws Exception {
        // Enter empty phone number
        TextView phoneNumberView = (TextView) verificationActivity.findViewById(R.id.phone_number);
        phoneNumberView.setText("");

        // Click verify
        verificationActivity.findViewById(R.id.verify_button).performClick();

        // Assert correct error is displayed
        assertEquals(phoneNumberView.getError().toString(), verificationActivity.getString(R.string.invalid_phone_number));
    }

    @Test
    public void testInvalidPhoneNumberValidation() throws Exception {
        // Enter invalid phone number
        TextView phoneNumberView = (TextView) verificationActivity.findViewById(R.id.phone_number);
        phoneNumberView.setText("1 234 567 8900+");

        // Click verify
        verificationActivity.findViewById(R.id.verify_button).performClick();

        // Assert correct error is displayed
        assertEquals(phoneNumberView.getError().toString(), verificationActivity.getString(R.string.invalid_phone_number));
    }

    @Test
    public void testFailedVerificationTransition() {
        // Test that with a valid phone number we transition to next screen
        TextView phoneNumberView = (TextView) verificationActivity.findViewById(R.id.phone_number);
        phoneNumberView.setText("12345678900");

        // Click verify
        verificationActivity.findViewById(R.id.verify_button).performClick();

        // Assert that the correct activity was launched
        Intent startedIntent = shadowOf(verificationActivity).getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);
        assertEquals(VerificationFailedActivity.class, shadowIntent.getIntentClass());
    }

    @Test
    public void testSuccessfulVerificationTransition() {
        // Inject success mock
        verificationActivity.verificationPresenter.setTwilioVerification(new MockTwilioVerification(verificationActivity, VerificationStatus.State.SUCCESS, VerificationStatus.State.SUCCESS));

        // Test that with a valid phone number we transition to next screen
        TextView phoneNumberView = (TextView) verificationActivity.findViewById(R.id.phone_number);
        phoneNumberView.setText("12345678900");

        // Click verify
        verificationActivity.findViewById(R.id.verify_button).performClick();

        // Assert that the correct activity was launched
        Intent startedIntent = shadowOf(verificationActivity).getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);
        assertEquals(VerificationSuccessfulActivity.class, shadowIntent.getIntentClass());
    }
}