package com.example.carassistant.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.APP.CarApp;
import com.example.carassistant.R;
import com.example.carassistant.retrofit.HttpHelper;
import com.example.carassistant.utils.CustomScrollViewPager;
import com.example.carassistant.utils.Utils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisassemblyInfofragment extends Fragment {

    private View view;
    private String carDetailId;
    @BindView(R.id.dismantleType)
    TextView dismantleType;//拆解方式
    @BindView(R.id.determineTime)
    TextView determineTime;//确定拆解方式时间
    @BindView(R.id.determinePerson)
    TextView determinePerson;//拆解方式确认人

    private CustomScrollViewPager viewPager;

    public DisassemblyInfofragment(CustomScrollViewPager viewPager) {
        this.viewPager = viewPager;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.disassemblyinfo,container,false);
        viewPager.setObjectForPosition(view,0);
        ButterKnife.bind(this,view);
        Bundle bundle = getArguments();
        assert bundle != null;
        carDetailId = bundle.getString("carDetailId");
        Log.e("VehicleDetailsFragment", "VehicleDetailsFragment: "+carDetailId );
        carInfo();
        return view;

    }

    private void carInfo(){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).dismantlingInfo(Utils.getShared2(getActivity(),"token"),carDetailId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        if(jsonObject.get("status").getAsInt() == 0){
                            JsonObject data = jsonObject.get("data").getAsJsonObject();
                            if(!data.get("dismantleType").isJsonNull()){
                                dismantleType.setText(data.get("dismantleType").getAsString());
                            }else {
                                dismantleType.setText("无");
                            }
                            if(!data.get("determineTime").isJsonNull()){
                                determineTime.setText(data.get("determineTime").getAsString());
                            }else {
                                determineTime.setText("无");
                            }

                            if(!data.get("determinePerson").isJsonNull()){
                                determinePerson.setText(data.get("determinePerson").getAsString());
                            }else {
                                determinePerson.setText("无");
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
                Toast.makeText(getActivity(),t.getMessage(),Toast.LENGTH_LONG).show();
                Log.e("TAG", "onResponse: "+t.getMessage());
            }
        });
    }
}
