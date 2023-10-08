package com.dev.engineerrant.network.models.matrix;

import com.dev.engineerrant.classes.matrix.Messages;
import com.dev.engineerrant.classes.sky.Projects;

import java.util.List;

// works for register & login
public class ModelMatrixChat {

    String room_id;
    Messages messages;

    public String getRoom_id() {
        return room_id;
    }

    public Messages getMessages() {
        return messages;
    }
}

