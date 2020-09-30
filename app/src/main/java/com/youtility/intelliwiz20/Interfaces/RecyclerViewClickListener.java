package com.youtility.intelliwiz20.Interfaces;

import android.view.View;

public interface RecyclerViewClickListener {
    void onClick(View view, int position, long jobneedid, int isExpiredValue, String jobStatus);
}
