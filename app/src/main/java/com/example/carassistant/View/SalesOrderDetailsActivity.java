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

public class SalesOrderDetailsActivity extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.orderNumber)
    TextView orderNumber;//销售单号
    @BindView(R.id.clientName)
    TextView clientName;//客户名称
    @BindView(R.id.linkManName)
    TextView linkManName;//联系人
    @BindView(R.id.linkManPhone)
    TextView linkManPhone;//联系电话
    @BindView(R.id.address)
    TextView address;//地址
    @BindView(R.id.invoiceType)
    TextView invoiceType;//是否开发票
    @BindView(R.id.totalPrice)
    TextView totalPrice;//总价
    @BindView(R.id.group_lin)
    LinearLayout group_lin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.salesorderdetailsactivity);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        approvaldetail();
    }

    private void approvaldetail(){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).saledetail(getIntent().getStringExtra("saleListId"));
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
                            if(!object.get("orderNumber").isJsonNull()){
                                orderNumber.setText(object.get("orderNumber").getAsString());
                            }else {
                                orderNumber.setText("无");
                            }

                            if(!object.get("clientName").isJsonNull()){
                                clientName.setText(object.get("clientName").getAsString());
                            }else {
                                clientName.setText("无");
                            }

                            if(!object.get("linkManName").isJsonNull()){
                                linkManName.setText(object.get("linkManName").getAsString());
                            }else {
                                linkManName.setText("无");
                            }
                            if(!object.get("linkManPhone").isJsonNull()){
                                linkManPhone.setText(object.get("linkManPhone").getAsString());
                            }else {
                                linkManPhone.setText("无");
                            }

                            if(!object.get("totalPrice").isJsonNull()){
                                totalPrice.setText("总价："+object.get("totalPrice").getAsString());
                            }else {
                                totalPrice.setText("无");
                            }

                            if(!object.get("invoiceType").isJsonNull()){
                               if(object.get("invoiceType").equals("0")){
                                   invoiceType.setText("不开票");
                               }else if(object.get("invoiceType").equals("1")){
                                   invoiceType.setText("增值发票");
                               }else {
                                   invoiceType.setText("普通发票");
                               }
                            }else {
                                invoiceType.setText("无");
                            }

                            if(!object.get("province").isJsonNull()|| !object.get("city").isJsonNull()|| !object.get("area").isJsonNull() || !object.get("address").isJsonNull()){
                              address.setText(object.get("province").getAsString()+object.get("city").getAsString()+object.get("area").getAsString()+object.get("address").getAsString());
                            }else {
                                address.setText("无");
                            }


                            if(!object.get("partDetails").isJsonNull()){
                                JsonArray partDetails = object.getAsJsonArray("partDetails");
                                for(int i = 0;i<partDetails.size();i++){
                                    JsonObject pardetailobj = partDetails.get(i).getAsJsonObject();
                                    View view = View.inflate(getApplicationContext(),R.layout.salesorderdetails_data,null);
                                    TextView num = view.findViewById(R.id.num);
                                    TextView partName = view.findViewById(R.id.partName);
                                    TextView partCode = view.findViewById(R.id.partCode);
                                    TextView partPrice = view.findViewById(R.id.partPrice);
                                    int sum = i+1;
                                    num.setText(""+sum);
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


                                    if(!pardetailobj.get("partPrice").isJsonNull()){
                                        partPrice.setText(pardetailobj.get("partPrice").getAsString());
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
