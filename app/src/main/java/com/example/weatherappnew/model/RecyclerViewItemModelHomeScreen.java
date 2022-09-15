package com.example.weatherappnew.model;

public class RecyclerViewItemModelHomeScreen {
    private String time;
    private String temperature;
    private String icon;

    public RecyclerViewItemModelHomeScreen(String time, String temperature, String icon) {
        this.time = time;
        this.temperature = temperature;
        this.icon = icon;
    }

    public String getTime() {
        return time;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getIcon() {
        return icon;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}

