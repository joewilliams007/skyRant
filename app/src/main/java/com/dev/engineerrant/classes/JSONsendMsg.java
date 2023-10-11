package com.dev.engineerrant.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JSONsendMsg {
    @SerializedName("msgtype")
    @Expose
    private transient String msgtype;
    @SerializedName("body")
    @Expose
    private transient String body;
    @SerializedName("m.mentions")
    @Expose
    private transient String[] mentions;

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String[] getMentions() {
        return mentions;
    }

    public void setMentions(String[] mentions) {
        this.mentions = mentions;
    }
}
