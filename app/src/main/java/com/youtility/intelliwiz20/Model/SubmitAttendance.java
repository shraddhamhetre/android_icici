package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 18/1/18.
 * Data transfer object for Area manager module
 */


public class SubmitAttendance {
    private long peopleId;
    private int absentOrPresent;
    private String attendanceDates;
    private String peopleName;
    private String mdtz;
    private String cdtz;
    private long cuser;
    private long muser;
    private int syncStatus;
    private long approvalStatus;
    private String attendanceMonth;
    private long buid;
    private long siteid;
    private int presentDaysCount;
    private String weeklyOffDays;
    private String nationalHoliday;
    private String extraDuty;
    private String remark;
    private int totalCount1;//pd+wo
    private int totalCount2;//pd+wo+ed+nh
    private String peopleLoginId;
    private int weeklyOffDaysCount;
    private int nationalHolidayCount;
    private int extraDutyDaysCount;
    private long contractId;
    private String contractName;
    private String siteCode;
    private String siteName;
    private String designation;
    private String period;

    private String pMornDates;
    private String pNoonDates;
    private String pNigtDates;
    private String pGenrDates;

    private String edMornDates;
    private String edNoonDates;
    private String edNigtDates;
    private String edGenrDates;

    public String getEdMornDates() {
        return edMornDates;
    }

    public void setEdMornDates(String edMornDates) {
        this.edMornDates = edMornDates;
    }

    public String getEdNoonDates() {
        return edNoonDates;
    }

    public void setEdNoonDates(String edNoonDates) {
        this.edNoonDates = edNoonDates;
    }

    public String getEdNigtDates() {
        return edNigtDates;
    }

    public void setEdNigtDates(String edNigtDates) {
        this.edNigtDates = edNigtDates;
    }

    public String getEdGenrDates() {
        return edGenrDates;
    }

    public void setEdGenrDates(String edGenrDates) {
        this.edGenrDates = edGenrDates;
    }

    public String getpMornDates() {
        return pMornDates;
    }

    public void setpMornDates(String pMornDates) {
        this.pMornDates = pMornDates;
    }

    public String getpNoonDates() {
        return pNoonDates;
    }

    public void setpNoonDates(String pNoonDates) {
        this.pNoonDates = pNoonDates;
    }

    public String getpNigtDates() {
        return pNigtDates;
    }

    public void setpNigtDates(String pNigtDates) {
        this.pNigtDates = pNigtDates;
    }

    public String getpGenrDates() {
        return pGenrDates;
    }

    public void setpGenrDates(String pGenrDates) {
        this.pGenrDates = pGenrDates;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public long getContractId() {
        return contractId;
    }

    public void setContractId(long contractId) {
        this.contractId = contractId;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getPeopleLoginId() {
        return peopleLoginId;
    }

    public void setPeopleLoginId(String peopleLoginId) {
        this.peopleLoginId = peopleLoginId;
    }

    public int getWeeklyOffDaysCount() {
        return weeklyOffDaysCount;
    }

    public void setWeeklyOffDaysCount(int weeklyOffDaysCount) {
        this.weeklyOffDaysCount = weeklyOffDaysCount;
    }

    public int getNationalHolidayCount() {
        return nationalHolidayCount;
    }

    public void setNationalHolidayCount(int nationalHolidayCount) {
        this.nationalHolidayCount = nationalHolidayCount;
    }

    public int getExtraDutyDaysCount() {
        return extraDutyDaysCount;
    }

    public void setExtraDutyDaysCount(int extraDutyDaysCount) {
        this.extraDutyDaysCount = extraDutyDaysCount;
    }

    public String getWeeklyOffDays() {
        return weeklyOffDays;
    }

    public void setWeeklyOffDays(String weeklyOffDays) {
        this.weeklyOffDays = weeklyOffDays;
    }

    public String getNationalHoliday() {
        return nationalHoliday;
    }

    public void setNationalHoliday(String nationalHoliday) {
        this.nationalHoliday = nationalHoliday;
    }

    public String getExtraDuty() {
        return extraDuty;
    }

    public void setExtraDuty(String extraDuty) {
        this.extraDuty = extraDuty;
    }

    public int getTotalCount1() {
        return totalCount1;
    }

    public void setTotalCount1(int totalCount1) {
        this.totalCount1 = totalCount1;
    }

    public int getTotalCount2() {
        return totalCount2;
    }

    public void setTotalCount2(int totalCount2) {
        this.totalCount2 = totalCount2;
    }

    public int getPresentDaysCount() {
        return presentDaysCount;
    }

    public void setPresentDaysCount(int presentDaysCount) {
        this.presentDaysCount = presentDaysCount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getPeopleId() {
        return peopleId;
    }

    public void setPeopleId(long peopleId) {
        this.peopleId = peopleId;
    }

    public int getAbsentOrPresent() {
        return absentOrPresent;
    }

    public void setAbsentOrPresent(int absentOrPresent) {
        this.absentOrPresent = absentOrPresent;
    }

    public String getAttendanceDates() {
        return attendanceDates;
    }

    public void setAttendanceDates(String attendanceDates) {
        this.attendanceDates = attendanceDates;
    }

    public String getPeopleName() {
        return peopleName;
    }

    public void setPeopleName(String peopleName) {
        this.peopleName = peopleName;
    }

    public String getMdtz() {
        return mdtz;
    }

    public void setMdtz(String mdtz) {
        this.mdtz = mdtz;
    }

    public String getCdtz() {
        return cdtz;
    }

    public void setCdtz(String cdtz) {
        this.cdtz = cdtz;
    }

    public long getCuser() {
        return cuser;
    }

    public void setCuser(long cuser) {
        this.cuser = cuser;
    }

    public long getMuser() {
        return muser;
    }

    public void setMuser(long muser) {
        this.muser = muser;
    }

    public int getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }

    public long getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(long approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getAttendanceMonth() {
        return attendanceMonth;
    }

    public void setAttendanceMonth(String attendanceMonth) {
        this.attendanceMonth = attendanceMonth;
    }

    public long getBuid() {
        return buid;
    }

    public void setBuid(long buid) {
        this.buid = buid;
    }

    public long getSiteid() {
        return siteid;
    }

    public void setSiteid(long siteid) {
        this.siteid = siteid;
    }
}
