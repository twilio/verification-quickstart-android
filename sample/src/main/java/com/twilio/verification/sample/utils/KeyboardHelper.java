package com.twilio.verification.sample.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by jsuarez on 3/22/17.
 */

public class KeyboardHelper {

    public static final int DELAY_MILLIS = 250;


    public static void showKeyboard(final Context context, final View view) {
        if (context == null || view == null) {
            return;
        }

        Handler handler = new Handler();

        //Uses a runnable to do the showing as there are race conditions with hiding the keyboard
        //that this solves.
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }, DELAY_MILLIS);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity == null) {
            return;
        }

        View focusedView = activity.getCurrentFocus();

        if (focusedView == null) {
            return;
        }

        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }
}
