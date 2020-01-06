package com.example.carassistant.Interface;

public interface WarehouseCarsInterface {
    interface View{
        void succeed();
        void failed();
        void onRefresh();
        void onLoadMore();
        void onNothingData();
    }
    //presenter
    interface Presenter{
        void getWarehouseCars(String searchInfo);
    }
}
