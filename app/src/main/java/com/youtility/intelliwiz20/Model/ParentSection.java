package com.youtility.intelliwiz20.Model;

import java.util.ArrayList;

/**
 * Created by youtility on 4/9/18.
 */

public class ParentSection {
    private long parentSecId;
    private long qSetId;
    private String qSetName;
    private long parentId;
    ArrayList<ChildSection> childSectionArrayList;

    public long getParentSecId() {
        return parentSecId;
    }

    public void setParentSecId(long parentSecId) {
        this.parentSecId = parentSecId;
    }

    public long getqSetId() {
        return qSetId;
    }

    public void setqSetId(long qSetId) {
        this.qSetId = qSetId;
    }

    public String getqSetName() {
        return qSetName;
    }

    public void setqSetName(String qSetName) {
        this.qSetName = qSetName;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public ArrayList<ChildSection> getChildSectionArrayList() {
        return childSectionArrayList;
    }

    public void setChildSectionArrayList(ArrayList<ChildSection> childSectionArrayList) {
        this.childSectionArrayList = childSectionArrayList;
    }
}
