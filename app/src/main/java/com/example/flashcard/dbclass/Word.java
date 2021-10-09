package com.example.flashcard.dbclass;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Word {

    private int wordId;
    private int setId;
    private String wordTitle;
    private String wordDes;
    private int remember;
    private int forgot;

    public Word(int wordId, int setId, String wordTitle, String wordDes, int remember, int forgot) {
        this.wordId = wordId;
        this.setId = setId;
        this.wordTitle = wordTitle;
        this.wordDes = wordDes;
        this.remember = remember;
        this.forgot = forgot;
    }

    public Word(int setId, String wordTitle, String wordDes) {
        this.setId = setId;
        this.wordTitle = wordTitle;
        this.wordDes = wordDes;
        this.remember = 0;
        this.forgot = 0;
    }

    public Word() {

    }

    public int getWordId() {
        return wordId;
    }

    public int getSetId() {
        return setId;
    }

    public String getWordTitle() {
        return wordTitle;
    }

    public String getWordDes() {
        return wordDes;
    }

    public int getRemember() {
        return remember;
    }

    public int getForgot() {
        return forgot;
    }

    public void setSetId(int setId) {
        this.setId = setId;
    }

    public void setForgot(int forgot) {
        this.forgot = forgot;
    }

    public void setRemember(int remember) {
        this.remember = remember;
    }

    public void setWordDes(String wordDes) {
        this.wordDes = wordDes;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public void setWordTitle(String wordTitle) {
        this.wordTitle = wordTitle;
    }

    @NotNull
    @Override
    public String toString() {
        return wordTitle;
    }
}
