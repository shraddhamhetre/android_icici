package com.youtility.intelliwiz20.Interfaces;

public interface IGridviewItemClickListeners
{
    public void onGridViewItemClick(int position, String appName, boolean isAccess, String appCode);
    public void onLongGridViewItemClick(int position, String appName, boolean isAccess, String appCode);

}
