package com.youtility.intelliwiz20.Model;

/**
 * Created by youtility on 11/4/18.
 */

public class TransportMode {
    private String travelMode;
    private String travelTime;
    private String travelDistance;
    private String travelMoney;
    private Boolean isSelected;


    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    public String getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(String travelTime) {
        this.travelTime = travelTime;
    }

    public String getTravelDistance() {
        return travelDistance;
    }

    public void setTravelDistance(String travelDistance) {
        this.travelDistance = travelDistance;
    }

    public String getTravelMoney() {
        return travelMoney;
    }

    public void setTravelMoney(String travelMoney) {
        this.travelMoney = travelMoney;
    }
}
