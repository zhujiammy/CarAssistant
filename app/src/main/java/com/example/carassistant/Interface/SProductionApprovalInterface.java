package com.example.carassistant.Interface;

public interface SProductionApprovalInterface {
    interface View{
        void succeed();
        void failed();
        void onRefresh();
        void onLoadMore();
        void onNothingData();
    }
    //presenter
    interface Presenter{
        void approvallist(String pageNum,String pageSize,String state,String disType,String docCode);//拆解单
        void crushManagerapprovallist(String pageNum,String pageSize,String state,String docCode);//破碎配件
        void saleapprovallist(String pageNum,String pageSize,String state,String docCode);//销售配件
        void returnManagerallist(String pageNum,String pageSize,String state,String docCode);//销售配件
    }
}
