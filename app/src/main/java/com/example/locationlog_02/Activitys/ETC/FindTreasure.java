package com.example.locationlog_02.Activitys.ETC;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.locationlog_02.R;

public class FindTreasure extends AppCompatActivity {

    //state
    int level=4;

    ImageView treasureImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_treasure);

        //App Bar 감추기
        ActionBar ab = getSupportActionBar();
        ab.hide();

        Intent momIntent = getIntent();
        level=momIntent.getIntExtra("Level",4);

        setLevelReward();
    }

    private void setLevelReward() {
        treasureImageView = findViewById(R.id.treasure_Image);

        switch (level){
            case 0:
                break;
            case 1: //낮은보상
                treasureImageView.setImageDrawable(getResources().getDrawable(R.drawable.lv1_reward));
                break;
            case 2://Lv 2보상
                treasureImageView.setImageDrawable(getResources().getDrawable(R.drawable.lv2_reward));
                break;
            case 3: //Lv 3보상
                treasureImageView.setImageDrawable(getResources().getDrawable(R.drawable.lv3_reward));
                break;
            case 4: //최고보상
                treasureImageView.setImageDrawable(getResources().getDrawable(R.drawable.lv4_reward));
                break;
        }
    }

    public void close(View view){
        finish();
    }
}
