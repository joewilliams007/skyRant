package com.dev.engineerrant.adapters;

import com.dev.engineerrant.classes.dev.Links;
import com.dev.engineerrant.classes.kbin.CommentItems;

import java.util.List;

public class EntryCommentItem {
    CommentItems commentItems;

    public EntryCommentItem(CommentItems commentItems) {
        this.commentItems = commentItems;
    }

    public CommentItems getCommentItems() {
        return commentItems;
    }
}
