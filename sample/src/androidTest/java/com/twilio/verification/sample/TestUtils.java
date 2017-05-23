package com.twilio.verification.sample;

import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.view.View;
import android.widget.ProgressBar;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.actionWithAssertions;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by jsuarez on 4/26/17.
 */

public class TestUtils {

    @NonNull
    public static ViewInteraction enterPhoneNumber(String phoneNumberInput) {
        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.phone_number), isDisplayed()));
        textInputEditText.perform(click());

        // Added a sleep statement to match the phone picker delay which we cannot control
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction phoneNumber = onView(
                allOf(withId(R.id.phone_number), isDisplayed()));
        phoneNumber.perform(replaceText(phoneNumberInput), closeSoftKeyboard());
        return phoneNumber;
    }

    public static ViewAction replaceProgressBarDrawable() {
        return actionWithAssertions(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(ProgressBar.class);
            }

            @Override
            public String getDescription() {
                return "replace the ProgressBar drawable";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                // Replace the indeterminate drawable with a static red ColorDrawable
                ProgressBar progressBar = (ProgressBar) view;
                progressBar.setIndeterminateDrawable(new ColorDrawable(0xffff0000));
                uiController.loopMainThreadUntilIdle();
            }
        });
    }
}
