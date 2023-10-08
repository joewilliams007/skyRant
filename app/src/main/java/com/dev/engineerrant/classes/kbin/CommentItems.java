package com.dev.engineerrant.classes.kbin;

import com.dev.engineerrant.adapters.CommentItem;

import java.util.List;

public class CommentItems {
    Integer commentId;
    User user;
    Magazine magazine;
    String body;
    String lang;
    Integer uv;
    Integer dv;
    Integer favourites;
    Boolean isFavourited;
    Integer userVote;
    String createdAt;
    String editedAt;
    String lastActive;
    List<CommentItems> children;
    Integer childCount;
    Image image;

    public Image getImage() {
        return image;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public User getUser() {
        return user;
    }

    public Magazine getMagazine() {
        return magazine;
    }

    public String getBody() {
        return body;
    }

    public String getLang() {
        return lang;
    }

    public Integer getUv() {
        return uv;
    }

    public Integer getDv() {
        return dv;
    }

    public Integer getFavourites() {
        return favourites;
    }

    public Boolean getFavourited() {
        return isFavourited;
    }

    public Integer getUserVote() {
        return userVote;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getEditedAt() {
        return editedAt;
    }

    public String getLastActive() {
        return lastActive;
    }

    public List<CommentItems> getChildren() {
        return children;
    }

    public Integer getChildCount() {
        return childCount;
    }
}
