package com.dev.engineerrant.models;

import com.dev.engineerrant.classes.Comment;
import com.dev.engineerrant.classes.ImgRant;

import java.util.List;

// works for register & login
public class ModelImgRant {
    List<Comment> comments;
    ImgRant rant;

    public List<Comment> getComments() {
        return comments;
    }

    public ImgRant getImgRant() {
        return rant;
    }
}

