package com.example.carassistant.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.APP.CarApp;
import com.example.carassistant.Interface.ProductionApprovalInterface;
import com.example.carassistant.Interface.SProductionApprovalInterface;
import com.example.carassistant.Interface.VehicleWeigInterface;
import com.example.carassistant.Interface.VehiclesNumberInterface;
import com.example.carassistant.Presenter.BrokenPresenter;
import com.example.carassistant.Presenter.PartsWithdrawalPresenter;
import com.example.carassistant.Presenter.ProductionApprovalPre;
import com.example.carassistant.Presenter.ProductionApprovalPresenter;
import com.example.carassistant.Presenter.RetrieveVehiclePresenter;
import com.example.carassistant.Presenter.SalesSlipPresenter;
import com.example.carassistant.Presenter.TestVehicleWeigPresenter;
import com.example.carassistant.Presenter.VehicleWeigPresenter;
import com.example.carassistant.Presenter.VehiclesNumberPresenter;
import com.example.carassistant.R;
import com.example.carassistant.adapter.BrokenListAdapter;
import com.example.carassistant.adapter.PartsWithdrawalAdapter;
import com.example.carassistant.adapter.ProductionApprovalPreAdapter;
import com.example.carassistant.adapter.SalesSlipAdapter;
import com.example.carassistant.adapter.VehicleWeigAdapter;
import com.example.carassistant.adapter.VehiclesNumberAdapter;
import com.example.carassistant.adapter.productionApproveAdapter;
import com.example.carassistant.retrofit.HttpHelper;
import com.example.carassistant.utils.RecyclerViewEmptySupport;
import com.example.carassistant.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


//生产审批
public class ProductionApprovalAty extends AppCompatActivity implements SProductionApprovalInterface.View, View.OnClickListener {

    @BindView(R.id.serach_btn)
    EditText serach_btn;//搜索
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerViewEmptySupport recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProductionApprovalPreAdapter adapter;
    private BrokenListAdapter brokenListAdapter;
    private SalesSlipAdapter salesSlipAdapter;
    private PartsWithdrawalAdapter partsWithdrawalAdapter;
    private static  int REQUEST_CODE = 1001;

    @BindView(R.id.Type_lin)
    LinearLayout Type_lin;
    @BindView(R.id.spinner1)
    Spinner spinner1;//拆解类型
    @BindView(R.id.chaijie)
    RadioButton chaijie;//拆解单
    @BindView(R.id.posui)
    RadioButton posui;//破碎配件
    @BindView(R.id.xiaoshou)
    RadioButton xiaoshou;//销售配件
    @BindView(R.id.tuiku)
    RadioButton tuiku;//配件退库

    private ProductionApprovalPre presenter;
    private BrokenPresenter brokenPresenter;
    private SalesSlipPresenter salesSlipPresenter;
    private PartsWithdrawalPresenter partsWithdrawalPresenter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    String disType;//拆解类型
    private ProgressDialog progressDialog;
    private int type = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.productionapprovalxml);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        initUI();
        presenter = new ProductionApprovalPre(this,ProductionApprovalAty.this,recyclerView,adapter);



    }

        Spinner.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = spinner1.getSelectedItem().toString();
                if(str.equals("拆解车")){
                    disType = "0";
                    presenter.approvallist("1","100","2",disType,null);
                }else {
                    disType = "1";
                    presenter.approvallist("1","100","2",disType,null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

    private void initUI(){
        spinner1.setOnItemSelectedListener(listener);
        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                presenter.approvallist("1","100","2",disType,null);
                serach_btn.setText("");
            }
        });

        serach_btn.addTextChangedListener(watcher);
        chaijie.setOnClickListener(this);
        posui.setOnClickListener(this);
        xiaoshou.setOnClickListener(this);
        tuiku.setOnClickListener(this);
    }

    //搜索
    TextWatcher watcher  = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            presenter.approvallist("1","100","2",disType,null);
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
        if(v ==chaijie){
            //拆解
            type = 1;
            Type_lin.setVisibility(View.VISIBLE);
           presenter = new ProductionApprovalPre(this,ProductionApprovalAty.this,recyclerView,adapter);
            presenter.approvallist("1","100","2",disType,null);
        }
        if(v ==posui){
            type = 2;
            Type_lin.setVisibility(View.GONE);
            brokenPresenter = new BrokenPresenter(this,ProductionApprovalAty.this,recyclerView,brokenListAdapter);
            brokenPresenter.crushManagerapprovallist("1","100","2",null);
        }
        if(v == xiaoshou){
            type = 3;
            Type_lin.setVisibility(View.GONE);
            salesSlipPresenter = new SalesSlipPresenter(this,ProductionApprovalAty.this,recyclerView,salesSlipAdapter);
            salesSlipPresenter.saleapprovallist("1","100","2",null);
        }
        if(v == tuiku){
            type = 4;
            Type_lin.setVisibility(View.GONE);
            partsWithdrawalPresenter = new PartsWithdrawalPresenter(this,ProductionApprovalAty.this,recyclerView,partsWithdrawalAdapter);
            partsWithdrawalPresenter.returnManagerallist("1","100","2",null);

        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void print(String msg,String result,String documentId){

        Map<String,Object> map = new HashMap<>();
        map.put("documentState","2");
        map.put("documentId",documentId);
        map.put("processingResult",result);
        Log.e("TAG", "print: "+map );
        //审批通过
        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(this);
        alterDiaglog.setTitle("提示");//文字
        alterDiaglog.setMessage(msg);//提示消息
        alterDiaglog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Gson gson = new Gson();
                String obj =gson.toJson(map);
                progressDialog = new ProgressDialog(ProductionApprovalAty.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("正在提交....");
                progressDialog.show();

                RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),obj);
                if(type == 1){
                    Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).approval(Utils.getShared2(getApplicationContext(),"token"),body);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.body()!=null){
                                try {
                                    String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                                    JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                                    if(jsonObject.get("status").getAsInt() == 0){
                                        Toast.makeText(getApplicationContext(),"提交成功",Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        presenter.approvallist("1","100","2",disType,null);

                                    }else {
                                        Toast.makeText(getApplicationContext(),jsonObject.get("msg").getAsString(),Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("TAG", "onFailure: "+t.getMessage() );
                            Toast.makeText(getApplicationContext(),"连接超时，请检查网络环境，避免影响使用！",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }else if(type == 2){
                    Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).crushManagerapproval(Utils.getShared2(getApplicationContext(),"token"),body);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.body()!=null){
                                try {
                                    String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                                    JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                                    if(jsonObject.get("status").getAsInt() == 0){
                                        Toast.makeText(getApplicationContext(),"提交成功",Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        brokenPresenter.crushManagerapprovallist("1","100","2",null);

                                    }else {
                                        Toast.makeText(getApplicationContext(),jsonObject.get("msg").getAsString(),Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),"连接超时，请检查网络环境，避免影响使用！",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }else if(type == 3){
                    Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).saleapproval(Utils.getShared2(getApplicationContext(),"token"),body);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.body()!=null){
                                try {
                                    String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                                    JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                                    if(jsonObject.get("status").getAsInt() == 0){
                                        Toast.makeText(getApplicationContext(),"提交成功",Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        salesSlipPresenter.saleapprovallist("1","100","2",null);

                                    }else {
                                        Toast.makeText(getApplicationContext(),jsonObject.get("msg").getAsString(),Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),"连接超时，请检查网络环境，避免影响使用！",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
                else if(type == 4){
                    Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).returnManagerapproval(Utils.getShared2(getApplicationContext(),"token"),body);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.body()!=null){
                                try {
                                    String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                                    JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                                    if(jsonObject.get("status").getAsInt() == 0){
                                        Toast.makeText(getApplicationContext(),"提交成功",Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        partsWithdrawalPresenter.returnManagerallist("1","100","2",null);

                                    }else {
                                        Toast.makeText(getApplicationContext(),jsonObject.get("msg").getAsString(),Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getApplicationContext(),"连接超时，请检查网络环境，避免影响使用！",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }

            }


        });
        alterDiaglog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        //显示
        alterDiaglog.show();

    }


}
