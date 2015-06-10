package com.example.android.customchoicelist;

/**
 * Created by manav on 9/6/15.
 */
public class Student {
    String name = null;
    boolean selected = false;

    public Student(String name, boolean selected) {
        this.name = name;
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
