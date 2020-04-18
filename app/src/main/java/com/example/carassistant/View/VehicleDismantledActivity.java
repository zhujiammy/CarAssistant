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
import com.example.carassistant.Interface.VehicleDismantledInterface;
import com.example.carassistant.Interface.VehicleWeigInterface;
import com.example.carassistant.Interface.VehiclesNumberInterface;
import com.example.carassistant.Presenter.RetrieveVehiclePresenter;
import com.example.carassistant.Presenter.VehicleDismantledPresenter;
import com.example.carassistant.Presenter.VehicleWeigPresenter;
import com.example.carassistant.Presenter.VehiclesNumberPresenter;
import com.example.carassistant.R;
import com.example.carassistant.adapter.VehicleDismantledAdapter;
import com.example.carassistant.adapter.VehicleWeigAdapter;
import com.example.carassistant.adapter.VehiclesNumberAdapter;
import com.example.carassistant.utils.RecyclerViewEmptySupport;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.BindView;
import butterknife.ButterKnife;


//待拆解
public class VehicleDismantledActivity extends AppCompatActivity implements VehicleDismantledInterface.View, View.OnClickListener {

    @BindView(R.id.serach_btn)
    EditText serach_btn;//搜索
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;//标题
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerViewEmptySupport recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private VehicleDismantledAdapter adapter;
    private VehicleDismantledPresenter presenter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.vehicledismantledactivity);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        if(getIntent().getStringExtra("disType").equals("0")){
            toolbar_title.setText("待拆解车");
        }else {
            toolbar_title.setText("待拆解配件");
        }
        initUI();
        presenter = new VehicleDismantledPresenter(this,this,recyclerView,adapter,getIntent().getStringExtra("disType"));
        presenter.Tobedismantled(getIntent().getStringExtra("disType"),null);
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
                presenter.Tobedismantled(getIntent().getStringExtra("disType"),null);
                serach_btn.setText("");
            }
        });
        serach_btn.addTextChangedListener(watcher);
    }



    //搜索
    TextWatcher watcher  = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            presenter.Tobedismantled(getIntent().getStringExtra("disType"),s.toString());
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


}
