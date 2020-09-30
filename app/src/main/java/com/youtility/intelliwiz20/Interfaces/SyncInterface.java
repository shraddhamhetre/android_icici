package com.youtility.intelliwiz20.Interfaces;

public interface SyncInterface {

    public abstract boolean assetMaster() throws Exception;

    public abstract boolean jobNeedMaster() throws Exception;

    public abstract boolean jobNeedDetailsMaster() throws Exception;

	public abstract boolean typeAssistMaster() throws Exception;

    public abstract boolean geoFenceDetailsMaster() throws Exception;

    public abstract boolean peopleDetailMaster() throws Exception;

    public abstract boolean groupDetailMaster() throws Exception;

    public abstract boolean attendanceHistoryMaster() throws Exception;

    public abstract boolean questionMaster() throws Exception;

    public abstract boolean questionSetMaster() throws Exception;

    public abstract boolean questionSetBelongingMaster() throws Exception;

    public abstract boolean peopleGroupBelongingMaster() throws Exception;

    public abstract boolean siteMaster() throws Exception;

}

