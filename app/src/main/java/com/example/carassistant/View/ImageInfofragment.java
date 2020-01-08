package com.example.carassistant.View;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.APP.CarApp;
import com.example.carassistant.R;
import com.example.carassistant.retrofit.HttpHelper;
import com.example.carassistant.utils.CustomScrollViewPager;
import com.example.carassistant.utils.Utils;
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

public class ImageInfofragment extends Fragment {
    private View view;
    private CustomScrollViewPager viewPager;
    private String carDetailId;
    @BindView(R.id.pretreatmentPicture)
    LinearLayout pretreatmentPicture;//初检图片
    @BindView(R.id.destructionPicture)
    LinearLayout destructionPicture;//毁型照片

    public ImageInfofragment(CustomScrollViewPager viewPager) {
        this.viewPager = viewPager;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.imageinfo,container,false);
        viewPager.setObjectForPosition(view,0);
        ButterKnife.bind(this,view);
        Bundle bundle = getArguments();
        assert bundle != null;
        carDetailId = bundle.getString("carDetailId");
        carPicInfo();
        return view;
    }


    private void carPicInfo(){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).carPicInfo(Utils.getShared2(getActivity(),"token"),carDetailId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        if(jsonObject.get("status").getAsInt() == 0){
                            JsonObject data = jsonObject.get("data").getAsJsonObject();
                            JsonArray pretreatmentPictureaar = data.get("pretreatmentPicture").getAsJsonArray();
                            JsonArray destructionPictureaar  = data.get("destructionPicture").getAsJsonArray();

                            if(pretreatmentPictureaar.size()!=0){
                                pretreatmentPicture.removeAllViews();
                                for(int i = 0;i<pretreatmentPictureaar.size();i++){
                                    View pretreatmentPictureaview = View.inflate(getActivity(),R.layout.img_data,null);
                                    ImageView attachUrlimg = (ImageView) pretreatmentPictureaview.findViewById(R.id.attachUrl);
                                    JsonObject attachUrl = pretreatmentPictureaar.get(i).getAsJsonObject();
                                    Glide.with(getActivity()).load(attachUrl.get("attachUrl").getAsString()).into(attachUrlimg);
                                    pretreatmentPicture.addView(pretreatmentPictureaview);
                                }
                            }

                            if(destructionPictureaar.size()!=0){
                                destructionPicture.removeAllViews();
                                for(int i = 0;i<destructionPictureaar.size();i++){
                                    View destructionPictureview = View.inflate(getActivity(),R.layout.img_data,null);
                                    ImageView attachUrlimg = (ImageView) destructionPictureview.findViewById(R.id.attachUrl);
                                    JsonObject attachUrl = destructionPictureaar.get(i).getAsJsonObject();
                                    Glide.with(getActivity()).load(attachUrl.get("attachUrl").getAsString()).into(attachUrlimg);
                                    destructionPicture.addView(destructionPictureview);
                                }
                            }

                        }else {
                            Toast.makeText(getActivity(),jsonObject.get("msg").getAsString(),Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(),"连接超时，请检查网络环境，避免影响使用！",Toast.LENGTH_LONG).show();
                Log.e("TAG", "onResponse: "+"连接超时，请检查网络环境，避免影响使用！");
            }
        });
    }
}
