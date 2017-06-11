package io.github.varunj.sangoshthi_ivr.models;

/**
 * Created by Deepak on 09-06-2017.
 */

public class CallerState {

    private String phoneNum;

    /* true - muted, false - unmuted */
    private boolean muteUnmuteState;

    /* true - question, false - no-question */
    private boolean questionState;

    private String onlineState;

    private int turn;

    public CallerState(String phoneNum, boolean muteUnmuteState, boolean questionState, String onlineState) {
        this.phoneNum = phoneNum;
        this.muteUnmuteState = muteUnmuteState;
        this.questionState = questionState;
        this.onlineState = onlineState;
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

    public String getOnlineState() {
        return onlineState;
    }

    public void setOnlineState(String onlineState) {
        this.onlineState = onlineState;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }
}
