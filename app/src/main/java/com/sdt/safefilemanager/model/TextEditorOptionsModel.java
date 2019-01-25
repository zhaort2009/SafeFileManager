package com.sdt.safefilemanager.model;

public class TextEditorOptionsModel {
    private int textSize;

    public TextEditorOptionsModel(int textSize) {
        this.textSize = textSize;
    }

    public TextEditorOptionsModel() {
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize() {
        this.textSize = 30;
    }
}
