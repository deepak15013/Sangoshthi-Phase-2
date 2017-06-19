/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.models;

/**
 * Created by Deepak on 18-06-2017.
 */

public class NotificationModel {

    private String body;
    private int read_status;
    private int msg_id;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getRead_status() {
        return read_status;
    }

    public void setRead_status(int read_status) {
        this.read_status = read_status;
    }

    public int getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(int msg_id) {
        this.msg_id = msg_id;
    }

    @Override
    public String toString() {
        return "NotificationModel{" +
                "body='" + body + '\'' +
                ", read_status=" + read_status +
                ", msg_id=" + msg_id +
                '}';
    }
}
