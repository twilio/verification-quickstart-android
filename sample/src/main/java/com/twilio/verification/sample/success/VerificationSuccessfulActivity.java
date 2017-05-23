package com.twilio.verification.sample.success;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.twilio.verification.sample.R;

public class VerificationSuccessfulActivity extends AppCompatActivity {

    public static Intent getIntent(Context context) {
        return new Intent(context, VerificationSuccessfulActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_successful);
    }
}
