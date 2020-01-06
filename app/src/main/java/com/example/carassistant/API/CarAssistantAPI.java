package com.example.carassistant.API;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface CarAssistantAPI {
    //接口

    //登录
    @POST("/user/login")
    Call<ResponseBody> Login(
            @Body RequestBody Body
    );

    //等待取回车辆列表
    @GET("/awaitEntrance/searchAwaitEntranceCars")
    Call<ResponseBody>searchAwaitEntranceCars(
            @Header("token") String token,
            @Query("searchInfo") String searchInfo  //车牌/车型/车辆编号
    );

    //车源详情
    @GET("/awaitEntrance/searchAwaitEntranceDetail")
    Call<ResponseBody>searchAwaitEntranceDetail(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId  //车辆id
    );

    //拓号车辆
    @GET("/printNo/searchNeedPrintNoCars")
    Call<ResponseBody>searchNeedPrintNoCars(
            @Header("token") String token,
            @Query("searchInfo") String searchInfo  //车牌/车型/车辆编号
    );
    //试验车拓号
    @GET("/printNo/testcar/searchNeedPrintNoCars")
    Call<ResponseBody>testcarsearchNeedPrintNoCars(
            @Header("token") String token,
            @Query("searchInfo") String searchInfo  //车牌/车型/车辆编号
    );

    //获取车辆拓号的图片信息
    @GET("/printNo/searchNeedPrintNoCarsDetail")
    Call<ResponseBody>searchNeedPrintNoCarsDetail(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId//车辆id
    );

    //提交拓号图片信息
    @Multipart
    @POST("/printNo/printNoCars")
    Call<ResponseBody>printNoCars(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId,  //车辆id
            @Query("type") int type,//0-暂存 1-保存
            @Part MultipartBody.Part userHeadFile

    );
    //提交试验车拓号图片信息
    @Multipart
    @POST("/printNo/testcar/printNoCars")
    Call<ResponseBody>testcarprintNoCars(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId,  //车辆id
            @Query("type") int type,//0-暂存 1-保存
            @Part MultipartBody.Part userHeadFile

    );

    //获取过磅车辆
    @GET("/weigh/searchWeighCars")
    Call<ResponseBody>searchWeighCars(
            @Header("token") String token,
            @Query("searchInfo") String searchInfo  //车牌/车型/车辆编号
    );

    @GET("/weigh/searchWeighCarsDetail")
    Call<ResponseBody>searchWeighCarsDetail(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId//车辆id
    );
    //车辆过磅
    @Multipart
    @POST("/weigh/weighCar")
    Call<ResponseBody>weighCar(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId,  //车辆id
            @Query("type") int type,//0-暂存 1-保存
            @Query("weight") float weight,//重量
            @Query("weightOdd") String weightOdd,//过磅单号
            @Part MultipartBody.Part userHeadFile

    );


    //获取试验车过磅车辆
    @GET("/weigh/testcar/searchWeighCars")
    Call<ResponseBody>testcarsearchWeighCars(
            @Header("token") String token,
            @Query("searchInfo") String searchInfo  //车牌/车型/车辆编号
    );
    //试验车辆过磅
    @Multipart
    @POST("/weigh/testcar/weighCar")
    Call<ResponseBody>testcarweighCar(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId,  //车辆id
            @Query("type") int type,//0-暂存 1-保存
            @Query("weight") float weight,//重量
            @Query("weightOdd") String weightOdd,//过磅单号
            @Part MultipartBody.Part userHeadFile

    );

    //待初检车辆
    @GET("/initialSurvey/searchInitialSurveyCars")
    Call<ResponseBody>searchInitialSurveyCars(
            @Header("token") String token,
            @Query("searchInfo") String searchInfo  //车牌/车型/车辆编号
    );

    //车辆初检信息保存`
    @POST("/initialSurvey/saveInitialSurveyDetail")
    Call<ResponseBody>saveInitialSurveyDetail(
            @Header("token") String token,
            @Body RequestBody Body
    );

    //待初检试验车辆
    @GET("/initialSurvey/testcar/searchInitialSurveyCars")
    Call<ResponseBody>testcarsearchInitialSurveyCars(
            @Header("token") String token,
            @Query("searchInfo") String searchInfo  //车牌/车型/车辆编号
    );

    //试验车辆初检信息保存`
    @POST("/initialSurvey/testcar/saveInitialSurveyDetail")
    Call<ResponseBody>testcarsaveInitialSurveyDetail(
            @Header("token") String token,
            @Body RequestBody Body
    );

    //获取待初检车辆信息
    @GET("/initialSurvey/searchInitialSurveyDetail")
    Call<ResponseBody>searchInitialSurveyDetail(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId  //车辆id
    );

    //车辆预处理
    @GET("/carPretreatment/searchCarPretreatment")
    Call<ResponseBody>searchCarPretreatment(
            @Header("token") String token,
            @Query("searchInfo") String searchInfo  //车牌/车型/车辆编号
    );

    //车辆预处理信息保存
    @Multipart
    @POST("/carPretreatment/saveCarPretreatmentDetail")
    Call<ResponseBody>saveCarPretreatmentDetail(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId,  //车辆id
            @Query("type") int type,//0-暂存 1-保存
            @Part List<MultipartBody.Part> parts
    );


    //试验车辆预处理
    @GET("/carPretreatment/testcar/searchTestCarPretreatment")
    Call<ResponseBody>testcarsearchCarPretreatment(
            @Header("token") String token,
            @Query("searchInfo") String searchInfo  //车牌/车型/车辆编号
    );

    //试验车辆预处理信息保存
    @Multipart
    @POST("/carPretreatment/testcar/saveCarPretreatmentDetail")
    Call<ResponseBody>testcarsaveCarPretreatmentDetail(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId,  //车辆id
            @Query("type") int type,//0-暂存 1-保存
            @Part List<MultipartBody.Part> parts
    );

    //车辆拆解
    @GET("/chooseDismantleType/getChooseDismantleTypeCars")
    Call<ResponseBody>getChooseDismantleTypeCars(
            @Header("token") String token,
            @Query("searchInfo") String searchInfo  //车牌/车型/车辆编号
    );

    //试验车辆入库
    @GET("/chooseWarehouse/testcar/getWarehouseCars")
    Call<ResponseBody>getWarehouseCars(
            @Header("token") String token,
            @Query("searchInfo") String searchInfo  //车牌/车型/车辆编号
    );
    //试验车辆入库明细
    @GET("/chooseWarehouse/testcar/getWarehouseCarsDetails")
    Call<ResponseBody>getWarehouseCarsDetails(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId  //车辆id
    );
    //试验车辆入库保存
    @POST("/chooseWarehouse/testcar/saveWarehouse")
    Call<ResponseBody>saveWarehouse(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId,//车辆id
            @Query("warehouseId") String warehouseId
    );


    //试验车辆拆解
    @GET("/chooseDismantleType/testcar/getChooseDismantleTypeCars")
    Call<ResponseBody>testcargetChooseDismantleTypeCars(
            @Header("token") String token,
            @Query("searchInfo") String searchInfo  //车牌/车型/车辆编号
    );
    //车辆拆解明细
    @GET("/chooseDismantleType/getChooseDismantleTypeCarsDetails")
    Call<ResponseBody>getChooseDismantleTypeCarsDetails(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId  //车辆id
    );
    //试验车车辆拆解明细
    @GET("/chooseDismantleType/testcar/getChooseDismantleTypeCarsDetails")
    Call<ResponseBody>testcargetChooseDismantleTypeCarsDetails(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId  //车辆id
    );

    //车辆拆解方式
    @POST("/chooseDismantleType/saveChooseDismantleType")
    Call<ResponseBody>saveChooseDismantleType(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId , //车辆id
            @Query("dismantleCode") String dismantleCode,//拆解方式
            @Query("warehouseId") String warehouseId
    );
    //试验车辆拆解方式
    @POST("/chooseDismantleType/testcar/saveChooseDismantleType")
    Call<ResponseBody>testcarsaveChooseDismantleType(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId ,//车辆id
            @Query("dismantleCode") String dismantleCode//拆解方式
    );

    //待确定拆解方式车辆
    @GET("/dismantleConfirm/searchDismantleConfirm")
    Call<ResponseBody>searchDismantleConfirm(
            @Header("token") String token,
            @Query("searchInfo") String searchInfo  //车牌/车型/车辆编号
    );

    //待拆解确认车辆明细
    @GET("/dismantleConfirm/searchDismantleConfirmCarDetail")
    Call<ResponseBody>searchDismantleConfirmCarDetail(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId  //车辆id
    );

    //车辆确定拆解
    @POST("/dismantleConfirm/sureDismantleConfirmCar")
    Call<ResponseBody>sureDismantleConfirmCar(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId  //车辆id
    );

    //待出库的车辆
    @GET("/needEXWarehouse/searchCars")
    Call<ResponseBody>searchCars(
            @Header("token") String token,
            @Query("searchInfo") String searchInfo  //车牌/车型/车辆编号
    );

    //待出库的试验车辆
    @GET("/needEXWarehouse/testcar/searchCars")
    Call<ResponseBody>testcarsearchCars(
            @Header("token") String token,
            @Query("searchInfo") String searchInfo  //车牌/车型/车辆编号
    );

    //待出库车辆明细
    @GET("/needEXWarehouse/searchCarsDetails")
    Call<ResponseBody>searchCarsDetails(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId  //车辆id
    );
    //车辆确定出库
    @POST("/needEXWarehouse/EXWarehouseCar")
    Call<ResponseBody>EXWarehouseCar(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId,  //车辆id
            @Query("flowId") String flowId
    );
    //试验车车辆确定出库
    @POST("/needEXWarehouse/testcar/EXWarehouseCar")
    Call<ResponseBody>testcarEXWarehouseCar(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId //车辆id
    );
    //试验车车辆确定出库明细
    @GET("/needEXWarehouse/testcar/searchCarsDetails")
    Call<ResponseBody>testcarsearchCarsDetails(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId //车辆id
    );

    //监销待确认
    @GET("/superviseDestroy/searchCars")
    Call<ResponseBody>superviseDestroysearchCars(
            @Header("token") String token,
            @Query("searchInfo") String searchInfo  //车牌/车型/车辆编号
    );

    //车辆监销待确认详情
    @GET("/superviseDestroy/searchCarsDetails")
    Call<ResponseBody>superviseDestroysearchCarsDetails(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId  //车辆id
    );

    //确定监销
    @POST("/superviseDestroy/superviseDestroyCar")
    Call<ResponseBody>superviseDestroyCar(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId  //车辆id
    );

    //试验车车辆毁型
    @GET("/destroyCar/testcar/searchDestroyCars")
    Call<ResponseBody>testcarsearchDestroyCars(
            @Header("token") String token,
            @Query("searchInfo") String searchInfo  //车牌/车型/车辆编号
    );
    //试验车车辆毁型明细
    @GET("/destroyCar/testcar/searchDestroyCarsDetails")
    Call<ResponseBody>testcarsearchDestroyCarsDetails(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId //车辆id
    );

    //车辆毁型
    @GET("/destroyCar/searchDestroyCars")
    Call<ResponseBody>searchDestroyCars(
            @Header("token") String token,
            @Query("searchInfo") String searchInfo  //车牌/车型/车辆编号
    );

    //获取毁型车辆明细
    @GET("/destroyCar/searchDestroyCarsDetails")
    Call<ResponseBody>searchDestroyCarsDetails(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId //车辆id

    );
    //保存试验车毁型车辆
    @Multipart
    @POST("/destroyCar/testcar/saveDestroyCars")
    Call<ResponseBody>testcarsaveDestroyCars(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId,  //车辆id
            @Query("type") int type,//0-暂存 1-保存
            @Query("picAttachIds") List<String> picAttachIds,
            @Part MultipartBody.Part[]  parts
    );

    //保存待毁型车辆
    @Multipart
    @POST("/destroyCar/saveDestroyCars")
    Call<ResponseBody>saveDestroyCars(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId,  //车辆id
            @Query("type") int type,//0-暂存 1-保存
            @Query("picAttachIds") List<String> picAttachIds,
            @Part MultipartBody.Part[]  parts
    );

    //获取待处理明细
    @GET("/carPretreatment/searchCarPretreatmentDetail")
    Call<ResponseBody>searchCarPretreatmentDetail(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId //车辆id

    );

    //查询需要退车的车辆
    @GET("/retreatCar/searchRetreatCar")
    Call<ResponseBody>searchRetreatCar(
            @Header("token") String token,
            @Query("searchInfo") String searchInfo  //车牌/车型/车辆编号
    );

    //获取待退车车辆明细
    @GET("/retreatCar/searchRetreatCarDetail")
    Call<ResponseBody>searchRetreatCarDetail(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId //车辆id
    );

    //车辆手续确定
    @POST("/retreatCar/confirmDoCarProcedure")
    Call<ResponseBody>confirmDoCarProcedure(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId,  //车辆id
            @Query("reason") String reason
    );

    //车辆出厂
    @POST("/retreatCar/confirmDoCar")
    Call<ResponseBody>confirmDoCar(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId,  //车辆id
            @Query("reason") String reason
    );

    //拍摄退车单
    @Multipart
    @POST("/retreatCar/shootBackCarPicture")
    Call<ResponseBody>shootBackCarPicture(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId,  //车辆id
            @Part MultipartBody.Part File
    );

    //文件上传
    @Multipart
    @POST("/entrance/entranceCar")
    Call<ResponseBody>entranceCar(
            @Header("token") String token,
            @Query("plateNumberNo") String plateNumberNo ,//车牌号
            @Query("plateNumberColour") String plateNumberColour,//车身颜色
            @Part MultipartBody.Part userHeadFile
    );

    //车牌颜色
    @GET("/dictionary/dictionaryValue/CAR_PLATE_NUMBER_COLOR")
    Call<ResponseBody>CAR_PLATE_NUMBER_COLOR(
            @Header("token") String token
    );
    //车身颜色
    @GET("/dictionary/dictionaryValue/COLOUR_TYPE")
    Call<ResponseBody>COLOUR_TYPE(
            @Header("token") String token
    );

    //获取未处理流程统计
    @GET("/disCarFlow/flowCount/1")
    Call<ResponseBody>flowCount(

    );
    //试验车获取未处理流程统计
    @GET("/disCarFlow/flowCount/10")
    Call<ResponseBody>flowCount1(

    );

    //查询车源类型
    @GET("/carDetail/carModelType")
    Call<ResponseBody>carModelType(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId //车辆id
    );

    //基础信息
    @GET("/carDetail/socialCarBaseInfo")
    Call<ResponseBody>socialCarBaseInfo(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId //车辆id
    );
    //试验车基础信息
    @GET("/carDetail/testCarBaseInfo")
    Call<ResponseBody>testCarBaseInfo(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId //车辆id
    );

    //过程信息
    @GET("/carDetail/carProcessInfo")
    Call<ResponseBody>carProcessInfo(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId //车辆id
    );

    //车辆信息
    @GET("/carDetail/carInfo")
    Call<ResponseBody>carInfo(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId //车辆id
    );
    //手续信息
    @GET("/carDetail/procedureInfo")
    Call<ResponseBody>procedureInfo(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId //车辆id
    );

    //车源信息
    @GET("/carDetail/carSourceInfo")
    Call<ResponseBody>carSourceInfo(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId //车辆id
    );

    //拆解信息
    @GET("/carDetail/dismantlingInfo")
    Call<ResponseBody>dismantlingInfo(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId //车辆id
    );

    //图片信息
    @GET("/carDetail/carPicInfo")
    Call<ResponseBody>carPicInfo(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId //车辆id
    );

    //仓库位置
    @GET("/warehouseManager/showWareHouseName")
    Call<ResponseBody>showWareHouseName(
            @Header("token") String token,
            @Query("warehouseId") String warehouseId //车辆id
    );

    //车牌识别
    @Multipart
    @POST("/ocr/car/licensePlate")
    Call<ResponseBody>licensePlate(
            @Header("token") String token,
            @Part MultipartBody.Part file
    );


    //车状态
    @GET("/dictionary/dictionaryValue/TEST_CAR_STATE")
    Call<ResponseBody>TEST_CAR_STATE(
            @Header("token") String token
    );

    //散车入场
    @Multipart
    @POST("/entrance/testcar/entranceCar")
    Call<ResponseBody>testcarentranceCar(
            @Header("token") String token,
            @Query("testMainEnginePlantsNum") String testMainEnginePlantsNum ,//主机厂编号
            @Query("testState") String testState,//车状态
            @Query("carType") String carType,//车型
            @Part MultipartBody.Part userHeadFile
    );
    //车源入场
    @Multipart
    @POST("/entrance/testcar/entranceCar")
    Call<ResponseBody>testcarentranceCar1(
            @Header("token") String token,
            @Query("carDetailId") String carDetailId,
            @Query("testMainEnginePlantsNum") String testMainEnginePlantsNum ,//主机厂编号
            @Query("testState") String testState,//车状态
            @Query("carType") String carType,//车型
            @Part MultipartBody.Part userHeadFile
    );

    //车源信息
    @GET("/entrance/testcar/getDisCarsList")
    Call<ResponseBody>getDisCarsList(
            @Header("token") String token,
            @Query("condition") String condition  //车牌/车型/车辆编号
    );

    //车源入场明细
    @GET("/entrance/getDisCarsDetails")
    Call<ResponseBody>getDisCarsDetails(
            @Header("token") String token,
            @Query("carsId") String carsId,  //车源id
            @Query("condition") String condition  //车牌/车型/车辆编号
    );

    //显示用户拥有的菜单
    @GET("/user/showAppMenus")
    Call<ResponseBody>showAppMenus(
            @Header("token") String token
    );
    //生产审批
    @GET("/productionApprove/")
    Call<ResponseBody>productionApprove(
            @Header("token") String token,
            @Query("condition") String condition  //车牌/车型/车辆编号
    );
    //生产审批通过
    @POST("/productionApprove/")
    Call<ResponseBody>productionApprovea(
            @Header("token") String token,
            @Query("carDetailIds") String[] carDetailIds  //车源明细id数组
    );






}
