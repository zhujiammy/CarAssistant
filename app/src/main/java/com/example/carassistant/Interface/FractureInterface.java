package com.example.carassistant.Interface;

public interface FractureInterface {
    interface View{
        void succeed();
        void failed();
        void onRefresh();
        void onLoadMore();
        void onNothingData();
    }
    //presenter
    interface Presenter{
        void listWaitCrush(String crushListCode);
    }
}
