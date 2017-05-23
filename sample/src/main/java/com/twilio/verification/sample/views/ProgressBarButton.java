package com.twilio.verification.sample.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.twilio.verification.sample.R;

/**
 * Created by jsuarez on 4/25/17.
 */

public class ProgressBarButton extends FrameLayout {

    private Button button;
    private ProgressBar progressBar;

    // Attrs
    private String text;
    private int buttonTheme;
    private ColorStateList indeterminateTint;

    public ProgressBarButton(@NonNull Context context) {
        this(context, null);
    }

    public ProgressBarButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressBarButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs != null) {
            // Attribute initialization
            final TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.ProgressBarButton, 0, 0);

            text = a.getString(R.styleable.ProgressBarButton_text);

            buttonTheme = a.getResourceId(R.styleable.ProgressBarButton_button_theme, 0);
            indeterminateTint = a.getColorStateList(R.styleable.ProgressBarButton_indeterminateTint);

            a.recycle();
        }

        initViews(context);
    }

    private void initViews(Context context) {

        button = new Button(new ContextThemeWrapper(context, buttonTheme));

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        addView(button, layoutParams);


        progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleSmall);
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        progressBar.setVisibility(GONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.setStateListAnimator(null);
            progressBar.setIndeterminateTintList(indeterminateTint);
            progressBar.setIndeterminateTintMode(PorterDuff.Mode.SRC_IN);
        }

        addView(progressBar, layoutParams);


        button.setClickable(true);
        progressBar.setClickable(false);
        setText(text);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        button.setOnClickListener(l);
    }

    public void showProgressBar(boolean showProgress) {
        progressBar.setVisibility(showProgress ? VISIBLE : GONE);
        button.setEnabled(!showProgress);
        button.setAlpha(showProgress ? 0.5f : 1.0f);
    }

    public String getText() {
        return button.getText().toString();
    }

    public void setText(String text) {
        button.setText(text);
    }
}
