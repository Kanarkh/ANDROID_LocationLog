package com.example.locationlog_02.Activitys.MyMapLog.Recyclerview;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class TitleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    ItemTouchHelperListener listener;

    public TitleItemTouchHelperCallback(ItemTouchHelperListener listener) {
        this.listener = listener;
    }


    // 각 View 에서 어떤 uesr action이 가능한지 정의
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;

        return makeMovementFlags(dragFlags, swipeFlags);
    }


    // user가 item을 drag할 때, ItemTouchHelper가 onMove를 호출
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

        return listener.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
    }

    // user가 itme을 swip할 때, ItemTouchHelper가 onSwiped를 호출
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        //listener.onItemRemove(viewHolder.getAdapterPosition());
    }
}
