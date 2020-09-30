package com.youtility.intelliwiz20.Interfaces;

import com.youtility.intelliwiz20.Model.SubmitAttendance;

import java.util.ArrayList;

/**
 * Created by PrashantD on 27/1/18.
 *
 * used in area and branch manager attendance sheet report
 */

public interface IRefreshListView {
    void changeRowData(int currentposition, ArrayList<SubmitAttendance> attendanceArrayList);
}
