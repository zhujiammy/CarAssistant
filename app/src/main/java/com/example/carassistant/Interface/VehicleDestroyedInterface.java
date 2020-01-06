package com.example.carassistant.Interface;

public interface VehicleDestroyedInterface {
    interface View{
        void succeed();
        void failed();
        void onRefresh();
        void onLoadMore();
        void onNothingData();
    }
    //presenter
    interface Presenter{
        void searchDestroyCars(String searchInfo);
    }
}
