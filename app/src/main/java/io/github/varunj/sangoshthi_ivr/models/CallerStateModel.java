package io.github.varunj.sangoshthi_ivr.models;

/**
 * Created by Deepak on 09-06-2017.
 */

public class CallerStateModel {

    private String phoneNum;

    /* true - muted, false - unmuted */
    private boolean muteUnmuteState;
    private boolean muteUnmuteDisabled;

    /* true - question, false - no-question */
    private boolean questionState;

    private int turn;

    public CallerStateModel(String phoneNum, boolean muteUnmuteState, boolean questionState) {
        this.phoneNum = phoneNum;
        this.muteUnmuteState = muteUnmuteState;
        this.questionState = questionState;
        this.muteUnmuteDisabled = false;
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
}
