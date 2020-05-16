package com.example.locationlog_02.PublicDialog;

import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.locationlog_02.Activitys.MyMapTrace.MyMapTrace;
import com.example.locationlog_02.Activitys.MyMapTrace.Recyclerview.TraceLogData;
import com.example.locationlog_02.R;


public class TextDialog {
    private MyMapTrace context;
    private TraceLogData traceLogData;
    public TextDialog(MyMapTrace context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction() {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_maptrace_add);
        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.

        final Button okButton = (Button) dlg.findViewById(R.id.dialog_publicText_okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.dialog_publicText_cancelButton);
        final TextView titleText = dlg.findViewById(R.id.dialog_publicText_title);
        final TextView textView =  dlg.findViewById(R.id.dialog_publicText_text);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //확인버튼

                    dlg.dismiss();

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //취소버튼

                dlg.dismiss();
            }
        });
    }

    public TraceLogData getTraceLogData() {
        return traceLogData;
    }

    public void setTraceLogData(TraceLogData traceLogData) {
        this.traceLogData = traceLogData;
    }
}

