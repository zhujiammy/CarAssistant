package com.example.carassistant.Presenter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.APP.CarApp;
import com.example.carassistant.Interface.RetrieveVehicleInterface;
import com.example.carassistant.Interface.VehiclesNumberInterface;
import com.example.carassistant.View.ExtensionDetailsActivity;
import com.example.carassistant.View.OptionsDetailsActivity;
import com.example.carassistant.adapter.RetrieveVehicleAdapter;
import com.example.carassistant.adapter.VehiclesNumberAdapter;
import com.example.carassistant.retrofit.HttpHelper;
import com.example.carassistant.utils.RecyclerViewEmptySupport;
import com.example.carassistant.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehiclesNumberPresenter implements VehiclesNumberInterface.Presenter {
    public VehiclesNumberInterface.View view;
    public Context context;
    JsonArray jsonElements;
    private VehiclesNumberAdapter adapter;
    RecyclerViewEmptySupport recyclerView;
    public VehiclesNumberPresenter(VehiclesNumberInterface.View view,Context context,RecyclerViewEmptySupport recyclerView,VehiclesNumberAdapter adapter){
        this.view = view;
        this.context =context;
        this.adapter = adapter;
        this.recyclerView = recyclerView;
    }

    @Override
    public void searchNeedPrintNoCars(String searchInfo) {
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).searchNeedPrintNoCars(Utils.getShared2(context,"token"),searchInfo);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        Log.e("TAG", "onResponse: "+jsonStr );
                        if(jsonObject.get("status").getAsInt() == 0){
                            jsonElements = jsonObject.getAsJsonArray("data");
                            adapter = new VehiclesNumberAdapter(context,jsonElements,1);
                            adapter.setOnitemClickListener(new VehiclesNumberAdapter.OnitemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                   JsonObject object =jsonElements.get(position).getAsJsonObject();
                                    Intent intent = new Intent(context, ExtensionDetailsActivity.class);
                                    intent.putExtra("type","1");
                                    intent.putExtra("carDetailId",object.get("carDetailId").getAsString());
                                    intent.putExtra("plateNumberNo",object.get("plateNumberNo").getAsString());
                                    if(!object.get("carType").isJsonNull()){
                                        intent.putExtra("carType",object.get("carType").getAsString());
                                    }else {
                                        intent.putExtra("carType","null");
                                    }
                                    if(!object.get("enterTime").isJsonNull()){
                                        intent.putExtra("erterTime",object.get("enterTime").getAsString());
                                    }else {
                                        intent.putExtra("erterTime","null");
                                    }
                                    if(!object.get("carCode").isJsonNull()){
                                        intent.putExtra("carCode",object.get("carCode").getAsString());
                                    }else {
                                        intent.putExtra("carCode","null");
                                    }

                                    if(!object.get("plateNumberColour").isJsonNull()){
                                        intent.putExtra("plateNumberColour",object.get("plateNumberColour").getAsString());
                                    }else {
                                        intent.putExtra("plateNumberColour","null");
                                    }
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
}
