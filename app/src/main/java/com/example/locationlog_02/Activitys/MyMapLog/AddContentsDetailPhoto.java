package com.example.locationlog_02.Activitys.MyMapLog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.locationlog_02.Activitys.MyMapLog.fragment.DetailImageFragment;
import com.example.locationlog_02.Activitys.MyMapLog.fragment.DetailImgaeFragmentAdapter;
import com.example.locationlog_02.Manager.ImageManager;
import com.example.locationlog_02.R;

import java.util.ArrayList;

public class AddContentsDetailPhoto extends AppCompatActivity implements DetailImageFragment.OnFragmentInteractionListener {

    ViewPager viewPager;
    DetailImgaeFragmentAdapter fragmentAdapter;

    //uri Image
    ArrayList<String> uriList;

    //Manager
    ImageManager imageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contents_detail_photo);

        //actionBar
        ActionBar myActionBar = getSupportActionBar();
        myActionBar.setTitle("사진");
        myActionBar.setDisplayHomeAsUpEnabled(true);

        //Initializaion
        imageManager = new ImageManager(this);

        //Intent
        Intent momIntent = getIntent();
        uriList=momIntent.getStringArrayListExtra("imageList");

        setViewPager();
    }

    //------------------------------------------------------------------------------------------------뷰페이저
    private void setViewPager() {
        viewPager = findViewById(R.id.contentsDetailPhoto_viewPager);
        fragmentAdapter = new DetailImgaeFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentAdapter);

        for (int i = 0; i < uriList.size(); i++) {
            DetailImageFragment imageFragment = new DetailImageFragment(this);
            Bundle bundle = new Bundle();
//            Uri uri = Uri.parse(uriList.get(i));
//            Log.e("detail_Uri", String.valueOf(uri));
//            bundle.putString("img",imageManager.getBase64String(imageManager.resize(this,uri,500)));
            bundle.putString("img",uriList.get(i));
            imageFragment.setArguments(bundle);
            fragmentAdapter.addItem(imageFragment);
        }
        fragmentAdapter.notifyDataSetChanged();
    }
    //------------------------------------------------------------------------------------------------기능

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //------------------------------------------------------------------------------------------------Back
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

