package com.twilio.verification.sample.failure;


import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.ProgressBar;

import com.twilio.verification.external.VerificationStatus;
import com.twilio.verification.sample.R;
import com.twilio.verification.sample.TestUtils;
import com.twilio.verification.sample.mocks.MockTokenServerApi;
import com.twilio.verification.sample.mocks.MockTwilioVerification;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.twilio.verification.sample.TestUtils.enterPhoneNumber;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class VerificationFailedActivityTest {

    @Rule
    public ActivityTestRule<VerificationFailedActivity> activityTestRule = new ActivityTestRule<>(VerificationFailedActivity.class);
    private Context context;
    private VerificationFailedActivity verificationFailedActivity;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getTargetContext();
        verificationFailedActivity = activityTestRule.getActivity();
        verificationFailedActivity.getVerificationPresenter().setTwilioVerification(new MockTwilioVerification(verificationFailedActivity, VerificationStatus.State.AWAITING_VERIFICATION, VerificationStatus.State.ERROR));
        verificationFailedActivity.getVerificationPresenter().setTokenServerApi(new MockTokenServerApi(true));
    }

    @Test
    public void phoneNumberValidationTest() {

        // Test entering invalid phone number
        ViewInteraction phoneNumber = enterPhoneNumber("1 234 567 8900+");

        ViewInteraction smsButton = onView(withId(R.id.verify_via_sms_button));
        smsButton.perform(click());

        phoneNumber.check(matches(hasErrorText(context.getString(R.string.invalid_phone_number))));

    }

    @Test
    public void smsSuccessTransitionTest() {
        successTransitionForButton(R.string.verify_via_sms_button_label);
    }

    @Test
    public void callSuccessTransitionTest() {
        successTransitionForButton(R.string.verify_via_call_button_label);
    }

    @Test
    public void callErrorTransitionTest() {
        errorTransitionForButton(R.string.verify_via_call_button_label);
    }

    @Test
    public void smsErrorTransitionTest() {
        errorTransitionForButton(R.string.verify_via_sms_button_label);
    }

    private void successTransitionForButton(int buttonText) {
        setupProgressBars();

        verificationFailedActivity.getVerificationPresenter().setTwilioVerification(new MockTwilioVerification(verificationFailedActivity, VerificationStatus.State.AWAITING_VERIFICATION, VerificationStatus.State.SUCCESS));

        ViewInteraction enterPhoneNumber = enterPhoneNumber("12345678900");
        enterPhoneNumber.perform(closeSoftKeyboard());

        ViewInteraction smsButton = onView(withText(buttonText));
        smsButton.perform(click());

        // Enter verification code
        ViewInteraction verificationCode = onView(allOf(withId(R.id.verification_code), isDisplayed()));
        verificationCode.perform(replaceText("1234"));

        ViewInteraction title = onView(withId(R.id.verification_success_title));
        title.check(matches(withText(R.string.number_verified_success_label)));
    }

    private void errorTransitionForButton(int buttonText) {
        setupProgressBars();

        verificationFailedActivity.getVerificationPresenter().setTwilioVerification(new MockTwilioVerification(verificationFailedActivity, VerificationStatus.State.AWAITING_VERIFICATION, VerificationStatus.State.ERROR));

        ViewInteraction enterPhoneNumber = enterPhoneNumber("12345678900");
        enterPhoneNumber.perform(closeSoftKeyboard());

        ViewInteraction smsButton = onView(withText(buttonText));
        smsButton.perform(click());

        // Enter verification code
        ViewInteraction verificationCode = onView(allOf(withId(R.id.verification_code), isDisplayed()));
        verificationCode.perform(replaceText("1234"));

        ViewInteraction title = onView(withId(R.id.verify_title));
        title.check(matches(withText(R.string.verify_failed_title_label)));
    }

    // Needed since the testing framework will wait until all progress bars stop spinning
    private void setupProgressBars() {
        // Replace the progress bar animation with a static color
        onView(
                allOf(isAssignableFrom(ProgressBar.class),
                        withParent(withId(R.id.verify_via_call_button)
                        )
                )
        ).perform(TestUtils.replaceProgressBarDrawable());

        onView(
                allOf(isAssignableFrom(ProgressBar.class),
                        withParent(withId(R.id.verify_via_sms_button)
                        )
                )
        ).perform(TestUtils.replaceProgressBarDrawable());
    }
}
