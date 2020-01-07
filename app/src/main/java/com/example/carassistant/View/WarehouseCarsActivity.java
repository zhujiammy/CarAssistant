package com.example.carassistant.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.APP.CarApp;
import com.example.carassistant.Interface.RetrieveVehicleInterface;
import com.example.carassistant.Interface.VehicleDismantlingInterface;
import com.example.carassistant.Interface.VehicleHandInterface;
import com.example.carassistant.Interface.VehiclesNumberInterface;
import com.example.carassistant.Interface.WarehouseCarsInterface;
import com.example.carassistant.Presenter.RetrieveVehiclePresenter;
import com.example.carassistant.Presenter.TestVehicleDismantlingPresenter;
import com.example.carassistant.Presenter.VehicleDismantlingPresenter;
import com.example.carassistant.Presenter.VehicleHandPresenter;
import com.example.carassistant.Presenter.VehiclesNumberPresenter;
import com.example.carassistant.Presenter.WarehouseCarsPresenter;
import com.example.carassistant.R;
import com.example.carassistant.adapter.VehicleDismantlingAdapter;
import com.example.carassistant.adapter.VehicleHandAdapter;
import com.example.carassistant.adapter.VehiclesNumberAdapter;
import com.example.carassistant.adapter.WarehouseCarsAdapter;
import com.example.carassistant.retrofit.HttpHelper;
import com.example.carassistant.utils.RecyclerViewEmptySupport;
import com.example.carassistant.utils.Utils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


//试验车辆入库
public class WarehouseCarsActivity extends AppCompatActivity implements WarehouseCarsInterface.View, View.OnClickListener {

    @BindView(R.id.serach_btn)
    EditText serach_btn;//搜索
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerViewEmptySupport recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private WarehouseCarsAdapter adapter;
    private static  int REQUEST_CODE = 1001;
    private WarehouseCarsPresenter presenter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.warehousecars);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        initUI();
        presenter = new WarehouseCarsPresenter(this,this,recyclerView,adapter);
        presenter.getWarehouseCars(null);
    }



    private void initUI(){
        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                presenter.getWarehouseCars(null);
                serach_btn.setText("");
            }
        });
        serach_btn.addTextChangedListener(watcher);

    }

    //搜索
    TextWatcher watcher  = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            presenter.getWarehouseCars(s.toString());
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        CarApp carApp = (CarApp)getApplication();
        if(carApp.isRefresh()){
            refreshLayout.autoRefresh();
            carApp.setRefresh(false);
        }
    }
    @Override
    public void onClick(View v) {

    }

    @Override
    public void succeed() {

    }

    @Override
    public void failed() {
        refreshLayout.finishRefresh(true);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        refreshLayout.finishRefresh();//结束刷新
    }

    @Override
    public void onLoadMore() {
        adapter.notifyDataSetChanged();
        refreshLayout.finishLoadMore(true);
    }

    @Override
    public void onNothingData() {
        //没有更多数据了
        refreshLayout.finishLoadMoreWithNoMoreData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.qrmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        if (id == R.id.qr) {
            //扫描仓库位置二维码
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // 没有权限，申请权限。
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);

            }else{
                Intent intent = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if(null != data){
                Bundle bundle = data.getExtras();
                if(bundle == null){
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Log.e("TAG", "onActivityResult: "+result );
                    testCarInfo(result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void testCarInfo(String result){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).testCarInfo(Utils.getShared2(getApplicationContext(),"token"),result,"206");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        if(jsonObject.get("status").getAsInt() == 0){

                            JsonObject data = jsonObject.get("data").getAsJsonObject();
                            presenter.getWarehouseCars(data.get("carCode").getAsString());

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

            }
        });
    }



}
