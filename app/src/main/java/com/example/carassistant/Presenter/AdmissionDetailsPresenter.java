package com.example.carassistant.Presenter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.APP.CarApp;
import com.example.carassistant.Interface.AdmissionDetailsInterface;
import com.example.carassistant.Interface.CarSourceAdmissionInterface;
import com.example.carassistant.Interface.RetrieveVehicleInterface;
import com.example.carassistant.Interface.VehiclesNumberInterface;
import com.example.carassistant.View.ExtensionDetailsActivity;
import com.example.carassistant.View.FreeAdmissionActivity;
import com.example.carassistant.View.OptionsDetailsActivity;
import com.example.carassistant.adapter.AdmissionDetailsAdapter;
import com.example.carassistant.adapter.CarSourceAdmissionAdapter;
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

public class AdmissionDetailsPresenter implements AdmissionDetailsInterface.Presenter {
    public AdmissionDetailsInterface.View view;
    public Context context;
    JsonArray jsonElements;
    private AdmissionDetailsAdapter adapter;
    RecyclerViewEmptySupport recyclerView;
    public AdmissionDetailsPresenter(AdmissionDetailsInterface.View view,Context context,RecyclerViewEmptySupport recyclerView,AdmissionDetailsAdapter adapter){
        this.view = view;
        this.context =context;
        this.adapter = adapter;
        this.recyclerView = recyclerView;
    }

    @Override
    public void getDisCarsDetails(String carsId, String condition) {
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).getDisCarsDetails(Utils.getShared2(context,"token"),carsId,condition);
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
                            adapter = new AdmissionDetailsAdapter(context,jsonElements);
                            adapter.setOnitemClickListener(new AdmissionDetailsAdapter.OnitemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    JsonObject object =jsonElements.get(position).getAsJsonObject();
                                    Intent intent = new Intent(context, FreeAdmissionActivity.class);
                                    intent.putExtra("testMainEnginePlantsNum",object.get("testMainEnginePlantsNum").getAsString());
                                    intent.putExtra("testState",object.get("testState").getAsString());
                                    intent.putExtra("carType",object.get("carType").getAsString());
                                    intent.putExtra("carDetailId",object.get("carDetailId").getAsString());
                                    if(!object.get("testMainEnginePlants").isJsonNull()){
                                        intent.putExtra("testMainEnginePlants",object.get("testMainEnginePlants").getAsString());
                                    }else {
                                        intent.putExtra("testMainEnginePlants","无");
                                    }

                                    intent.putExtra("type","2");
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
