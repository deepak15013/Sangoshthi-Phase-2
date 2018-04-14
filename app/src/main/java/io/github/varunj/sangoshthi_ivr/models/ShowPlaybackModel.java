package io.github.varunj.sangoshthi_ivr.models;

import android.support.annotation.NonNull;

public class ShowPlaybackModel implements Comparable<ShowPlaybackModel> {

    private Type type;
    private int order;
    private String duration;
    private String name;

    // Not started yet = -1
    // Stopped, but can be played = 0
    // Playing = 1
    // Pause = 2
    // Stopped = 3
    private int audioState;

    public ShowPlaybackModel(Type type, int order, String duration, String name) {
        this.type = type;
        this.order = order;
        this.duration = duration;
        this.name = name;
        this.audioState = -1;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAudioState() {
        return audioState;
    }

    public void setAudioState(int audioState) {
        this.audioState = audioState;
    }

    @Override
    public int compareTo(@NonNull ShowPlaybackModel showPlaybackModel) {
        return this.getOrder() - showPlaybackModel.getOrder();
    }

    public enum Type {
        content, question, answer
    }

    @Override
    public String toString() {
        return "ShowPlaybackModel{" +
                "type=" + type +
                ", order=" + order +
                ", duration='" + duration + '\'' +
                ", name='" + name + '\'' +
                ", audioState=" + audioState +
                '}';
    }
}
