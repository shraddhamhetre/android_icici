package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 18/11/17.
 *
 * not in used
 */

public class ApplicationAccess {
    private String appName;
    private int appImage;
    private String appDesc;
    private boolean isAccess;
    private String appCode;

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getAppImage() {
        return appImage;
    }

    public void setAppImage(int appImage) {
        this.appImage = appImage;
    }

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    public boolean getIsAccess() {
        return isAccess;
    }

    public void setIsAccess(boolean isAccess) {
        this.isAccess = isAccess;
    }
}
