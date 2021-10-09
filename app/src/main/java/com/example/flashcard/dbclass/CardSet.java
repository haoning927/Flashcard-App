package com.example.flashcard.dbclass;

import org.jetbrains.annotations.NotNull;

public class CardSet {
    private int setId;
    private int userId;
    private String setName;

    public CardSet(int setId, int userId, String setName) {
        this.setId = setId;
        this.userId = userId;
        this.setName = setName;
    }

    public int getSetId() {
        return setId;
    }

    public int getUserId() {
        return userId;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetId(int setId) {
        this.setId = setId;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @NotNull
    @Override
    public String toString() {
        return setName;
    }
}
