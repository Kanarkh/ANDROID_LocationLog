package com.example.locationlog_02.Activitys.MyMapTrace.Recyclerview;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locationlog_02.Activitys.MyMapTrace.Dialog.DialogStartTrace;
import com.example.locationlog_02.Activitys.MyMapTrace.MyMapTrace;
import com.example.locationlog_02.R;
import com.example.locationlog_02.SQLiteDB.LocationLogDBHelper;

import java.util.ArrayList;
import java.util.List;

public class TraceLogRecyclerAdapter extends RecyclerView.Adapter<TraceLogRecyclerAdapter.ViewHolder> {

    public interface RecyclerClickListener {
        void onTraceLogItemClicked(int pos);
    }
    
    private RecyclerClickListener mListener;
    private List<TraceLogData> mItems = new ArrayList<>();
    private MyMapTrace context;

    private LocationLogDBHelper logDBHelper;
    public TraceLogRecyclerAdapter(RecyclerClickListener listener,MyMapTrace context) {
        this.context = context;
        mListener = listener;
        logDBHelper = new LocationLogDBHelper(context);
    }

    public void setItems(List<TraceLogData> items) {
        this.mItems = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_maptrace_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        TraceLogData item = mItems.get(position);
        holder.title.setText(item.getTitle());
        holder.setTime.setText(item.getStopTime());
        holder.distance.setText(item.getDistance()+"m");
        final TraceLogData thisItme = item;
        final int pos = position;
        holder.moreMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, holder.moreMenu);
                popupMenu.inflate(R.menu.menu_mymaplist_category);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {

                            case R.id.menu_mymapListCategory_edit: //수정
                                DialogStartTrace dialog = new DialogStartTrace(context,thisItme);
                                dialog.callFunction(thisItme.getTitle(),false,pos);
                                notifyDataSetChanged();
                                return true;
                            case R.id.menu_mymapListCategory_delete:  //삭제
                                //db삭제
                                logDBHelper.delete(thisItme.getId());
                                //리사이클러 삭제
                                mItems.remove(position);
                                notifyDataSetChanged();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });
        if(mListener!=null){

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onTraceLogItemClicked(pos);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView setTime;
        TextView distance;
        ImageButton moreMenu;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_mapTrace_title);
            setTime = itemView.findViewById(R.id.item_mapTrace_setTime);
            distance = itemView.findViewById(R.id.item_mapTrace_distance);
            moreMenu = itemView.findViewById(R.id.item_mapTrace_moreButton);
        }
    }
}