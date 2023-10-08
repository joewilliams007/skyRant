package com.dev.engineerrant.adapters;

import com.dev.engineerrant.classes.matrix.Chunk;
import com.dev.engineerrant.classes.matrix.Messages;

public class MatrixChatItem {
    private final Chunk message;

    public MatrixChatItem(Chunk message) {
        this.message = message;
    }

    public Chunk getMessage() {
        return message;
    }
}
