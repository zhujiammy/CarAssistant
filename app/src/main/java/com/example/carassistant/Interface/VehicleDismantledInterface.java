package com.example.carassistant.Interface;

public interface VehicleDismantledInterface {
    interface View{
        void succeed();
        void failed();
        void onRefresh();
        void onLoadMore();
        void onNothingData();
    }
    //presenter
    interface Presenter{
        void Tobedismantled(String disType,String listCode);
    }
}
