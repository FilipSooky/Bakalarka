package com.example.languageguide.utils;

public class StudyProgram {
    private String name;
    private String level;
    private String form;
    private String description;

    public StudyProgram(String name, String level, String form, String description) {
        this.name = name;
        this.level = level;
        this.form = form;
        this.description = description;
    }

    public String getName() { return name; }
    public String getLevel() { return level; }
    public String getForm() { return form; }
    public String getDescription() { return description; }
}

