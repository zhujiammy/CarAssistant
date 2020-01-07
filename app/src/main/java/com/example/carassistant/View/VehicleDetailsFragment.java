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

public class VehicleDetailsFragment extends Fragment {

    private View view;
    private String carDetailId;
    @BindView(R.id.enterTime)
    TextView enterTime;//入场时间
    @BindView(R.id.enterOperator)
    TextView enterOperator;//入场经手人
    @BindView(R.id.carAddress)
    TextView carAddress;//车辆存放区域
    @BindView(R.id.plateNumber)
    TextView plateNumber;//车牌数量
    @BindView(R.id.newoldLevel)
    TextView newoldLevel;//新旧程度
    @BindView(R.id.remark)
    TextView remark;//  备注
    @BindView(R.id.airConditioningPumpAmount)
    TextView airConditioningPumpAmount;//空调泵数
    @BindView(R.id.carBatteryAmount)
    TextView carBatteryAmount;//电池
    @BindView(R.id.carMotorAmount)
    TextView carMotorAmount;//马达
    @BindView(R.id.carDoorAmount)
    TextView carDoorAmount;//车门
    @BindView(R.id.carAluminumRingAmount)
    TextView carAluminumRingAmount;//铝圈数量
    @BindView(R.id.carMotorsAmount)
    TextView carMotorsAmount;//电机
    @BindView(R.id.isCarAluminumRing)
    TextView isCarAluminumRing;//轮毂是否是铝圈
    @BindView(R.id.carWaterTankAmount)
    TextView carWaterTankAmount;//水箱
    @BindView(R.id.weightOdd)
    TextView weightOdd;//磅单编号
    @BindView(R.id.weight)
    TextView weight;//磅单重量
    private CustomScrollViewPager viewPager;

    public VehicleDetailsFragment(CustomScrollViewPager viewPager) {
        this.viewPager = viewPager;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.vehicledetails,container,false);
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
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).carInfo(Utils.getShared2(getActivity(),"token"),carDetailId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        if(jsonObject.get("status").getAsInt() == 0){
                            JsonObject data = jsonObject.get("data").getAsJsonObject();
                            enterTime.setText(data.get("enterTime").getAsString());
                            if(!data.get("enterOperator").isJsonNull()){
                                enterOperator.setText(data.get("enterOperator").getAsString());
                            }else {
                                enterOperator.setText("无");
                            }
                            if(!data.get("carAddress").isJsonNull()){
                                carAddress.setText(data.get("carAddress").getAsString());
                            }else {
                                carAddress.setText("无");
                            }
                            if(!data.get("plateNumber").isJsonNull()){
                                plateNumber.setText(data.get("plateNumber").getAsString());
                            }else {
                                plateNumber.setText("无");
                            }
                            if(!data.get("newoldLevel").isJsonNull()){
                                newoldLevel.setText(data.get("newoldLevel").getAsString());
                            }else {
                                newoldLevel.setText("无");
                            }
                            if(!data.get("remark").isJsonNull()){
                                remark.setText(data.get("remark").getAsString());
                            }else {
                                remark.setText("无");
                            }

                            if(!data.get("airConditioningPumpAmount").isJsonNull()){
                                airConditioningPumpAmount.setText(data.get("airConditioningPumpAmount").getAsString());
                            }else {
                                airConditioningPumpAmount.setText("无");
                            }
                            if(!data.get("carBatteryAmount").isJsonNull()){
                                carBatteryAmount.setText(data.get("carBatteryAmount").getAsString());
                            }else {
                                carBatteryAmount.setText("无");
                            }
                            if(!data.get("carMotorAmount").isJsonNull()){
                                carMotorAmount.setText(data.get("carMotorAmount").getAsString());
                            }else {
                                carMotorAmount.setText("无");
                            }

                            if(!data.get("carDoorAmount").isJsonNull()){
                                carDoorAmount.setText(data.get("carDoorAmount").getAsString());
                            }else {
                                carDoorAmount.setText("无");
                            }
                            if(!data.get("carAluminumRingAmount").isJsonNull()){
                                carAluminumRingAmount.setText(data.get("carAluminumRingAmount").getAsString());
                            }else {
                                carAluminumRingAmount.setText("无");
                            }
                            if(!data.get("carMotorsAmount").isJsonNull()){
                                carMotorsAmount.setText(data.get("carMotorsAmount").getAsString());
                            }else {
                                carMotorsAmount.setText("无");
                            }
                            if(!data.get("isCarAluminumRing").isJsonNull()){
                                isCarAluminumRing.setText(data.get("isCarAluminumRing").getAsString());
                            }else {
                                isCarAluminumRing.setText("无");
                            }
                            if(!data.get("carWaterTankAmount").isJsonNull()){
                                carWaterTankAmount.setText(data.get("carWaterTankAmount").getAsString());
                            }else {
                                carWaterTankAmount.setText("无");
                            }


                            if(!data.get("weightOdd").isJsonNull()){
                                weightOdd.setText(data.get("weightOdd").getAsString());
                            }else {
                                weightOdd.setText("无");
                            }
                            if(!data.get("weight").isJsonNull()){
                                weight.setText(data.get("weight").getAsString());
                            }else {
                                weight.setText("无");
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
