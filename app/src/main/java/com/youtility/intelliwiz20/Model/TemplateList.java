package com.youtility.intelliwiz20.Model;

//questionsetid, qsetname, sites
public class TemplateList
{
    private long questionsetid;
    private String qsetname;
    private String sites;

    public long getQuestionsetid() {
        return questionsetid;
    }

    public void setQuestionsetid(long questionsetid) {
        this.questionsetid = questionsetid;
    }

    public String getQsetname() {
        return qsetname;
    }

    public void setQsetname(String qsetname) {
        this.qsetname = qsetname;
    }

    public String getSites() {
        return sites;
    }

    public void setSites(String sites) {
        this.sites = sites;
    }
}
