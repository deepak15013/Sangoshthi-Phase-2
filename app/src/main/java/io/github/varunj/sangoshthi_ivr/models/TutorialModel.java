/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.models;


public class TutorialModel {

    private String tutorialName;
    private String fileName;
    private String showName;

    /* Locked - true, Unlocked = false */
    private boolean locked;

    public TutorialModel(String tutorialName, String fileName, String showName, boolean locked) {
        this.tutorialName = tutorialName;
        this.fileName = fileName;
        this.showName = showName;
        this.locked = locked;
    }

    public String getTutorialName() {
        return tutorialName;
    }

    public void setTutorialName(String tutorialName) {
        this.tutorialName = tutorialName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
