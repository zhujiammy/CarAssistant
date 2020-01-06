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

public class CarSourceInfofragment extends Fragment {

    private View view;
    private String carDetailId;
    @BindView(R.id.linkman)
    TextView linkman;//联系人姓名
    @BindView(R.id.linkmanPhone)
    TextView linkmanPhone;//联系人电话
    @BindView(R.id.carType)
    TextView carType;//车型
    @BindView(R.id.plateNumberNo)
    TextView plateNumberNo;//车牌号
    @BindView(R.id.managerType)
    TextView managerType;//处理方式
    @BindView(R.id.managerDate)
    TextView managerDate;//处理日期
    @BindView(R.id.procedureWay)
    TextView procedureWay;//手续获取方式
    @BindView(R.id.drivingLicense)
    TextView drivingLicense;//行驶本
    @BindView(R.id.registerStatus)
    TextView registerStatus;//登记证
    @BindView(R.id.copyOfIdCard)
    TextView copyOfIdCard;//身份证复印件
    @BindView(R.id.copyOfBusinessLicense)
    TextView copyOfBusinessLicense;//营业执照复印件
    @BindView(R.id.carScrapTable)
    TextView carScrapTable;//车辆报废表
    @BindView(R.id.carAccidentProof)
    TextView carAccidentProof;//车辆事故证明
    @BindView(R.id.remark)
    TextView remark;//备注
    private CustomScrollViewPager viewPager;

    public CarSourceInfofragment(CustomScrollViewPager viewPager) {
        this.viewPager = viewPager;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.carsourceinfo,container,false);
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
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).carSourceInfo(Utils.getShared2(getActivity(),"token"),carDetailId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        if(jsonObject.get("status").getAsInt() == 0){
                            JsonObject data = jsonObject.get("data").getAsJsonObject();
                            if(!data.get("linkman").isJsonNull()){
                                linkman.setText(data.get("linkman").getAsString());
                            }else {
                                linkman.setText("无");
                            }
                            if(!data.get("linkmanPhone").isJsonNull()){
                                linkmanPhone.setText(data.get("linkmanPhone").getAsString());
                            }else {
                                linkmanPhone.setText("无");
                            }
                            if(!data.get("plateNumberNo").isJsonNull()){
                                plateNumberNo.setText(data.get("plateNumberNo").getAsString());
                            }else {
                                plateNumberNo.setText("无");
                            }
                            carType.setText(data.get("carType").getAsString());
                            if(!data.get("managerType").isJsonNull()){
                                if(data.get("managerType").getAsString().equals("1")){
                                    managerType.setText("拖车");
                                }
                                if(data.get("managerType").getAsString().equals("2")){
                                    managerType.setText("代驾");
                                }
                                if(data.get("managerType").getAsString().equals("3")){
                                    managerType.setText("自送");
                                }
                                if(data.get("managerType").getAsString().equals("4")){
                                    managerType.setText("上门开车");
                                }
                            }else {
                                managerType.setText("无");
                            }
                            if(!data.get("managerDate").isJsonNull()){
                                managerDate.setText(data.get("managerDate").getAsString());
                            }else {
                                managerDate.setText("无");
                            }
                            if(!data.get("procedureWay").isJsonNull()){
                                procedureWay.setText(data.get("procedureWay").getAsString());
                            }else {
                                procedureWay.setText("无");
                            }


                            drivingLicense.setText(data.get("drivingLicense").getAsString());
                            if(data.get("drivingLicense").getAsString().equals("1")){
                                drivingLicense.setText("有");
                            } else if(data.get("drivingLicense").getAsString().equals("2")){
                                drivingLicense.setText("无");
                            }else {
                                drivingLicense.setText("补");
                            }

                            if(data.get("registerStatus").getAsString().equals("1")){
                                registerStatus.setText("有");
                            } else if(data.get("registerStatus").getAsString().equals("2")){
                                registerStatus.setText("无");
                            }else {
                                registerStatus.setText("补");
                            }
                            if(data.get("copyOfIdCard").getAsString().equals("1")){
                                copyOfIdCard.setText("有");
                            }
                            if(data.get("copyOfIdCard").getAsString().equals("2")){
                                copyOfIdCard.setText("无");
                            }
                            if(data.get("copyOfIdCard").getAsString().equals("3")){
                                copyOfIdCard.setText("补");
                            }
                            if(data.get("copyOfBusinessLicense").getAsString().equals("1")){
                                copyOfBusinessLicense.setText("有");
                            }
                            if(data.get("copyOfBusinessLicense").getAsString().equals("2")){
                                copyOfBusinessLicense.setText("无");
                            }
                            if(data.get("copyOfBusinessLicense").getAsString().equals("3")){
                                copyOfBusinessLicense.setText("补");
                            }
                            if(data.get("carScrapTable").getAsString().equals("1")){
                                carScrapTable.setText("有");
                            }
                            if(data.get("carScrapTable").getAsString().equals("2")){
                                carScrapTable.setText("无");
                            }
                            if(data.get("carScrapTable").getAsString().equals("3")){
                                carScrapTable.setText("补");
                            }
                            if(data.get("carAccidentProof").getAsString().equals("1")){
                                carAccidentProof.setText("有");
                            }
                            if(data.get("carAccidentProof").getAsString().equals("2")){
                                carAccidentProof.setText("无");
                            }
                            if(data.get("carAccidentProof").getAsString().equals("3")){
                                carAccidentProof.setText("补");
                            }
                            if(!data.get("remark").isJsonNull()){
                                remark.setText(data.get("remark").getAsString());
                            }else {
                                remark.setText("无");
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
