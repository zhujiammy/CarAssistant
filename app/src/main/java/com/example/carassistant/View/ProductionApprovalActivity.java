package com.example.carassistant.View;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.APP.CarApp;
import com.example.carassistant.Interface.ProductionApprovalInterface;
import com.example.carassistant.Interface.VehicleWeigInterface;
import com.example.carassistant.Interface.VehiclesNumberInterface;
import com.example.carassistant.Presenter.ProductionApprovalPresenter;
import com.example.carassistant.Presenter.RetrieveVehiclePresenter;
import com.example.carassistant.Presenter.TestVehicleWeigPresenter;
import com.example.carassistant.Presenter.VehicleWeigPresenter;
import com.example.carassistant.Presenter.VehiclesNumberPresenter;
import com.example.carassistant.R;
import com.example.carassistant.adapter.VehicleWeigAdapter;
import com.example.carassistant.adapter.VehiclesNumberAdapter;
import com.example.carassistant.adapter.productionApproveAdapter;
import com.example.carassistant.retrofit.HttpHelper;
import com.example.carassistant.utils.RecyclerViewEmptySupport;
import com.example.carassistant.utils.Utils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


//生产审批
public class ProductionApprovalActivity extends AppCompatActivity implements ProductionApprovalInterface.View, View.OnClickListener {

    @BindView(R.id.serach_btn)
    EditText serach_btn;//搜索
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerViewEmptySupport recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private productionApproveAdapter adapter;

    private ProductionApprovalPresenter presenter;
    @BindView(R.id.ok_btn)
    Button ok_btn;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.productionapproval);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        initUI();
        presenter = new ProductionApprovalPresenter(this,ProductionApprovalActivity.this,recyclerView,adapter);
        presenter.productionApprove(null);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initUI(){
        ok_btn.setOnClickListener(this);
        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                presenter.productionApprove(null);
                serach_btn.setText("");
            }
        });

        serach_btn.addTextChangedListener(watcher);
    }

    //搜索
    TextWatcher watcher  = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            presenter.productionApprove(s.toString());
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
        if(v == ok_btn){
            presenter.print();
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


}
