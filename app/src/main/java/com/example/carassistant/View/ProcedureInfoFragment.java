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

public class ProcedureInfoFragment extends Fragment {

    private View view;
    private String carDetailId;
    @BindView(R.id.belongTo)
    TextView belongTo;//车辆归属
    @BindView(R.id.carOwner)
    TextView carOwner;//车主姓名
    @BindView(R.id.ownerPhone)
    TextView ownerPhone;//车主电话
    @BindView(R.id.postcode)
    TextView postcode;//邮编
    @BindView(R.id.ownerCode)
    TextView ownerCode;//车主身份证
    @BindView(R.id.ownerAddressProvince)
    TextView ownerAddressProvince;//车主地址省
    @BindView(R.id.ownerAddressCity)
    TextView ownerAddressCity;//车主地址市
    @BindView(R.id.ownerAddressArea)
    TextView ownerAddressArea;//车主地址区
    @BindView(R.id.ownerAddress)
    TextView ownerAddress;//车主详细地址
    @BindView(R.id.dealPerson)
    TextView dealPerson;//交车人
    @BindView(R.id.dealPersonPhone)
    TextView dealPersonPhone;//交车人电话
    @BindView(R.id.dealAddressProvince)
    TextView dealAddressProvince;//交车人地址省
    @BindView(R.id.dealAddressCity)
    TextView dealAddressCity;//交车人地址市
    @BindView(R.id.dealAddressArea)
    TextView dealAddressArea;
    @BindView(R.id.dealAddress)
    TextView dealAddress;
    @BindView(R.id.drivingLicense)
    TextView drivingLicense;//行驶证
    @BindView(R.id.registerStatus)
    TextView registerStatus;//登记证
    @BindView(R.id.doSupervise)
    TextView doSupervise;//车辆性质
    @BindView(R.id.fuelOilType)
    TextView fuelOilType;//燃油类型
    @BindView(R.id.useProperty)
    TextView useProperty;//使用性质
    @BindView(R.id.carTypeName)
    TextView carTypeName;//车辆类型
    @BindView(R.id.registerTime)
    TextView registerTime;//注册日期
    @BindView(R.id.certificateTime)
    TextView certificateTime;//发证日期
    @BindView(R.id.approvedPassenger)
    TextView approvedPassenger;//核定载客
    @BindView(R.id.carDisplacement)
    TextView carDisplacement;//排量
    @BindView(R.id.carTotalWeight)
    TextView carTotalWeight;//总质量
    @BindView(R.id.curbWeight)
    TextView curbWeight;//整备质量
    @BindView(R.id.carLength)
    TextView carLength;//汽车长度
    @BindView(R.id.remark)
    TextView remark;//备注
    private CustomScrollViewPager viewPager;

    public ProcedureInfoFragment(CustomScrollViewPager viewPager) {
        this.viewPager = viewPager;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.procedureinfo,container,false);
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
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).procedureInfo(Utils.getShared2(getActivity(),"token"),carDetailId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        if(jsonObject.get("status").getAsInt() == 0){
                            if(!jsonObject.get("data").isJsonNull()){
                                JsonObject data = jsonObject.get("data").getAsJsonObject();
                                if(data.get("belongTo").getAsString().equals("1")){
                                    belongTo.setText("个人");
                                }else {
                                    belongTo.setText("单位");
                                }
                                ownerPhone.setText(data.get("ownerPhone").getAsString());
                                carOwner.setText(data.get("carOwner").getAsString());
                                postcode.setText(data.get("postcode").getAsString());
                                ownerCode.setText(data.get("ownerCode").getAsString());
                                ownerAddressProvince.setText(data.get("ownerAddressProvince").getAsString());
                                ownerAddressCity.setText(data.get("ownerAddressCity").getAsString());
                                ownerAddressArea.setText(data.get("ownerAddressArea").getAsString());
                                ownerAddress.setText(data.get("ownerAddress").getAsString());
                                dealPerson.setText(data.get("dealPerson").getAsString());
                                dealPersonPhone.setText(data.get("dealPersonPhone").getAsString());
                                dealAddressProvince.setText(data.get("dealAddressProvince").getAsString());
                                dealAddressCity.setText(data.get("dealAddressCity").getAsString());
                                dealAddressArea.setText(data.get("dealAddressArea").getAsString());
                                dealAddress.setText(data.get("dealAddress").getAsString());
                                drivingLicense.setText(data.get("drivingLicense").getAsString());
                                if(data.get("drivingLicense").getAsString().equals("0")){
                                    drivingLicense.setText("有");
                                } else if(data.get("drivingLicense").getAsString().equals("1")){
                                    drivingLicense.setText("无");
                                }else {
                                    drivingLicense.setText("登记");
                                }
                                if(data.get("registerStatus").getAsString().equals("0")){
                                    registerStatus.setText("有");
                                } else if(data.get("registerStatus").getAsString().equals("1")){
                                    registerStatus.setText("无");
                                }else {
                                    registerStatus.setText("登记");
                                }
                                useProperty.setText(data.get("useProperty").getAsString());
                                if(!data.get("carTypeName").isJsonNull()){
                                    carTypeName.setText(data.get("carTypeName").getAsString());
                                }else{
                                    carTypeName.setText("无");
                                }
                                registerTime.setText(data.get("registerTime").getAsString());
                                certificateTime.setText(data.get("certificateTime").getAsString());
                                approvedPassenger.setText(data.get("approvedPassenger").getAsString());
                                carDisplacement.setText(data.get("carDisplacement").getAsString());
                                carTotalWeight.setText(data.get("carTotalWeight").getAsString());
                                curbWeight.setText(data.get("curbWeight").getAsString());
                                carLength.setText(data.get("carLength").getAsString());
                                remark.setText(data.get("remark").getAsString());
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
