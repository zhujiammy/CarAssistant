package com.example.carassistant.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.R;
import com.example.carassistant.retrofit.HttpHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DismantlingDetailActivity extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.carCode)
    TextView carCode;//车联编号
    @BindView(R.id.partName)
    TextView partName;//配件名称
    @BindView(R.id.testMainEnginePlants)
    TextView testMainEnginePlants;//主机厂名称
    @BindView(R.id.plateNumberNo)
    TextView plateNumberNo;//车牌号
    @BindView(R.id.enterTime)
    TextView enterTime;//入场时间
    @BindView(R.id.remark)
    TextView remark;//备注
    @BindView(R.id.partsNum)
    TextView partsNum;//配件总数
    @BindView(R.id.createDate)
    TextView createDate;//创建时间
    @BindView(R.id.createPerson)
    TextView createPerson;//创建人
    @BindView(R.id.group_lin)
    LinearLayout group_lin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.dismantlingdetail);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        approvaldetail();
    }

    private void approvaldetail(){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).approvaldetail(getIntent().getStringExtra("disListId"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        Log.e("TAG", "onResponse: "+jsonStr );
                        if(jsonObject.get("status").getAsInt() == 0){
                            JsonObject object = jsonObject.getAsJsonObject("data").getAsJsonObject();
                            if(!object.get("carCode").isJsonNull()){
                                carCode.setText(object.get("carCode").getAsString());
                            }else {
                                carCode.setText("无");
                            }

                            if(!object.get("partName").isJsonNull()){
                                partName.setText(object.get("partName").getAsString());
                            }else {
                                partName.setText("无");
                            }

                            if(!object.get("testMainEnginePlants").isJsonNull()){
                                testMainEnginePlants.setText(object.get("testMainEnginePlants").getAsString());
                            }else {
                                testMainEnginePlants.setText("无");
                            }
                            if(!object.get("plateNumberNo").isJsonNull()){
                                plateNumberNo.setText(object.get("plateNumberNo").getAsString());
                            }else {
                                plateNumberNo.setText("无");
                            }

                            if(!object.get("enterTime").isJsonNull()){
                                enterTime.setText(object.get("enterTime").getAsString());
                            }else {
                                enterTime.setText("无");
                            }

                            if(!object.get("remark").isJsonNull()){
                                remark.setText(object.get("remark").getAsString());
                            }else {
                                remark.setText("无");
                            }

                            if(!object.get("partName").isJsonNull()){
                                partsNum.setText(object.get("partName").getAsString());
                            }else {
                                partsNum.setText("无");
                            }
                            if(!object.get("createDate").isJsonNull()){
                                createDate.setText(object.get("createDate").getAsString());
                            }else {
                                createDate.setText("无");
                            }
                            if(!object.get("createPerson").isJsonNull()){
                                createPerson.setText(object.get("createPerson").getAsString());
                            }else {
                                createPerson.setText("无");
                            }

                            if(!object.get("partDetails").isJsonNull()){
                                for(int i = 0;i<object.get("partDetails").getAsJsonArray().size();i++){
                                    View view = View.inflate(getApplicationContext(),R.layout.accessories_details,null);
                                    TextView num = view.findViewById(R.id.num);
                                    TextView name = view.findViewById(R.id.name);
                                    num.setText(""+i);
                                    name.setText(object.get("partDetails").getAsJsonArray().get(i).toString());
                                    group_lin.addView(view);
                                }
                            }


                        }else {
                            Toast.makeText(getApplicationContext(),jsonObject.get("msg").getAsString(),Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"连接超时，请检查网络环境，避免影响使用！",Toast.LENGTH_LONG).show();
                Log.e("TAG", "onResponse: "+"连接超时，请检查网络环境，避免影响使用！" );
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
