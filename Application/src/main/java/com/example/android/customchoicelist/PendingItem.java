package com.example.android.customchoicelist;

public class PendingItem {
    String name= null;
    Boolean isPending = false;

    public PendingItem(String name, Boolean isPending) {
        this.name = name;
        this.isPending = isPending;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isPending() {
        return isPending;
    }

    public void setIsPending(Boolean isPending) {
        this.isPending = isPending;
    }
}
