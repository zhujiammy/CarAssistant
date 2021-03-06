package com.example.carassistant.Interface;

public interface ForOutboundInterface {
    interface View{
        void succeed();
        void failed();
        void onRefresh();
        void onLoadMore();
        void onNothingData();
    }
    //presenter
    interface Presenter{
        void searchCars(String searchInfo);
    }
}
