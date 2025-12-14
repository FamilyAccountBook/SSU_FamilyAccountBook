package com.example.ui_familybook;

import com.google.firebase.Timestamp;
import java.io.Serializable;
import java.util.Date;

public class Sticker implements Serializable {
    private int stickerIndex;
    private String emoji;
    private String message;
    private Timestamp timestamp;    //  Firebase 정렬을 위한 시간 객체

    // 1. 빈 생성자 (Firebase Firestore에서 데이터를 불러올 때 필수)
    public Sticker() {
    }

    // 2. 데이터 생성용 생성자
    public Sticker(int stickerIndex, String emoji, String message, Timestamp timestamp) {
        this.stickerIndex = stickerIndex;
        this.emoji = emoji;
        this.message = message;
        this.timestamp = timestamp;
    }

    // --- Getters ---
    public int getStickerIndex() {
        return stickerIndex;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    // --- Setters ---
    public void setStickerIndex(int stickerIndex) {
        this.stickerIndex = stickerIndex;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}