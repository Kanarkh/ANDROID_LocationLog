package com.example.locationlog_02.Activitys.MyMapLog.Recyclerview;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locationlog_02.Manager.ImageManager;
import com.example.locationlog_02.R;
import com.example.locationlog_02.SQLiteDB.LocationLogDBHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LocationContentsRecyclerAdapter extends RecyclerView.Adapter<LocationContentsRecyclerAdapter.ViewHolder> {
    Context context;

    private RecyclerVlickListener mListener;

    private List<LocationContentsData> mItems = new ArrayList<>();
    private String preItemDay = "0";
    private ImageManager imageManager;

    LocationLogDBHelper logDBHelper;


//    ArrayList<ViewHolder> viewHolderList = new ArrayList<>();


    public interface RecyclerVlickListener {
        void onContentsItemClicked(int pos);

        void onContentsCorrectionClicked(int pos);
    }

    public LocationContentsRecyclerAdapter(Context context, RecyclerVlickListener listener) {
        this.context = context;
        imageManager = new ImageManager(context);
        mListener = listener;
        logDBHelper = new LocationLogDBHelper(context);
    }

    public void setItems(List<LocationContentsData> items) {
        this.mItems = items;
        dateFrstCheck();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mymaplog_contents, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationContentsData item = mItems.get(position);


        //날짜
//        String fullDateStrign = item.getDay(); //YYYYMMddHHmmss 정렬을 여기서 할게 아니라 아이템이 새로 삽입될때 해야한다.
//        String dateString = item.getDay().substring(0,8);
//        String timeString = item.getDay().substring(8);
//
//        String date = String.format("%s-%s-%s",dateString.substring(0,4),dateString.substring(4,6),dateString.substring(6,8));
//        String time = String.format("【%s시 %s분 %s초】",timeString.substring(0,2),timeString.substring(2,4),timeString.substring(4,6));
//        holder.dayTextView.setText(date);
//        holder.timeTextView.setText(time);
//
//        if (preItemDay.equals(dateString) == false) {
//            //같지않을때, 날짜가 다를때
//            holder.dayTextView.setVisibility(View.VISIBLE);
//            preItemDay = dateString;
//        } else {
//            //날짜가 이전에 추가한것과 같을때
//            holder.dayTextView.setVisibility(View.GONE);
//            if (position != 0) {
//                holder.dayTextView.setVisibility(View.VISIBLE);
//                viewHolderList.get(position-1).dayTextView.setVisibility(View.GONE);
//                preItemDay = dateString; //이전 시간으로 기록
//            }
//        }

        //글 시간
        String timeString = item.getDay().substring(8);
        String time = String.format("【%s시 %s분 %s초】", timeString.substring(0, 2), timeString.substring(2, 4), timeString.substring(4, 6));
        holder.timeTextView.setText(time);

        //글 날짜
        if (item.isFirst()) {
            //상단 첫번째 날짜
            String dateString = item.getDay().substring(0, 8);
            String date = String.format("%s-%s-%s", dateString.substring(0, 4), dateString.substring(4, 6), dateString.substring(6, 8));
            holder.dayTextView.setText(date);
            holder.dayTextView.setVisibility(View.VISIBLE);
        } else {
            holder.dayTextView.setVisibility(View.GONE);
        }


        //이미지
        if (item.contentsPhoto_uri != null) {
            //이미지 개수가 하나이고, 첫번째 값이 "" 이 아닐때
            if (item.contentsPhoto_uri.size() == 1 && item.contentsPhoto_uri.get(0).equals("") == false) {
                Log.e("LoctionContent_onBindViewHolder", item.contentsPhoto_uri.get(0));
//                holder.contentsImage.setImageBitmap(imageManager.DecodeBitmapFile(item.getContentsPhoto_uri().get(0)));
                holder.contentsImage.setImageBitmap(imageManager.resizeBitmapImage(imageManager.DecodeBitmapFile(item.getContentsPhoto_uri().get(0)), 300));
//                holder.contentsImage.setImageURI(Uri.parse(item.contentsPhoto_uri.get(0)));
                holder.contentsImage.setVisibility(View.VISIBLE);
                holder.imageNum.setVisibility(View.GONE);
                //이미지 개수가 0이 아니고, 이미지 개수가 1개가 아닐때
            } else if (item.contentsPhoto_uri.size() != 0 && item.contentsPhoto_uri.size() != 1) {
                Log.e("LoctionContent_onBindViewHolder", item.contentsPhoto_uri.get(0));
//                holder.contentsImage.setImageBitmap(imageManager.DecodeBitmapFile(item.getContentsPhoto_uri().get(0)));
                holder.contentsImage.setImageBitmap(imageManager.resizeBitmapImage(imageManager.DecodeBitmapFile(item.getContentsPhoto_uri().get(0)), 300));
//                holder.contentsImage.setImageURI(Uri.parse(item.contentsPhoto_uri.get(0)));
                holder.contentsImage.setVisibility(View.VISIBLE);
                holder.imageNum.setVisibility(View.VISIBLE);
                holder.imageNum.setText("+" + (item.contentsPhoto_uri.size() - 1));
            } else {
                //그 외에는 보이지 않게 한다.
                holder.contentsImage.setVisibility(View.GONE);
                holder.imageNum.setVisibility(View.GONE);
            }

        } else {
            //이미지가 없으면 보이지 않게 한다.
            holder.contentsImage.setVisibility(View.GONE);
            holder.imageNum.setVisibility(View.GONE);
        }

        if (item.contentsText.equals("") == false) {
            holder.contentsText.setText(item.contentsText);
            holder.contentsText.setVisibility(View.VISIBLE);
            Log.e("LoctionContent_onBindViewHolder", item.contentsText);
        } else {
            holder.contentsText.setVisibility(View.GONE);
        }

        //클릭이벤트
        if (mListener != null) {
            final int pos = position;
            Log.e("adapter_mListener pos", "" + pos);
            Log.e("adapter_mListener position", "" + position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //클릭-상세보기
                    mListener.onContentsItemClicked(pos);
                }
            });
            holder.correctBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //수정
                    mListener.onContentsCorrectionClicked(pos);
                }
            });
            holder.deleteBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //삭제
                    dialog(pos);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // TODO : 뷰홀더 완성하시오
        TextView dayTextView;
        TextView timeTextView;
        TextView contentsText;
        ImageView contentsImage;
        TextView imageNum;
        Button correctBt;
        Button deleteBt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.item_mapLogContents_dayText);
            timeTextView = itemView.findViewById(R.id.item_myMapLogContents_timeText);
            contentsText = itemView.findViewById(R.id.item_mapLogContents_textView);
            contentsImage = itemView.findViewById(R.id.item_mapLogContents_image);
            imageNum = itemView.findViewById(R.id.item_mapLogContents_photoNumTextView);
            correctBt = itemView.findViewById(R.id.item_myMapLogContents_correctionbutton);
            deleteBt = itemView.findViewById(R.id.item_myMapLogContents_deletionbutton);
        }
    }

//    @Override
//    public void onViewRecycled(@NonNull ViewHolder holder) {
//        super.onViewRecycled(holder);
//        Log.e("LoctionContent_onViewRecyclerd", holder.contentsImage.toString());
//    }

    //---------------------------------------------------------------------------------------------- 기능
    //항목 추가하기.
    public void addNewItem(LocationContentsData data) {
        this.mItems.add(0, data);
        dateFrstCheck(); //임시, 느려지면 개선할것.
//        dateSortingList();
        notifyDataSetChanged();
    }

    //항목 수정하기.
    public void ReviseItem(LocationContentsData data, int posision) {
        this.mItems.remove(posision);
        this.mItems.add(posision, data);
        notifyItemChanged(posision);
    }

    //항목 삭제하기.
    public void deleteItem(int position) {
        this.mItems.remove(position);
        notifyItemRemoved(position);
    }

    //List 시간순으로 정렬하기.(선택정렬 사용) // 사실 시간순으로 정렬하기보다 시간순에 맞게 새로운 데이터를 삽입하는게 올바르다.
    public void dateSortingList() {
        LinkedList<Long> tempIdList = new LinkedList<>(); //Id 임시저장
        LocationContentsData pointData; // 초기화.

        for (int i = 0; i < mItems.size(); i++) {
            tempIdList.add(mItems.get(i).getId()); // Id의 위치를 기억한다. (소팅시 순서가 바뀌게 되고, 바뀐 상태를 저장하려면 필요하다.
        }
        for (int i = 0; i < mItems.size(); i++) { //I
            pointData = mItems.get(i); //새로운 포인트
            for (int j = i + 1; j < mItems.size(); j++) { // J
                //선택정렬
                if (Long.getLong(pointData.getDay()) < Long.getLong(mItems.get(j).getDay())) {
                    //크면 포인트를 바꿔준다.
                    pointData = mItems.get(j);
                }
            }
            //현재 pointData는 getDay가 가장 큰 숫자가 들어가있다. 위치를 i 위치에 삽입한다.
            mItems.remove(pointData);
            mItems.add(i, pointData);
        }
        for (int i = 0; i < mItems.size(); i++) { //위치가 바뀐 Id를 복구한다.
            mItems.get(i).setId(tempIdList.get(i));
        }
        //저장은 생명주기, 리스트를 바꿔줄때 자연스럽게 바뀌도록 한다.
    }

    //item 날짜 보여줄지 말지 검사.
    public void dateFrstCheck() {
        if (mItems != null)
            for (int i = 0; i < mItems.size(); i++) {
                if (i == 0) {
                    mItems.get(i).setFirst(true);
                } else { // 첫항이 아니기 때문에 항상 이전값이 있다.
                    String priorDate = mItems.get(i - 1).getDay().substring(0, 8);
                    String currentDate = mItems.get(i).getDay().substring(0, 8);
                    if (priorDate.equals(currentDate)) {
                        //이전과 현재의 Date가 같다면
                        mItems.get(i).setFirst(false);
                    } else {
                        //이전과 현재의 Date가 다르다면
                        mItems.get(i).setFirst(true);
                    }
                }
            }
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
        textView.setText("삭제시 복구할수 없으며 포함된 모든 데이터가 지워집니다. 정말 지우시겠습니까?");
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //DB 삭제
                logDBHelper.delete(mItems.get(position).getId());

                deleteItem(position);
                notifyDataSetChanged();
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
