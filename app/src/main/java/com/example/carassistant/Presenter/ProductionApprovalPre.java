package com.example.carassistant.Presenter;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.APP.CarApp;
import com.example.carassistant.Interface.ProductionApprovalInterface;
import com.example.carassistant.Interface.RetrieveVehicleInterface;
import com.example.carassistant.Interface.SProductionApprovalInterface;
import com.example.carassistant.Interface.VehicleWeigInterface;
import com.example.carassistant.Interface.VehiclesNumberInterface;
import com.example.carassistant.R;
import com.example.carassistant.View.DismantlingDetailActivity;
import com.example.carassistant.View.ExtensionDetailsActivity;
import com.example.carassistant.View.OptionsDetailsActivity;
import com.example.carassistant.View.ProductionApprovalActivity;
import com.example.carassistant.View.ProductionApprovalAty;
import com.example.carassistant.View.VehicleWeighingActivity;
import com.example.carassistant.adapter.ProductionApprovalPreAdapter;
import com.example.carassistant.adapter.RetrieveVehicleAdapter;
import com.example.carassistant.adapter.VehicleWeigAdapter;
import com.example.carassistant.adapter.VehiclesNumberAdapter;
import com.example.carassistant.adapter.productionApproveAdapter;
import com.example.carassistant.retrofit.HttpHelper;
import com.example.carassistant.utils.RecyclerViewEmptySupport;
import com.example.carassistant.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductionApprovalPre implements SProductionApprovalInterface.Presenter {
    public SProductionApprovalInterface.View view;
    public ProductionApprovalAty context;
    JsonArray jsonElements;
    private ProductionApprovalPreAdapter adapter;
    RecyclerViewEmptySupport recyclerView;
    public ProductionApprovalPre(SProductionApprovalInterface.View view,ProductionApprovalAty context,RecyclerViewEmptySupport recyclerView,ProductionApprovalPreAdapter adapter){
        this.view = view;
        this.context =context;
        this.adapter = adapter;
        this.recyclerView = recyclerView;
    }



    @Override
    public void approvallist(String pageNum,String pageSize,String state,String disType,String docCode) {
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).approvallist("1","100","2",disType,docCode);
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
                            jsonElements = object.getAsJsonArray("list");
                            adapter = new ProductionApprovalPreAdapter(context,jsonElements,2);
                            adapter.setOnitemClickListener(new ProductionApprovalPreAdapter.OnitemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    JsonObject object =jsonElements.get(position).getAsJsonObject();
                                    Intent intent = new Intent(context, DismantlingDetailActivity.class);
                                    intent.putExtra("disListId",object.get("disListId").getAsString());

                                    context.startActivity(intent);
                                }
                            });
                            recyclerView.setAdapter(adapter);
                            view.onRefresh();


                        }else {
                            Toast.makeText(context,jsonObject.get("msg").getAsString(),Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context,"连接超时，请检查网络环境，避免影响使用！",Toast.LENGTH_LONG).show();
                Log.e("TAG", "onResponse: "+"连接超时，请检查网络环境，避免影响使用！" );
            }
        });
    }

    @Override
    public void crushManagerapprovallist(String pageNum, String pageSize, String state, String docCode) {

    }


}
