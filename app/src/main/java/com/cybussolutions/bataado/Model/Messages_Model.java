package com.cybussolutions.bataado.Model;

/**
 * Created by Rizwan Jillani on 23-Apr-18.
 */
public class Messages_Model {
    public String getChatKey() {
        return chatKey;
    }

    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
    }

    String chatKey;
    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }

    String chatMessage;
    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    String messageDate;
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    String messageType;
    public String getBrandid() {
        return brandid;
    }

    public void setBrandid(String brandid) {
        this.brandid = brandid;
    }

    public String getBrandname() {
        return brandname;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    public String getBrandimage() {
        return brandimage;
    }

    public void setBrandimage(String brandimage) {
        this.brandimage = brandimage;
    }

    public String getBrandRating() {
        return brandRating;
    }

    public void setBrandRating(String brandRating) {
        this.brandRating = brandRating;
    }

    String brandid;
    String brandname;
    String brandimage;
    String brandRating;

    public String getChatBrandId() {
        return chatBrandId;
    }

    public void setChatBrandId(String chatBrandId) {
        this.chatBrandId = chatBrandId;
    }

    public String getChatFlagId() {
        return chatFlagId;
    }

    public void setChatFlagId(String chatFlagId) {
        this.chatFlagId = chatFlagId;
    }

    String chatBrandId;
    String chatFlagId;

    public String getChatFrom() {
        return chatFrom;
    }

    public void setChatFrom(String chatFrom) {
        this.chatFrom = chatFrom;
    }

    String chatFrom;
}
