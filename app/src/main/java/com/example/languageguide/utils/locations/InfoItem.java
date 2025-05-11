package com.example.languageguide.utils.locations;

public class InfoItem {
    private String key;
    private int fontSize;
    private String fontStyle;

    public InfoItem(String key, int fontSize, String fontStyle) {
        this.key = key;
        this.fontSize = fontSize;
        this.fontStyle = fontStyle;
    }

    public String getKey() {
        return key;
    }
    public int getFontSize() {
        return fontSize;
    }
    public String getFontStyle() {
        return fontStyle;
    }
}
