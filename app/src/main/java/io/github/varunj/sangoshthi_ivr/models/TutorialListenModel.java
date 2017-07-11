/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.models;

public class TutorialListenModel {

    String show_id;
    String show_status;
    String listen_timestamp;
    String topic;
    int packet_id;
    int countSeconds;

    public TutorialListenModel(String show_id, String show_status, String listen_timestamp, String topic, int countSeconds) {
        this.show_id = show_id;
        this.show_status = show_status;
        this.listen_timestamp = listen_timestamp;
        this.topic = topic;
        this.countSeconds = countSeconds;
    }

    public String getShow_id() {
        return show_id;
    }

    public void setShow_id(String show_id) {
        this.show_id = show_id;
    }

    public String getShow_status() {
        return show_status;
    }

    public void setShow_status(String show_status) {
        this.show_status = show_status;
    }

    public String getListen_timestamp() {
        return listen_timestamp;
    }

    public void setListen_timestamp(String listen_timestamp) {
        this.listen_timestamp = listen_timestamp;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getPacket_id() {
        return packet_id;
    }

    public void setPacket_id(int packet_id) {
        this.packet_id = packet_id;
    }

    public int getCountSeconds() {
        return countSeconds;
    }

    public void setCountSeconds(int countSeconds) {
        this.countSeconds = countSeconds;
    }

    @Override
    public String toString() {
        return "TutorialListenModel{" +
                "show_id='" + show_id + '\'' +
                ", show_status='" + show_status + '\'' +
                ", listen_timestamp='" + listen_timestamp + '\'' +
                ", topic='" + topic + '\'' +
                ", packet_id=" + packet_id +
                ", countSeconds=" + countSeconds +
                '}';
    }
}
