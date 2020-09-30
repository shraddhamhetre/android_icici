package com.youtility.intelliwiz20.Interfaces;

/**
 * Created by youtility on 25/5/18.
 */

public interface IAsynCompletedListener {
    public void asyncComplete(boolean success,int statusCode, long returnId);
}
