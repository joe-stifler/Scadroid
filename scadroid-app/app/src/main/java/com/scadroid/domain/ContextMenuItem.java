package com.scadroid.domain;

public class ContextMenuItem {
    private int icon;
    private String label;

    public ContextMenuItem(int i, String l){
        icon = i;
        label = l;
    }


    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
