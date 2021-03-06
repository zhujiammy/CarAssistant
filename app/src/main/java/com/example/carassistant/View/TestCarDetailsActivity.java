package com.example.carassistant.View;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.APP.CarApp;
import com.example.carassistant.Model.Trace;
import com.example.carassistant.R;
import com.example.carassistant.adapter.Adapter;
import com.example.carassistant.adapter.TraceListAdapter;
import com.example.carassistant.retrofit.HttpHelper;
import com.example.carassistant.utils.CustomListView;
import com.example.carassistant.utils.CustomScrollViewPager;
import com.example.carassistant.utils.HoldTabScrollView;
import com.example.carassistant.utils.MyScrollView;
import com.example.carassistant.utils.Utils;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestCarDetailsActivity extends AppCompatActivity implements HoldTabScrollView.OnHoldTabScrollViewScrollChanged {

    @BindView(R.id.carCode)
    TextView carCode;//车辆编号
    @BindView(R.id.testMainEnginePlants)
    TextView testMainEnginePlants;//车牌号
    @BindView(R.id.testMainEnginePlantsNum)
    TextView testMainEnginePlantsNum;//车牌颜色
    @BindView(R.id.carColour)
    TextView carColour;//车身颜色
    @BindView(R.id.carBrandName)
    TextView carBrandName;//品牌型号
    @BindView(R.id.carVin)
    TextView carVin;//车架号
    @BindView(R.id.engineNo)
    TextView engineNo;//发动机号
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private String carDetailId;//id
    @BindView(R.id.lvTrace)
    CustomListView lvTrace;//时间轴
    @BindView(R.id.my_scrollview)
    HoldTabScrollView my_scrollview;
    private List<Trace> traceList = new ArrayList<>();
    private TraceListAdapter adapter;
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.view_pager)
    CustomScrollViewPager viewPager;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<>();
    private int mHeight = 0;
    private boolean canJump = true;
    @BindView(R.id.rl_center)
    RelativeLayout centerRl;
    @BindView(R.id.rl_top)
    RelativeLayout topRl;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.testsocialcardetails);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        carDetailId = getIntent().getStringExtra("carDetailId");
        Log.e("TAG", "onCreate: "+carDetailId );
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        lvTrace.measure(w, h);
        my_scrollview.setOnObservableScrollViewScrollChanged(this);
/*        my_scrollview.setAdjustDistance(width,height);
        my_scrollview.setListView(lvTrace);*/
        socialCarBaseInfo();
        carProcessInfo();
        initUI();


    }
    private void initUI(){
        Bundle bundle = new Bundle();
        bundle.putString("carDetailId",carDetailId);
        VehicleDetailsFragment vehicleDetailsFragment = new VehicleDetailsFragment(viewPager);
        vehicleDetailsFragment.setArguments(bundle);

        DisassemblyInfofragment disassemblyInfofragment = new DisassemblyInfofragment(viewPager);
        disassemblyInfofragment.setArguments(bundle);

        ImageInfofragment imageInfofragment = new ImageInfofragment(viewPager);
        imageInfofragment.setArguments(bundle);
        mFragments.add(vehicleDetailsFragment);
        mFragments.add(disassemblyInfofragment);
        mFragments.add(imageInfofragment);

        list.add("车辆信息");
        list.add("拆解信息");
        list.add("图片信息");

        tablayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(new Adapter(getSupportFragmentManager(),mFragments,list));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.resetHeight(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void socialCarBaseInfo(){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).testCarBaseInfo(Utils.getShared2(getApplicationContext(),"token"),carDetailId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        if(jsonObject.get("status").getAsInt() == 0){
                            JsonObject data = jsonObject.get("data").getAsJsonObject();
                            if(!jsonObject.get("data").isJsonNull()){
                                carCode.setText(data.get("carCode").getAsString());
                                if(!data.get("testMainEnginePlants").isJsonNull()){
                                    testMainEnginePlants.setText(data.get("testMainEnginePlants").getAsString());
                                }else {
                                    testMainEnginePlants.setText("无");
                                }
                                if(!data.get("testMainEnginePlantsNum").isJsonNull()){
                                    testMainEnginePlantsNum.setText(data.get("testMainEnginePlantsNum").getAsString());
                                }else {
                                    testMainEnginePlantsNum.setText("无");
                                }

                                if(!data.get("carColour").isJsonNull()){
                                    carColour.setText(data.get("carColour").getAsString());
                                }else {
                                    carColour.setText("无");
                                }

                                if(!data.get("carBrandName").isJsonNull()){
                                    carBrandName.setText(data.get("carBrandName").getAsString());
                                }else {
                                    carBrandName.setText("无");
                                }

                                if(!data.get("carVin").isJsonNull()){
                                    carVin.setText(data.get("carVin").getAsString());
                                }else {
                                    carVin.setText("无");
                                }

                                if(!data.get("engineNo").isJsonNull()){
                                    engineNo.setText(data.get("engineNo").getAsString());
                                }else {
                                    engineNo.setText("无");
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
                Log.e("TAG", "onResponse: "+"连接超时，请检查网络环境，避免影响使用！");
            }
        });
    }
    private void carProcessInfo(){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).carProcessInfo(Utils.getShared2(getApplicationContext(),"token"),carDetailId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        if(jsonObject.get("status").getAsInt() == 0){
                            JsonArray data = jsonObject.get("data").getAsJsonArray();
                            traceList.clear();
                            for(int i = 0;i<data.size();i++){
                                Trace trace = new Trace();
                                JsonObject object =data.get(i).getAsJsonObject();
                                trace.setAcceptTime(object.get("processDate").getAsString());
                                trace.setAcceptStation(object.get("processName").getAsString());
                                if(!object.get("processor").isJsonNull()){
                                    trace.setProcessor(object.get("processor").getAsString());
                                }else {
                                    trace.setProcessor("无");
                                }

                                traceList.add(trace);
                            }

                            adapter = new TraceListAdapter(getApplicationContext(), traceList);
                            lvTrace.setAdapter(adapter);

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
                Log.e("TAG", "onResponse: "+"连接超时，请检查网络环境，避免影响使用！");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            //获取HeaderView的高度，当滑动大于等于这个高度的时候，需要把topView移除当前布局，放入到外层布局
            mHeight = centerRl.getTop();
        }
    }

    @Override
    public void onObservableScrollViewScrollChanged(int l, int t, int oldl, int oldt) {
        if (t >= mHeight) {
            if (tablayout.getParent() != topRl) {
                centerRl.removeView(tablayout);
                topRl.addView(tablayout);
                canJump = false;
            }
        } else {
            if (tablayout.getParent() != centerRl) {
                topRl.removeView(tablayout);
                centerRl.addView(tablayout);
                canJump = true;
            }
        }
        if (canJump && t >= oldt) {
            my_scrollview.smoothScrollToSlow(0, mHeight, 300);
        } else if (canJump && t < oldt) {
            my_scrollview.smoothScrollToSlow(0, 0, 300);
        }
    }

}
