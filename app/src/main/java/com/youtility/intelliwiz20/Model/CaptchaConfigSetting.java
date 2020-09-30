package com.youtility.intelliwiz20.Model;

public class CaptchaConfigSetting {
    private boolean enablesleepingguard;
    private String starttime;
    private String endtime;
    private int captchafreq;

    public boolean isEnablesleepingguard() {
        return enablesleepingguard;
    }

    public void setEnablesleepingguard(boolean enablesleepingguard) {
        this.enablesleepingguard = enablesleepingguard;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public int getCaptchafreq() {
        return captchafreq;
    }

    public void setCaptchafreq(int captchafreq) {
        this.captchafreq = captchafreq;
    }
}
