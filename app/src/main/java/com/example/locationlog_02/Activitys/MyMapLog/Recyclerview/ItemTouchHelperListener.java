package com.example.locationlog_02.Activitys.MyMapLog.Recyclerview;

public interface ItemTouchHelperListener {
    boolean onItemMove(int fromPosision, int toPosision);
    void onItemRemove(int posision);
}

