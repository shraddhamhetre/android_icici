package com.youtility.intelliwiz20.Model;

import java.util.ArrayList;

/**
 * Created by youtility on 4/9/18.
 */

public class ChildSection {
    private long childSecId;
    private long childQSetId;
    private String childQSetName;
    private long parentId;
    private int childSeqNo;
    ArrayList<ChildSectionQuest> childSectionQuestArrayList;

    public int getChildSeqNo() {
        return childSeqNo;
    }

    public void setChildSeqNo(int childSeqNo) {
        this.childSeqNo = childSeqNo;
    }

    public long getChildSecId() {
        return childSecId;
    }

    public void setChildSecId(long childSecId) {
        this.childSecId = childSecId;
    }

    public long getChildQSetId() {
        return childQSetId;
    }

    public void setChildQSetId(long childQSetId) {
        this.childQSetId = childQSetId;
    }

    public String getChildQSetName() {
        return childQSetName;
    }

    public void setChildQSetName(String childQSetName) {
        this.childQSetName = childQSetName;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public ArrayList<ChildSectionQuest> getChildSectionQuestArrayList() {
        return childSectionQuestArrayList;
    }

    public void setChildSectionQuestArrayList(ArrayList<ChildSectionQuest> childSectionQuestArrayList) {
        this.childSectionQuestArrayList = childSectionQuestArrayList;
    }
}
