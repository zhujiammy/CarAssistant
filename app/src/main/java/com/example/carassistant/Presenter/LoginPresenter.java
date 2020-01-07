package com.example.carassistant.Presenter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.Interface.LoginInterface;
import com.example.carassistant.retrofit.HttpHelper;
import com.example.carassistant.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter implements LoginInterface.Presenter{
    private Context context;
    private LoginInterface.View view;
    public LoginPresenter(Context context,LoginInterface.View view){
        this.context = context;
        this.view = view;
    }

    @Override
    public void login(Map<String,Object> map) {
       Gson gson = new Gson();
        String obj =gson.toJson(map);
        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),obj);
       Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).Login(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        if(jsonObject.get("status").getAsInt() == 0){
                            Toast.makeText(context,"登录成功",Toast.LENGTH_LONG).show();
                            JsonObject dataobject = jsonObject.get("data").getAsJsonObject();
                            Utils.setShare2(context,"token",dataobject.get("token").getAsString());
                            Log.e("TAG", "onResponse: "+jsonStr );
                            Utils.setShare2(context,"userName",dataobject.get("userName").getAsString());
                            Utils.setShare2(context,"userId",dataobject.get("userId").getAsString());
                            Utils.setShare2(context,"flag","1");

                            view.succeed();
                        }else {
                            Toast.makeText(context,"用户名或密码错误！",Toast.LENGTH_LONG).show();
                            view.failed();
                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context,"连接超时，请检查网络环境，避免影响使用！",Toast.LENGTH_SHORT).show();
                view.failed();
            }
        });

    }
}
