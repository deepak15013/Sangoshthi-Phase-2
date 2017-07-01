/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.models;

public class CallerStateModel {

    private String phoneNum;

    /* true - muted, false - unmuted */
    private boolean muteUnmuteState;
    private boolean muteUnmuteDisabled;

    /* true - question, false - no-question */
    private boolean questionState;

    private String task;
    private boolean reconnection;

    private int turn;

    public CallerStateModel(String phoneNum, boolean muteUnmuteState, boolean questionState, String task) {
        this.phoneNum = phoneNum;
        this.muteUnmuteState = muteUnmuteState;
        this.questionState = questionState;
        this.task = task;
        this.muteUnmuteDisabled = false;
        this.reconnection = false;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public boolean isMuteUnmuteState() {
        return muteUnmuteState;
    }

    public void setMuteUnmuteState(boolean muteUnmuteState) {
        this.muteUnmuteState = muteUnmuteState;
    }

    public boolean isQuestionState() {
        return questionState;
    }

    public void setQuestionState(boolean questionState) {
        this.questionState = questionState;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public boolean isMuteUnmuteDisabled() {
        return muteUnmuteDisabled;
    }

    public void setMuteUnmuteDisabled(boolean muteUnmuteDisabled) {
        this.muteUnmuteDisabled = muteUnmuteDisabled;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isReconnection() {
        return reconnection;
    }

    public void setReconnection(boolean reconnection) {
        this.reconnection = reconnection;
    }
}
