package com.example.locationlog_02.Activitys.MyMapLog.Dialog;


import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.locationlog_02.Activitys.MyMapLog.MyMapLog;
import com.example.locationlog_02.Activitys.MyMapLog.Recyclerview.PlaceData;
import com.example.locationlog_02.Activitys.MyMapLog.Recyclerview.PlaceRecyclerAdapter;
import com.example.locationlog_02.Activitys.MyMapLog.Recyclerview.TitleListData;
import com.example.locationlog_02.R;
import com.example.locationlog_02.SQLiteDB.LocationLogDBHelper;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class DialogAddMapMarker {

    private MyMapLog context;
    private PlaceRecyclerAdapter adapter;
    private LocationLogDBHelper logDBHelper;
    private String address;
    private LatLng latLng;

    public DialogAddMapMarker(MyMapLog context, PlaceRecyclerAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
        logDBHelper = new LocationLogDBHelper(context);
    }
    public void setInit(String address, LatLng latLng){
        this.address =address;
        this.latLng = latLng;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(ArrayList<TitleListData> categoryList, int categoryNum) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_addmapmarker);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.

         Button okButton = dlg.findViewById(R.id.okButton);
         Button cancelButton = dlg.findViewById(R.id.cancelButton);
        final EditText editText = dlg.findViewById(R.id.dialog_addMapMarker_editText);
        final Spinner spinner = dlg.findViewById(R.id.dialog_addPlace_categorySpinner);
        TextView addressView = dlg.findViewById(R.id.addMapMarker_address_textView);
        addressView.setText("주소 : "+address);

        ArrayList<String> spinnerList = new ArrayList<>();
        for (int i = 0; i < categoryList.size(); i++) { //스피니의 데이터 리스트를 만들어준다.
            spinnerList.add(categoryList.get(i).getTitle());
        }
        spinner.setAdapter(new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, spinnerList));


        if (categoryNum >= 0) //선택되지 않았을경우 -1
            spinner.setSelection(categoryNum);
        else
            spinner.setSelection(0);

        //예외처리
        if(spinner.getSelectedItem()==null){
            Toast.makeText(context, "Category가 없습니다. 위치 리스트로 가서 추가해주세요", Toast.LENGTH_SHORT).show();
            dlg.dismiss();
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String category = spinner.getSelectedItem().toString();
                String place = editText.getText().toString();
                //카테고리 타이틀 위도 경도
                ArrayList<PlaceData> placeList = logDBHelper.readPlaceList(category); // 현위치의 카테고리의 데이터를 가져온다.
                boolean samePlaceName=false;
                
                for (int i = 0; i < placeList.size(); i++) {

                    //같은 이름이 있는지 검사한다.
                    samePlaceName=placeList.get(i).getTitle().equals(place);
                    if(samePlaceName)
                        break;
                }

                if(samePlaceName==false) { //같은 이름이 없다면
                    adapter.addNewTitleItem(category, place, latLng.latitude,latLng.longitude); //DB에 저장
                    Toast.makeText(context, "장소가 추가되었습니다.", Toast.LENGTH_SHORT).show();  // 토스트로 추가 메시지
                    context.completearker(category, place, latLng.latitude,latLng.longitude); //MaMapLog에 장소추가가 완료되었다 말한다.
                    adapter.notifyDataSetChanged(); //노티 초기화
                    dlg.dismiss();
                }else{ // 동일한 이름이 있다면.
                    Toast.makeText(context, "선택한 Category에 동일한 아름의 장소가 있습니다, 장소 이름을 수정해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
    }
}