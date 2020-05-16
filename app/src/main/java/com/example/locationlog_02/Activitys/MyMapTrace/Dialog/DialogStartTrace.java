package com.example.locationlog_02.Activitys.MyMapTrace.Dialog;

import android.app.Dialog;
import android.icu.text.SimpleDateFormat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.locationlog_02.Activitys.MyMapTrace.MyMapTrace;
import com.example.locationlog_02.Activitys.MyMapTrace.Recyclerview.TraceLogData;
import com.example.locationlog_02.R;
import com.example.locationlog_02.SQLiteDB.LocationLogDBHelper;

import java.util.Date;

public class DialogStartTrace {
    private MyMapTrace context;
    private TraceLogData traceObjcet;

    private LocationLogDBHelper logDBHelper;
    public DialogStartTrace(MyMapTrace context, TraceLogData traceObjcet) {
        this.context = context;
        this.traceObjcet = traceObjcet;
        logDBHelper = new LocationLogDBHelper(context);
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(String presendTitle, final boolean isAddTitle, final int revisePosition) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_maptrace_add);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.

        final Button okButton = (Button) dlg.findViewById(R.id.dialog_maptrace_okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.dialog_maptrace_cancelButton);
        final EditText editTitle = dlg.findViewById(R.id.dialog_maptrace_editTitle);
        editTitle.setText(presendTitle);

        if(isAddTitle==false){
            //수정하기 경우
            okButton.setText("수정");
        }else{
            okButton.setText("시작");
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //시작버튼
                if(editTitle.getText().toString().equals("")==false) {
                    if (isAddTitle == true) {
                        //추가하기
                        traceObjcet.setTitle(editTitle.getText().toString()); //
                        SimpleDateFormat formatDate = new SimpleDateFormat("YYYY-MM-dd hh:mm");
                        String theDate = formatDate.format(new Date());
                        traceObjcet.setStartTime(theDate);

                        context.setTraceRun(true); //추적 Thread 시작
                    } else {
                        //수정하기
                        traceObjcet.setTitle(editTitle.getText().toString());
                        logDBHelper.updateTrace(traceObjcet.getId(),editTitle.getText().toString());
                        context.adapter.notifyDataSetChanged();
                    }
                    dlg.dismiss();
                }else{
                    Toast.makeText(context, "Trace이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //취소버튼
                if(isAddTitle==true) {
                    //추가하기
                    context.setTraceRun(false);
                }else{
                    //수정하기

                }
                dlg.dismiss();
            }
        });
    }

}
