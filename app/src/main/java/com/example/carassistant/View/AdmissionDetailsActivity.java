package com.example.carassistant.View;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carassistant.APP.CarApp;
import com.example.carassistant.Interface.AdmissionDetailsInterface;
import com.example.carassistant.Interface.CarSourceAdmissionInterface;
import com.example.carassistant.Interface.VehiclesNumberInterface;
import com.example.carassistant.Presenter.AdmissionDetailsPresenter;
import com.example.carassistant.Presenter.CarSourceAdmissionPresenter;
import com.example.carassistant.Presenter.RetrieveVehiclePresenter;
import com.example.carassistant.Presenter.VehiclesNumberPresenter;
import com.example.carassistant.R;
import com.example.carassistant.adapter.AdmissionDetailsAdapter;
import com.example.carassistant.adapter.CarSourceAdmissionAdapter;
import com.example.carassistant.adapter.VehiclesNumberAdapter;
import com.example.carassistant.utils.RecyclerViewEmptySupport;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.BindView;
import butterknife.ButterKnife;


//车源入场明细
public class AdmissionDetailsActivity extends AppCompatActivity implements AdmissionDetailsInterface.View, View.OnClickListener {

    @BindView(R.id.serach_btn)
    EditText serach_btn;//搜索
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerViewEmptySupport recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private AdmissionDetailsAdapter adapter;
    private AdmissionDetailsPresenter presenter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.admissiondetails);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        initUI();
        presenter = new AdmissionDetailsPresenter(this,this,recyclerView,adapter);
        presenter.getDisCarsDetails(getIntent().getStringExtra("carsId"),null);
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
        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                presenter.getDisCarsDetails(getIntent().getStringExtra("carsId"),null);
                serach_btn.setText(null);
            }
        });
        serach_btn.addTextChangedListener(watcher);
    }

    //搜索
    TextWatcher watcher  = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            presenter.getDisCarsDetails(getIntent().getStringExtra("carsId"),s.toString());
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
        refreshLayout.autoRefresh();
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


}
