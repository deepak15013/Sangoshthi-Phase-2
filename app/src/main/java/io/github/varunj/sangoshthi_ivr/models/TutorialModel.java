/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.models;

/**
 * Created by Deepak on 20-06-2017.
 */

public class TutorialModel {

    private String tutorialName;
    private String fileName;

    /* Lock - true, Unlock = false */
    private boolean locked;

    public TutorialModel(String tutorialName, String fileName, boolean locked) {
        this.tutorialName = tutorialName;
        this.fileName = fileName;
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

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
