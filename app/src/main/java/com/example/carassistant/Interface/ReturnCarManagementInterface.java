package com.example.carassistant.Interface;

public interface ReturnCarManagementInterface {
    interface View{
        void succeed();
        void failed();
        void onRefresh();
        void onLoadMore();
        void onNothingData();
    }
    //presenter
    interface Presenter{
        void searchRetreatCar(String searchInfo);
    }
}
