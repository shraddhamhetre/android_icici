package com.youtility.intelliwiz20.Model;

/**
 * Created by PrashantD on 17/08/17.
 *
 * asset data transfer object
 */

public class Asset {
    private long assetid;
    private String enable;
    private long parent;
    private long cuser;
    private long muser;
    private String cdtz;
    private String mdtz;
    //private String isdeleted;
    private String assetcode;
    private String assetname;
    private String iscritical;
    private String gpslocation;
    private long identifier;
    private long runningstatus;
    private long buid;
    private String loccode;
    private String locname;

    private long type;
    private long category;
    private long subcategory;
    private long brand;
    private long model;
    private String supplier;
    private double capacity;
    private long unit;
    private String yom;
    private String msn;
    private String bdate;
    private String pdate;
    private String isdate;
    private double billval;
    private long servprov;
    private String sfdate;
    private String stdate;
    private long meter;
    private String qsetids;
    private String qsetname;
    private long service;
    private String servprovname;
    private String tempcode;
    private double multiplicationfactor;

    public double getMultiplicationfactor() {
        return multiplicationfactor;
    }

    public void setMultiplicationfactor(double multiplicationfactor) {
        this.multiplicationfactor = multiplicationfactor;
    }

    public String getTempcode() {
        return tempcode;
    }

    public void setTempcode(String tempcode) {
        this.tempcode = tempcode;
    }

    public String getServprovname() {
        return servprovname;
    }

    public void setServprovname(String servprovname) {
        this.servprovname = servprovname;
    }

    public long getService() {
        return service;
    }

    public void setService(long service) {
        this.service = service;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public double getBillval() {
        return billval;
    }

    public void setBillval(double billval) {
        this.billval = billval;
    }

    public String getQsetname() {
        return qsetname;
    }

    public void setQsetname(String qsetname) {
        this.qsetname = qsetname;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public long getCategory() {
        return category;
    }

    public void setCategory(long category) {
        this.category = category;
    }

    public long getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(long subcategory) {
        this.subcategory = subcategory;
    }

    public long getBrand() {
        return brand;
    }

    public void setBrand(long brand) {
        this.brand = brand;
    }

    public long getModel() {
        return model;
    }

    public void setModel(long model) {
        this.model = model;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }


    public long getUnit() {
        return unit;
    }

    public void setUnit(long unit) {
        this.unit = unit;
    }

    public String getYom() {
        return yom;
    }

    public void setYom(String yom) {
        this.yom = yom;
    }

    public String getMsn() {
        return msn;
    }

    public void setMsn(String msn) {
        this.msn = msn;
    }

    public String getBdate() {
        return bdate;
    }

    public void setBdate(String bdate) {
        this.bdate = bdate;
    }

    public String getPdate() {
        return pdate;
    }

    public void setPdate(String pdate) {
        this.pdate = pdate;
    }

    public String getIsdate() {
        return isdate;
    }

    public void setIsdate(String isdate) {
        this.isdate = isdate;
    }


    public long getServprov() {
        return servprov;
    }

    public void setServprov(long servprov) {
        this.servprov = servprov;
    }

    public String getSfdate() {
        return sfdate;
    }

    public void setSfdate(String sfdate) {
        this.sfdate = sfdate;
    }

    public String getStdate() {
        return stdate;
    }

    public void setStdate(String stdate) {
        this.stdate = stdate;
    }

    public long getMeter() {
        return meter;
    }

    public void setMeter(long meter) {
        this.meter = meter;
    }

    public String getQsetids() {
        return qsetids;
    }

    public void setQsetids(String qsetids) {
        this.qsetids = qsetids;
    }

    public String getLoccode() {
        return loccode;
    }

    public void setLoccode(String loccode) {
        this.loccode = loccode;
    }

    public String getLocname() {
        return locname;
    }

    public void setLocname(String locname) {
        this.locname = locname;
    }

    public long getBuid() {
        return buid;
    }

    public void setBuid(long buid) {
        this.buid = buid;
    }

    public long getAssetid() {
        return assetid;
    }

    public void setAssetid(long assetid) {
        this.assetid = assetid;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public long getParent() {
        return parent;
    }

    public void setParent(long parent) {
        this.parent = parent;
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

    public String getCdtz() {
        return cdtz;
    }

    public void setCdtz(String cdtz) {
        this.cdtz = cdtz;
    }

    public String getMdtz() {
        return mdtz;
    }

    public void setMdtz(String mdtz) {
        this.mdtz = mdtz;
    }

    public String getAssetcode() {
        return assetcode;
    }

    public void setAssetcode(String assetcode) {
        this.assetcode = assetcode;
    }

    public String getAssetname() {
        return assetname;
    }

    public void setAssetname(String assetname) {
        this.assetname = assetname;
    }

    public String getIscritical() {
        return iscritical;
    }

    public void setIscritical(String iscritical) {
        this.iscritical = iscritical;
    }

    public String getGpslocation() {
        return gpslocation;
    }

    public void setGpslocation(String gpslocation) {
        this.gpslocation = gpslocation;
    }

    public long getIdentifier() {
        return identifier;
    }

    public void setIdentifier(long identifier) {
        this.identifier = identifier;
    }

    public long getRunningstatus() {
        return runningstatus;
    }

    public void setRunningstatus(long runningstatus) {
        this.runningstatus = runningstatus;
    }
}
