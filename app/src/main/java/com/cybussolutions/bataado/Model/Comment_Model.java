package com.cybussolutions.bataado.Model;

/**
 * Created by Rizwan Jillani on 22-Feb-18.
 */

public class Comment_Model {
    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private String commentId;
    private String comment;

    public String getCommentBy() {
        return commentBy;
    }

    public void setCommentBy(String commentBy) {
        this.commentBy = commentBy;
    }

    public String getCommentById() {
        return commentById;
    }

    public void setCommentById(String commentById) {
        this.commentById = commentById;
    }

    private String commentBy;
    private String commentById;

    public String getCommentByProfile() {
        return commentByProfile;
    }

    public void setCommentByProfile(String commentByProfile) {
        this.commentByProfile = commentByProfile;
    }

    String commentByProfile;

    public String getInappropriate() {
        return inappropriate;
    }

    public void setInappropriate(String inappropriate) {
        this.inappropriate = inappropriate;
    }

    String inappropriate;
}
