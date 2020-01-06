package com.example.carassistant.Interface;

public interface VehiclesNumberInterface {
    interface View{
        void succeed();
        void failed();
        void onRefresh();
        void onLoadMore();
        void onNothingData();
    }
    //presenter
    interface Presenter{
        void searchNeedPrintNoCars(String searchInfo);
    }
}
