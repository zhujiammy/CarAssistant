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
import com.example.carassistant.utils.StringConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PartsWithdrawalActivity extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.returnListCode)
    TextView returnListCode;//退货单号
    @BindView(R.id.group_lin)
    LinearLayout group_lin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.partswithdrawalactivity);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        approvaldetail();
    }

    private void approvaldetail(){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).returnManagerdetail(getIntent().getStringExtra("returnListId"));
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
                            if(!object.get("returnListCode").isJsonNull()){
                                returnListCode.setText(object.get("returnListCode").getAsString());
                            }else {
                                returnListCode.setText("无");
                            }

                            if(!object.get("partsInfoList").isJsonNull()){
                                JsonArray partDetails = object.getAsJsonArray("partsInfoList");
                                for(int i = 0;i<partDetails.size();i++){
                                    JsonObject pardetailobj = partDetails.get(i).getAsJsonObject();
                                    View view = View.inflate(getApplicationContext(),R.layout.salesorderdetails_data,null);
                                    TextView num = view.findViewById(R.id.num);
                                    TextView partName = view.findViewById(R.id.partName);
                                    TextView partCode = view.findViewById(R.id.partCode);
                                    TextView partPrice = view.findViewById(R.id.partPrice);
                                    num.setText(""+i);
                                    if(!pardetailobj.get("partName").isJsonNull()){
                                        partName.setText(pardetailobj.get("partName").getAsString());
                                    }else {
                                        partName.setText("无");
                                    }


                                    if(!pardetailobj.get("partCode").isJsonNull()){
                                        partCode.setText(pardetailobj.get("partCode").getAsString());
                                    }else {
                                        partCode.setText("无");
                                    }


                                    if(!pardetailobj.get("remark").isJsonNull()){
                                        partPrice.setText(pardetailobj.get("remark").getAsString());
                                    }else {
                                        partPrice.setText("无");
                                    }

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
