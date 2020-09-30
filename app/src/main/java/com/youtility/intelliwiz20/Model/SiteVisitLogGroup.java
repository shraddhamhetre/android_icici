package com.youtility.intelliwiz20.Model;

import java.util.ArrayList;

/**
 * Created by youtility on 13/7/18.
 */

public class SiteVisitLogGroup {
    private String siteName;
    private ArrayList<SiteVisitedLog>siteVisitedLogArrayList;

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public ArrayList<SiteVisitedLog> getSiteVisitedLogArrayList() {
        return siteVisitedLogArrayList;
    }

    public void setSiteVisitedLogArrayList(ArrayList<SiteVisitedLog> siteVisitedLogArrayList) {
        this.siteVisitedLogArrayList = siteVisitedLogArrayList;
    }
}
