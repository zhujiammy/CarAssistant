package com.example.carassistant.View;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.APP.CarApp;
import com.example.carassistant.Model.AllData;
import com.example.carassistant.R;
import com.example.carassistant.adapter.Adapter;
import com.example.carassistant.adapter.VehicleDismantledAdapter;
import com.example.carassistant.retrofit.HttpHelper;
import com.example.carassistant.utils.NoScrollViewPager;
import com.example.carassistant.utils.RecyclerViewEmptySupport;
import com.example.carassistant.utils.TabLayoutAddOnClickHelper;
import com.example.carassistant.utils.Utils;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONArray;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DefiniteDismantlingActivity extends AppCompatActivity implements UndemolishedFragment.MyListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.view_pager)
    NoScrollViewPager viewPager;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<>();
    private Adapter adapter;
    private  Bundle bundle;
    private UndemolishedFragment undemolishedFragment;
    private DismantledFragment dismantledFragment;
    private ProgressDialog progressDialog;
    private String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.definitedismantlingactivity);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        initUI();
    }

    private void initUI(){
        bundle = new Bundle();
        bundle.putString("disListId",getIntent().getStringExtra("disListId"));
         undemolishedFragment = new UndemolishedFragment();
        undemolishedFragment.setArguments(bundle);
         dismantledFragment = new DismantledFragment();
        dismantledFragment.setArguments(bundle);

        //imageInfofragment.setArguments(bundle);
        mFragments.add(undemolishedFragment);
        list.add("未拆解");
        mFragments.add(dismantledFragment);
        list.add("已拆解");
        tablayout.setupWithViewPager(viewPager);
        adapter = new Adapter(getSupportFragmentManager(),mFragments,list);
        viewPager.setAdapter(adapter);
/*        for (int i = 0; i < tablayout.getTabCount(); i++) {
            TabLayout.Tab tab = tablayout.getTabAt(i);
            if (tab == null) return;
            //这里使用到反射，拿到Tab对象后获取Class
            Class c = tab.getClass();
            try {
                //Filed “字段、属性”的意思,c.getDeclaredField 获取私有属性。
                //"mView"是Tab的私有属性名称(可查看TabLayout源码),类型是 TabView,TabLayout私有内部类。
                Field field = c.getDeclaredField("view");
                //值为 true 则指示反射的对象在使用时应该取消 Java 语言访问检查。值为 false 则指示反射的对象应该实施 Java 语言访问检查。
                //如果不这样会报如下错误
                // java.lang.IllegalAccessException:
                //Class com.test.accessible.Main
                //can not access
                //a member of class com.test.accessible.AccessibleTest
                //with modifiers "private"
                field.setAccessible(true);
                final View view = (View) field.get(tab);
                if (view == null) return;
                view.setTag(i);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) view.getTag();
                        //这里就可以根据业务需求处理点击事件了。
                        if(position == 0){
                            showDismantleDetail("1");
                            defStatus ="1";
                        }
                        if(position == 1)
                        {
                            showDismantleDetail("2");
                            defStatus ="2";
                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chai, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        if(id == R.id.sumbit){
            if(type.equals("2") ){
                progressDialog = new ProgressDialog(this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("正在提交....");
                progressDialog.show();
                Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).partAndCarComplete(getIntent().getStringExtra("disListId"));
                call.enqueue(new Callback<ResponseBody>() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.body()!=null){
                            try {
                                String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                                JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                                Log.e("TAG", "onResponse: "+jsonStr );
                                if(jsonObject.get("status").getAsInt() == 0){
                                    Toast.makeText(getApplicationContext(),"已成功拆解所有配件",Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    CarApp carApp =(CarApp)getApplication();
                                    carApp.setRefresh(true);
                                    finish();
                                }else {
                                    progressDialog.dismiss();
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
                        Log.e("TAG", "onResponse: "+"连接超时，请检查网络环境，避免影响使用！" );
                    }
                });
            }else {
                Toast.makeText(getApplicationContext(),"还有未拆解的配件，请拆解完成",Toast.LENGTH_SHORT).show();
            }


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendValue(String value) {
        Log.e("TAG", "sendValue: "+value );
        type = value;
    }



/*    private void showDismantleDetail(String defStatus){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).showDismantleDetail(getIntent().getStringExtra("disListId"),defStatus);
        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("ResourceType")
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        if(jsonObject.get("status").getAsInt() == 0){
                                radio_group.removeAllViews();
                                JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
                                for(int i=0;i<jsonArray.size();i++){
                                    JsonObject jsonObject1 = jsonArray.get(i).getAsJsonObject();
                                    RadioButton tempButton = new RadioButton(getApplicationContext());
                                    tempButton.setBackgroundResource(R.drawable.rd_selector);
                                    tempButton.setTextColor(getResources().getColorStateList(R.drawable.rd_text_slelct));
                                    tempButton.setButtonDrawable(null);// 设置文字距离按钮四周的距离
                                    tempButton.setText(jsonObject1.get("partName").getAsString());
                                    tempButton.setGravity(Gravity.CENTER);
                                    tempButton.setId(i);

                                    Log.e("TAG", "onResponse: "+ tempButton.getId());
                                    if(tempButton.getId() == 0){
                                        tempButton.setChecked(true);
                                        if(defStatus.equals("2")){
                                            partName = jsonObject1.get("partName").getAsString();
                                            id = tempButton.getId();
                                            bundle = new Bundle();
                                            bundle.putString("partName",partName);
                                            bundle.putString("defStatus",defStatus);
                                            bundle.putString("id", String.valueOf(id));


                                        }
                                    }
                                    radio_group.addView(tempButton, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    tempButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            partName = jsonObject1.get("partName").getAsString();
                                            id = tempButton.getId();
                                            bundle.putString("partName",partName);
                                            bundle.putString("defStatus",defStatus);
                                            bundle.putString("id", String.valueOf(id));
                                            mFragments.clear();
                                            list.clear();
                                            mFragments.add(undemolishedFragment);
                                            list.add("未拆解");
                                            mFragments.add(dismantledFragment);
                                            list.add("已拆解");
                                            tablayout.setupWithViewPager(viewPager);
                                            adapter = new Adapter(getSupportFragmentManager(),mFragments,list);
                                            viewPager.setAdapter(adapter);

                                        }
                                    });
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
                Log.e("TAG", "onResponse: "+"连接超时，请检查网络环境，避免影响使用！" );
            }
        });
    }*/


}
