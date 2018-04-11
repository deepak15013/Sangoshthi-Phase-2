package io.github.varunj.sangoshthi_ivr.models;

public class ShowPlaybackModel {

    private Type type;
    private int order;
    private String duration;
    private String name;

    public ShowPlaybackModel(Type type, int order, String duration, String name) {
        this.type = type;
        this.order = order;
        this.duration = duration;
        this.name = name;
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

    public enum Type {
        content, question, answer
    }
}
