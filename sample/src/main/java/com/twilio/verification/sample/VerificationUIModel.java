package com.twilio.verification.sample;

import com.twilio.verification.external.Via;

/**
 * Created by jsuarez on 4/20/17.
 */

public class VerificationUIModel {
    private State currentState;
    private String errorMessage;
    private Via via;

    protected VerificationUIModel(State state) {
        currentState = state;
    }
    
    public static VerificationUIModel inProgress(Via via) {
        VerificationUIModel verificationUIModel = new VerificationUIModel(State.IN_PROGRESS);
        verificationUIModel.setVia(via);
        return verificationUIModel;
    }

    public static VerificationUIModel failure(String errorMessage) {
        VerificationUIModel verificationUIModel = new VerificationUIModel(State.ERROR);
        verificationUIModel.setErrorMessage(errorMessage);
        return verificationUIModel;
    }

    public static VerificationUIModel success() {
        return new VerificationUIModel(State.SUCCESS);
    }

    public State getCurrentState() {
        return currentState;
    }

    public Via getVia() {
        return via;
    }

    public void setVia(Via via) {
        this.via = via;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public enum State {
        IN_PROGRESS,
        SUCCESS,
        ERROR
    }
}
