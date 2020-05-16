package com.example.locationlog_02.Activitys.MyMapLog.Recyclerview;

import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locationlog_02.Activitys.MyMapLog.MyMapLog;
import com.example.locationlog_02.R;
import com.example.locationlog_02.SQLiteDB.LocationLogDBHelper;

import java.util.ArrayList;

public class PlaceRecyclerAdapter extends RecyclerView.Adapter<PlaceRecyclerAdapter.ViewHolder> implements ItemTouchHelperListener {

    private static final String TAG = "PlaceRecyclerAdapter";
    MyMapLog context;
    ArrayList<PlaceData> dataList;
    private RecyclerPlaceClickListener mListener;
    private PlaceRecyclerAdapter thisAdapter = this;

    //db기능
    LocationLogDBHelper logDBHelper;

    //생성자
    public PlaceRecyclerAdapter(MyMapLog context, ArrayList<PlaceData> dataList) {
        this.context = context;
        this.dataList = dataList;
        logDBHelper = new LocationLogDBHelper(context);
    }

    public void setItems(ArrayList<PlaceData> items) {
        this.dataList = items;
        notifyDataSetChanged();
    }

    //ItemTouchHelperListener
    @Override
    public boolean onItemMove(int fromPosision, int toPosision) {
        if (fromPosision < 0 || fromPosision >= dataList.size() || toPosision < 0 || toPosision >= dataList.size()) {
            return false;
        }

//        PlaceData fromItem = dataList.get(fromPosision);
//        dataList.remove(fromPosision);
//        dataList.add(toPosision, fromItem);

        //리사이클러뷰 처리
        //이름을 바꿔준다.
        String fromTitle = dataList.get(fromPosision).getTitle();
        dataList.get(fromPosision).setTitle(dataList.get(toPosision).getTitle());
        dataList.get(toPosision).setTitle(fromTitle);

        //위치를 바꿔준다.
        double fromLat = dataList.get(fromPosision).getLatitude();
        double fromLon = dataList.get(fromPosision).getLongitude();
        dataList.get(fromPosision).setLatitude(dataList.get(toPosision).getLatitude());
        dataList.get(fromPosision).setLongitude(dataList.get(toPosision).getLongitude());
        dataList.get(toPosision).setLatitude(fromLat);
        dataList.get(toPosision).setLongitude(fromLon);

        notifyItemMoved(fromPosision, toPosision);
        return true;
    }

    @Override
    public void onItemRemove(int posision) {

    }

    //클릭 인터페이스
    public interface RecyclerPlaceClickListener {
        void onPlaceItemClicked(int position);
    }

    public void setOnClickListener(RecyclerPlaceClickListener listener) {
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
        final PlaceData item = dataList.get(position);
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
                                placeAEditDialog(pos, item.getTitle());
//                                DialogAddTitle titleDialog = new DialogAddTitle(context, thisAdapter);
//                                titleDialog.callFunction(item.getTitle(), false, position);
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
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onPlaceItemClicked(pos);
                }
            });
        }
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
    public void addNewTitleItem(String category, String title, double latitude, double longitude) {
        //리사이클러뷰에 추가해준다.
        PlaceData data = new PlaceData(category, title, latitude, longitude);

        //null일 경우 리스트를 만들어준다.
        if(this.dataList==null)
            dataList = new ArrayList<>();

        this.dataList.add(0, data);
        notifyItemInserted(0);

        //SQLite에 추가한다.
        logDBHelper.savePlace(category, title, latitude, longitude); // DB에 저장
        this.dataList.get(0).setId(logDBHelper.readLastId()); //마지막에 저장된 ID로 셋
    }

    //항목 수정하기.
    public void ReviseTitleItem(String title, int posision) {
        this.dataList.get(posision).setTitle(title);
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
                logDBHelper.delete(dataList.get(position).getId()); // 플레이스 삭제

                //플레이스안에 내용 삭제
                ArrayList<LocationContentsData> contentsList=logDBHelper.readPlaceContentsList(dataList.get(position).getCategory(),dataList.get(position).getTitle());
                for (int i = 0; i < contentsList.size(); i++) {
                    logDBHelper.delete(contentsList.get(i).getId());
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

    } // end dialog

    //수정시 사용하는 Dialog
    private void placeAEditDialog(final int position, final String presendTitle) {
        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_add_place);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final Button okButton = (Button) dlg.findViewById(R.id.dialog_addPlace_okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.dialog_addPlace_cancelButton);
        final EditText editTitle = dlg.findViewById(R.id.dialog_addPlace_editTitle);
        final Spinner spinner = dlg.findViewById(R.id.dialog_addPlace_categorySpinner);

        //기존 장소명을 가져온다.
        editTitle.setText(presendTitle);

        //카테고리
        final String presentCategotyTitle = dataList.get(position).getCategory();

        //선택된 카테고리 번호를 가져온다. -1일 경우 선택되지 않았음.
        int categoryNum = context.presentCategoryNum;
        Log.e(TAG, categoryNum + "");
        ArrayList<String> spinnerList = new ArrayList<>();
        for (int i = 0; i < context.categoryRecyclerDataList.size(); i++) { //스피니의 데이터 리스트를 만들어준다.
            spinnerList.add(context.categoryRecyclerDataList.get(i).getTitle());
        }
        spinner.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, spinnerList));

        if (categoryNum >= 0)//카테고리 초기화
            spinner.setSelection(categoryNum);
        else
            spinner.setSelection(0);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DB 추가
//                Log.e("title Adapter",""+dataList.get(position).getId());
//                categoryHelper.delete(dataList.get(position).getId());
                //DB 업데이트


                //data (수정한 데이터)
                int id = (int) dataList.get(position).getId();
                String category = spinner.getSelectedItem().toString();
                String place = editTitle.getText().toString();
                double latitude =dataList.get(position).getLatitude();
                double longitude=dataList.get(position).getLongitude();

                if (presentCategotyTitle.equals(spinner.getSelectedItem().toString())) {
                    // 카테고리 위치가 바뀌지 않았다.
                    ArrayList<PlaceData> placeList = logDBHelper.readPlaceList(category); // 현위치의 카테고리의 데이터를 가져온다.
                    boolean samePlaceName=false;
                    for (int i = 0; i < placeList.size(); i++) { //같은 이름이 있는지 검사한다.
                        samePlaceName=placeList.get(i).getTitle().equals(place);
                        if(samePlaceName)
                            break;
                    }
                    if(samePlaceName==false) { //같은이름이 없다, 저장한다.

                        logDBHelper.updatePlace(id, category, place, latitude, longitude);
                        logDBHelper.updatePlaceCategory(presentCategotyTitle, category, presendTitle, place);

                        //리사이클러뷰 수정
                        dataList.get(position).setTitle(editTitle.getText().toString());
                        notifyItemChanged(position);
                        dlg.dismiss();
                    }else{
                        Toast.makeText(context, "카테고리에 동일한 이름의 장소가 있습니다. 장소이름을 바꿔주세요", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //카테고리 위치가 변경됬다.
                    ArrayList<PlaceData> placeList = logDBHelper.readPlaceList(category); // 바꿀 카테고리의 데이터를 가져온다.
                    boolean samePlaceName=false;
                    for (int i = 0; i < placeList.size(); i++) { //같은 이름이 있는지 검사한다.
                        samePlaceName=placeList.get(i).getTitle().equals(place);
                        if(samePlaceName)
                            break;
                    }
                    if(samePlaceName==false) { //같은 이름이 없다.
                        //DB 변경
                        logDBHelper.updatePlace(id, category, place, latitude, longitude);
                        logDBHelper.updatePlaceCategory(presentCategotyTitle, category, presendTitle, place);
                        //DB 위치를 맨 위로 올려줘야하는뎅..
                        placeList = logDBHelper.readPlaceList(category); // 바꾼 카테고리의 데이터를 가져온다.

                        for (int i = 0; i < placeList.size(); i++) {
                            if(placeList.get(i).getTitle().equals(place)){ //넘어간 데이터를 찾았다.
                                for (int j = i; j > 0 ; j--) {
                                    //해당 데이터와 이전 데이터의 place를 서로 바꿔준다.
                                    String fromName=placeList.get(j).getTitle();
                                    placeList.get(j).setTitle(placeList.get(j-1).getTitle());
                                    placeList.get(j-1).setTitle(fromName);

                                    //위치를 바꿔준다.
                                    double fromLat = placeList.get(i).getLatitude();
                                    double fromLon = placeList.get(i).getLongitude();
                                    placeList.get(j).setLatitude(placeList.get(j-1).getLatitude());
                                    placeList.get(j).setLongitude(placeList.get(j-1).getLongitude());
                                    placeList.get(j-1).setLatitude(fromLat);
                                    placeList.get(j-1).setLongitude(fromLon);
                                }
                                break;
                            }
                        }
                        // 바꾼 위치로 저장한다.
                        logDBHelper.updatePlaceAll(placeList);

                        context.mapMarkerReset();
                        //리사이클러뷰 삭제
                        dataList.remove(position);
                        notifyDataSetChanged();
                        dlg.dismiss();
                    }else{
                        Toast.makeText(context, "선택하신 카테고리에 동일한 이름의 장소가 있습니다. 장소이름을 바꿔주세요", Toast.LENGTH_SHORT).show();
                    }
                }


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
