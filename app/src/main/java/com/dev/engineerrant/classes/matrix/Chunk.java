package com.dev.engineerrant.classes.matrix;

public class Chunk {

    String sender;
    String type;
    Content content;
    Long origin_server_ts;

    public Long getOrigin_server_ts() {
        return origin_server_ts;
    }

    public Content getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public String getType() {
        return type;
    }

}
