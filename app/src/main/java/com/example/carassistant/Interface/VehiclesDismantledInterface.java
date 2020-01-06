package com.example.carassistant.Interface;

public interface VehiclesDismantledInterface {
    interface View{
        void succeed();
        void failed();
        void onRefresh();
        void onLoadMore();
        void onNothingData();
    }
    //presenter
    interface Presenter{
        void searchDismantleConfirm(String searchInfo);
    }
}
