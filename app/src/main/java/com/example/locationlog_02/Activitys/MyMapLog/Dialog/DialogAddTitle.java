package com.example.locationlog_02.Activitys.MyMapLog.Dialog;

import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.locationlog_02.Activitys.MyMapLog.MyMapLog;
import com.example.locationlog_02.Activitys.MyMapLog.Recyclerview.TitelListRecyclerAdapter;
import com.example.locationlog_02.Activitys.MyMapLog.Recyclerview.TitleListData;
import com.example.locationlog_02.R;
import com.example.locationlog_02.SQLiteDB.LocationLogDBHelper;

import java.util.ArrayList;

public class DialogAddTitle {
    private MyMapLog context;
    private TitelListRecyclerAdapter adapter;
    private LocationLogDBHelper logDBHelper;

    public DialogAddTitle(MyMapLog context, TitelListRecyclerAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
        logDBHelper = new LocationLogDBHelper(context);
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(final String presendTitle, final boolean isAddTitle, final int revisePosition) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_add_title);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.

        final Button okButton = (Button) dlg.findViewById(R.id.dialog_addtitel_okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.dialog_addtitel_cancelButton);
        final EditText editTitle = dlg.findViewById(R.id.dialog_addtitle_editTitle);
        editTitle.setText(presendTitle);

        if (isAddTitle == false) {
            //수정하기 경우
            okButton.setText("수정");
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<TitleListData> placeList = logDBHelper.readCategoryList(); // 현위치의 카테고리의 데이터를 가져온다.
                boolean samePlaceName = false;
                for (int i = 0; i < placeList.size(); i++) { //같은 이름이 있는지 검사한다.
                    samePlaceName = placeList.get(i).getTitle().equals(editTitle.getText().toString());
                    if (samePlaceName)
                        break;
                }

                if (samePlaceName == false) {
                    if (isAddTitle == true) {
                        //추가하기
                        adapter.addNewTitleItem(editTitle.getText().toString());
                    } else {
                        //수정하기
                        adapter.ReviseTitleItem(presendTitle,editTitle.getText().toString(), revisePosition);
                    }
                    dlg.dismiss();
                }
                else{
                    Toast.makeText(context, "같은 이름의 Category가 있습니다. 카테고리 이름을 수정해주세요", Toast.LENGTH_SHORT).show();
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
