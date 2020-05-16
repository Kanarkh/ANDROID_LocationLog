package com.example.locationlog_02.Activitys.MyMapLog.Recyclerview;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locationlog_02.Activitys.MyMapLog.Dialog.DialogAddTitle;
import com.example.locationlog_02.Activitys.MyMapLog.MyMapLog;
import com.example.locationlog_02.R;
import com.example.locationlog_02.SQLiteDB.LocationLogDBHelper;

import java.util.ArrayList;

public class TitelListRecyclerAdapter extends RecyclerView.Adapter<TitelListRecyclerAdapter.ViewHolder> implements ItemTouchHelperListener {
    private static final String TAG = "TitelListRecyclerAdapter";

    MyMapLog context;
    ArrayList<TitleListData> dataList;
    private RecyclerClickListener mListener;
    private TitelListRecyclerAdapter thisAdapter = this;

    //DB 제어
    LocationLogDBHelper logDBHelper;

    //생성자
    public TitelListRecyclerAdapter(MyMapLog context, ArrayList<TitleListData> dataList) {
        this.context = context;
        this.dataList = dataList;
        logDBHelper = new LocationLogDBHelper(context);
    }

    //ItemTouchHelperListener
    @Override
    public boolean onItemMove(int fromPosision, int toPosision) {
        if (fromPosision < 0 || fromPosision >= dataList.size() || toPosision < 0 || toPosision >= dataList.size()) {
            return false;
        }

        //리사이클러뷰 처리
        String fromTitle = dataList.get(fromPosision).getTitle();
        dataList.get(fromPosision).setTitle(dataList.get(toPosision).getTitle());
        dataList.get(toPosision).setTitle(fromTitle);

//        dataList.remove(fromPosision);
//        dataList.add(toPosision, fromItem);

        //DB 처리, 현재 ID 위치가 DB의 위치이기 때문에 자리바꿈(끼어들기) 의 경우 자리바뀌는 위치를 시작으로 그보다 낮은 부분을 전부 바꿔줘야한다.

        notifyItemMoved(fromPosision, toPosision);
//        notifyItemChanged(toPosision);
        return true;
    }

    @Override
    public void onItemRemove(int posision) {

    }

    //클릭 인터페이스
    public interface RecyclerClickListener {
        void onItemClicked(int position);
    }

    public void setOnClickListener(RecyclerClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mymaplog_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final TitleListData item = dataList.get(position);
        holder.title.setText(item.getTitle());
        holder.moreMenu.setVisibility(View.VISIBLE);

        final int pos = position;
        final ViewHolder hol = holder;
        holder.moreMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, hol.moreMenu);
                popupMenu.inflate(R.menu.menu_mymaplist_category);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {

                            case R.id.menu_mymapListCategory_edit:
                                DialogAddTitle titleDialog = new DialogAddTitle(context, thisAdapter);
                                titleDialog.callFunction(item.getTitle(), false, pos);
                                return true;
                            case R.id.menu_mymapListCategory_delete:
//                                dataList.remove(position);
//                                notifyDataSetChanged();
                                dialog(pos);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });
        //클릭
        if (mListener != null) {
            final int posC = position;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClicked(posC);
                }
            });
        }
//        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        Log.e(TAG+"dowm","down "+holder.title.getText());
//                        break;
//                    default:
//                        return false;
//                }
//
//                return true;
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return this.dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageButton moreMenu;
        ImageButton changePositionButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_myMapLogCategory_titel_textView);
            moreMenu = itemView.findViewById(R.id.item_myMapLogCategory_moreMenu_imageButton);
            changePositionButton = itemView.findViewById(R.id.item_myMapLogCategory_changePosition_Button);
        }
    }

    //항목 추가하기.
    public void addNewTitleItem(String title) {
        //리사이클러뷰에 추가해준다.
        TitleListData data = new TitleListData(title, 0);
        this.dataList.add(0, data);
        notifyDataSetChanged();

        //SQLite에 추가한다.
//        categoryHelper.saveData(title,0);
//        this.dataList.get(0).setId(categoryHelper.readLastId());
//        Log.e("titel Adapter",title+categoryHelper.readLastId());
        logDBHelper.saveCategory(title);

    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
    }


    //항목 수정하기.
    public void ReviseTitleItem(String fromTitle ,String toTitle, int posision) {
        //DB 수정
        //logDBHelper.updateCategory(dataList.get(posision).getId(), title);
        logDBHelper.updateCategoryChangeInData(fromTitle,toTitle);
        //리사이클러뷰 수정
        this.dataList.get(posision).setTitle(toTitle);
        notifyDataSetChanged();
    }

    //------------------------------------------------------------------------------------------------dialog
    private void dialog(final int position) {
        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_public_simpletext);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final Button okButton = (Button) dlg.findViewById(R.id.dialog_publicText_okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.dialog_publicText_cancelButton);
        final TextView titleText = dlg.findViewById(R.id.dialog_publicText_title);
        final TextView textView = dlg.findViewById(R.id.dialog_publicText_text);
        textView.setText("삭제시 복구할수 없으며 포함된 모든 데이터가 지워집니다.\"" + dataList.get(position).getTitle() + "\"를 정말 지우시겠습니까?");
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DB 삭제
                logDBHelper.delete(dataList.get(position).getId());

                //카테고리 내 플레이스 삭제
                ArrayList<PlaceData> placeList = logDBHelper.readPlaceList(dataList.get(position).getTitle());
                for (int i = 0; i < placeList.size(); i++) {

                    //플레이스안에 내용 삭제
                    ArrayList<LocationContentsData> contentsList=logDBHelper.readPlaceContentsList(placeList.get(i).getCategory(),placeList.get(i).getTitle());
                    for (int j = 0; j < contentsList.size(); j++) {
                        logDBHelper.delete(contentsList.get(j).getId());
                    }
                    //플레이스 삭제
                    logDBHelper.delete(placeList.get(i).getId());
                }

                //리사이클러뷰 삭제
                dataList.remove(position);
                notifyDataSetChanged();

                //사용자에게 공지
                Toast.makeText(context, "삭제되었습니다", Toast.LENGTH_SHORT).show();
                dlg.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dlg.dismiss();
            }
        });

    }
}
