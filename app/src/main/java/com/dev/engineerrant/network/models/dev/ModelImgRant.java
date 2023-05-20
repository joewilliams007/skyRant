package com.dev.engineerrant.network.models.dev;

import com.dev.engineerrant.classes.dev.Comment;
import com.dev.engineerrant.classes.dev.ImgRant;

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

