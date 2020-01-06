package com.example.carassistant.View;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.carassistant.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestVehicleEntryActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btn1)
    RelativeLayout btn1;//散车入场
    @BindView(R.id.btn2)
    RelativeLayout btn2;//车源信息入场
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.testvehicleentry);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v == btn1){
            //散车入场
            Intent intent = new Intent(getApplicationContext(),FreeAdmissionActivity.class);
            intent.putExtra("type","1");
            startActivity(intent);

        }
        if(v == btn2){
            //车源信息入场
            Intent intent = new Intent(getApplicationContext(),CarSourceAdmissionActivity.class);
            startActivity(intent);
        }
    }
}
