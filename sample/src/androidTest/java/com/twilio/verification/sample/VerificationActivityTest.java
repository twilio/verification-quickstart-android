package com.twilio.verification.sample;


import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.twilio.verification.external.VerificationStatus;
import com.twilio.verification.sample.mocks.MockTokenServerApi;
import com.twilio.verification.sample.mocks.MockTwilioVerification;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.twilio.verification.sample.TestUtils.enterPhoneNumber;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class VerificationActivityTest {

    @Rule
    public ActivityTestRule<VerificationActivity> verificationActivityActivityTestRule = new ActivityTestRule<>(VerificationActivity.class);
    private Context context;
    private VerificationActivity verificationActivity;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getTargetContext();
        verificationActivity = verificationActivityActivityTestRule.getActivity();
        verificationActivity.verificationPresenter.setTwilioVerification(new MockTwilioVerification(verificationActivity, VerificationStatus.State.ERROR, VerificationStatus.State.ERROR));
        verificationActivity.verificationPresenter.setTokenServerApi(new MockTokenServerApi(true));
    }

    @Test
    public void phoneNumberValidationTest() {
        // Test entering empty phone number
        ViewInteraction phoneNumber = enterPhoneNumber("");

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.verify_button), withText(context.getString(R.string.verify_button_label)), isDisplayed()));
        appCompatButton.perform(click());

        phoneNumber.check(matches(hasErrorText(context.getString(R.string.invalid_phone_number))));


        // Test entering invalid phone number
        phoneNumber = enterPhoneNumber("1 234 567 8900+");
        appCompatButton = onView(
                allOf(withId(R.id.verify_button), withText(context.getString(R.string.verify_button_label)), isDisplayed()));
        appCompatButton.perform(click());
        phoneNumber.check(matches(hasErrorText(context.getString(R.string.invalid_phone_number))));
    }

    @Test
    public void failureTransitionTest() {
        // Test that with a valid phone number we transition to next screen
        enterPhoneNumber("12345678900");

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.verify_button), withText(context.getString(R.string.verify_button_label)), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction title = onView(withId(R.id.verify_title));
        title.check(matches(withText(R.string.verify_failed_title_label)));
    }

    @Test
    public void successTransitionTest() {
        verificationActivity.verificationPresenter.setTwilioVerification(new MockTwilioVerification(verificationActivity, VerificationStatus.State.SUCCESS, VerificationStatus.State.SUCCESS));
        verificationActivity.verificationPresenter.setTwilioVerification(new MockTwilioVerification(verificationActivity, VerificationStatus.State.SUCCESS, VerificationStatus.State.SUCCESS));

        // Test that with a valid phone number we transition to next screen
        enterPhoneNumber("12345678900");

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.verify_button), withText(context.getString(R.string.verify_button_label)), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction title = onView(withId(R.id.verification_success_title));
        title.check(matches(withText(R.string.number_verified_success_label)));
    }

}
