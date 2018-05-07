package com.cybussolutions.bataado.Model;

/**
 * Created by Rizwan Jillani on 23-Apr-18.
 */
public class Conversation_Model {
    private String chatId;

    public String getChatFrom() {
        return chatFrom;
    }

    public void setChatFrom(String chatFrom) {
        this.chatFrom = chatFrom;
    }

    private String chatFrom;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }

    private String chatMessage;
}
