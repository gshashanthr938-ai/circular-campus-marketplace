package com.campusmarket.model;

import java.sql.Timestamp;

public class Review {
    private int rating;
    private String comment;
    private String reviewerName;
    private Timestamp createdAt;
    public int getRating(){return rating;} public void setRating(int rating){this.rating=rating;}
    public String getComment(){return comment;} public void setComment(String comment){this.comment=comment;}
    public String getReviewerName(){return reviewerName;} public void setReviewerName(String name){this.reviewerName=name;}
    public Timestamp getCreatedAt(){return createdAt;} public void setCreatedAt(Timestamp value){this.createdAt=value;}
}
