package com.help.android.utility;


import com.help.android.login.CustomerAddressRequestParams;
import com.help.android.login.CustomerAddressResponse;
import com.help.android.login.CustomerDetailResponse;
import com.help.android.login.CustomerLoginResponse;
import com.help.android.login.CustomerRegisterRequestParams;
import com.help.android.login.CustomerRegisterResponse;
import com.help.android.login.GETOTPRequestParams;
import com.help.android.login.GetOTPResponse;
import com.help.android.login.VerifyOTPRequestParams;
import com.help.android.login.VerifyOtpResponse;

import java.util.List;

import okhttp3.Response;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Url;

import static com.help.android.utility.ConstantAPIKt.customer_order_retrieve;
import static com.help.android.utility.ConstantAPIKt.getOtp;
import static com.help.android.utility.ConstantAPIKt.product;
import static com.help.android.utility.ConstantAPIKt.customer_login;
import static com.help.android.utility.ConstantAPIKt.customer_register;
import static com.help.android.utility.ConstantAPIKt.customer_retrieve;
import static com.help.android.utility.ConstantAPIKt.customer_update_address;
import static com.help.android.utility.ConstantAPIKt.products_categories;
import static com.help.android.utility.ConstantAPIKt.veryfyOtp;

public interface ApiInterface {






    @POST(getOtp)
    Call<GetOTPResponse> getOTP(@Body GETOTPRequestParams gETOTPRequestParams);

    @POST(veryfyOtp)
    Call<VerifyOtpResponse>  verifyOTP(@Body VerifyOTPRequestParams verifyOTPRequestParams);


    @GET(products_categories)
    Call<List<Catagories>> getCatagories(@Query("per_page")int per_page,@Query("hide_empty")boolean  hide_empty ,@Query("parent")int  parent);


    @GET(products_categories)
    Call<List<Catagories>> getCatagoriesSelected(@Query("per_page")int per_page,@Query("hide_empty")boolean  hide_empty ,@Query("include") String params);



    @GET(products_categories)
    Call<Response> getCatagoriesRaw(@Query("per_page")int per_page, @Query("hide_empty")boolean  hide_empty , @Query("parent")int  parent);

    @GET(product)
    Call<List<Product>> getProductList(@Query("category")int category ,@Query("per_page")int per_page ,@Query("page")int page ,@Query("featured")boolean featured );

    @GET(product)
    Call<List<Product>> getProductList(@Query("per_page")int per_page ,@Query("page")int page ,@Query("featured")boolean featured );

    @POST(customer_register)
    Call<CustomerRegisterResponse> getregisterDetails(@Body CustomerRegisterRequestParams model);

    @GET(customer_login)
    Call<List<CustomerLoginResponse>> getLoginDetails(@Query("email") String email, @Query("password") String password);

    @PUT(customer_update_address+"/{id}")
    Call<CustomerAddressResponse> addAddressDetails(@Path ("id") int id,@Body CustomerAddressRequestParams model);

    @GET(customer_retrieve+"/{id}")
    Call<CustomerDetailResponse> getCustomerProfile(@Path("id")int id);

    @GET(customer_order_retrieve)
    Call<List<Order>> getMyOrder(@Query("customer") int customer );

    @GET
    Call<Product> getProductDetails(@Url String url);

    @DELETE
    Call<Order> cancelmyOrder(@Url String url);

    @POST(customer_order_retrieve)
    Call<Order> createMyOrder(@Body CreateOrderRequest createOrderRequest);

    @PUT
    Call<Order> updateOrder(@Url String url,@Body UpdatePaymentOrderRequest updatePaymentOrderRequest);


    @GET
    Call<List<WishList>> getMyWishlist(@Url String url);

    @GET
    Call<List<WishListProducts>> getMyWishlistProduct(@Url String url);

    @POST
    Call<List<createWishlistResponse>> createWishList(@Url String url, @Body createWishlistRequestParams model);


//    @POST(ENDPOINT_GET_TASK_DETAILS_LIST_BY_BEAT_ID)
//    Call<BeatWiseTakListResponse> getTaskDetailsByBeat(@Body GetBeatDeatilsRequestParams model);
//
//    @POST(ENDPOINTBeat_Task_Details)
//    Call<BeatTaskDetailsListResponse> getScheduleTaskDetailsByBeat(@Body BeatTaskDetailsRequestParams model);
//
//    @POST(Dealer_Distrib_Task_Worksheet)
//    Call<DealerDetailsResponse> getDealerDetailsByBeat(@Body DealerDetailsRequestParams model);
//
//    @POST(Get_Order_History)
//    Call<OrderHistoryDetailsResponse> getBeatAllOrderHistory(@Body BeatAllOrderListRequestParams model);
//
//    @POST(Get_Product_List_By_Cat_Seg)
//    Call<CreateOrderDetailsResponse> getCreateOrderList(@Body CreateOrderListRequestParams model);
//
//    @POST(Beat_Report_By_Date)
//    Call<BeatReportListDetailsResponse> getBeatReportList(@Body BeatReportListRequestParams model);
//
//    @POST(Get_Dropdown_Details_byName)
//    Call<OrderVSQualityResponse> getOrdervsQualityList(@Body OrderVSQualityRequestParams model);
//
//    @POST(Create_Beat_Report)
//    Call<CreateBeatReportResponse> createBeatReport(@Body CreateBeatReportRequestParams model);
//
//
//    //sec drop down beat create
//    @POST(Get_Beat_By_Level)
//    Call<BeatDetailListResponse> getBeatDetailList(@Body BeatDetailListRequestParams model);
//    //task for secon beat
//    @POST(Get_Task_For)
//    Call<TaskForListResponse> getTaskForList(@Body TaskForListRequestParams model);
//
//    //3rd drop down
//    @POST(Get_Location_By_Level)
//    Call<LocationByLevelListResponse> getLocationByLevelList(@Body LocationByLevelListRequestParams model);
//
//    //member list drop down
//    @POST(Get_AssignTo_By_TaskFor_And_Location)
//    Call<AssignToListResponse> getAssignToList(@Body AssignToListRequestParams model);
//
//    //task
//    @POST(Get_Deal_Dist_Mech_List)
//    Call<DealDistMechListResponse> getDealDistMechList(@Body DealDistMechListRequestParams model);
//
//    @POST(Create_Beat_Schedule)
//    Call<CreateBeatReportResponse> createBeatSchedule(@Body CreateBeatScheduleRequestParams model);
//
//    @POST(Complaint_List)
//    Call<ComplaintListResponse> getComplaintList(@Body ComplaintListRequestParams model);
//
//    @POST(Save_AssignTask)
//    Call<CreateBeatReportResponse> assignTask(@Body AssignTaskRequestParams model);
//
//    @POST(Create_Order)
//    Call<CreateOrderResponse> createOrder(@Body CreateOrderPRParams model);
//
//    @POST(Get_Team_List)
//    Call<TeamListResponse> getTeamList(@Body TeamListRequestParams model);
//
//
//    @POST(Apply_Leave)
//    Call<ApplyLeaveResponse> createLeave(@Body ApplyLeaveRequest model);
//
//    @POST(Edit_Leave)
//    Call<ApplyLeaveResponse> updateLeave(@Body ApplyLeaveRequest model);
//
//    @POST(Leave_List)
//    Call<LeaveListResponse> getLeaveList(@Body LeaveListRequest model);
//
//    @POST(Approved_Reject_Leave)
//    Call<ApplyLeaveResponse> accepeRejectLeave(@Body ApproveRejectLeaveListRequest model);
//
//    @POST(DoAttend)
//    Call<AttendancResponse> doAttendance(@Body AttendanceRequestParams model);
//
//    @POST(Get_Task_dropdown_in_expens)
//    Call<BidListResponse> getBitList(@Body AttendanceRequestParams model);
//
//    @POST(Create_Expense)
//    Call<CreateExpenseResponse> createExpense(@Body ExpenseCreateRequest model);
//
//    @Multipart
//    @POST(Add_ExpensImage)
//    Call<CreateExpenseResponse> uploadpic(
//            @Part("recPhoto1\"; filename=\"pp.png\" ") RequestBody file,
//            @Part("expensId") RequestBody expensId,
//            @Part("userId") RequestBody userId);
//
//    @POST(Distrib_Dropdown)
//    Call<DistListResponse> distDropDownList(@Body DistListRequestParams model);
//
//    @POST(Dealer_Dropdown)
//    Call<DealListResponse> dealDropDownList(@Body DealListRequestParams model);
//
//    @POST(Get_ReminderList)
//    Call<ReminderListResponse> getReminderList(@Body ReminderListRequestParams model);
//
//    @POST(Create_Reminder)
//    Call<CreateReminderResponse> addReminder(@Body CreateReminderRequestParams model);
//
//    @POST(Forgot_Password)
//    Call<forgotPasswordResponse> forgotPassword(@Body forgotPasswordRequestParams model);
//
//    @POST(Reset_Password)
//    Call<ResetPasswordResponse> resetPassword(@Body ResetPasswordRequestParams model);
//
//    @Multipart
//    @POST(Add_ExpensImage)
//    Call<CreateExpenseResponse> uploadpic(
//            @Part MultipartBody.Part filePart, @Part MultipartBody.Part filePart1, @Part MultipartBody.Part filePar2, @Part MultipartBody.Part filePar3);
//
//    @Headers({"Content-Type:text/html; charset=UTF-8","Connection:keep-alive"})
//    @Multipart
//    @POST(Add_ExpensImage)
//    Call<CreateExpenseResponse> uploadpic(
//
//            @PartMap Map<String, RequestBody> map);
//
//
//    @POST(Expense_List)
//    Call<ExpenseResponse> getLeaveList(AttendanceRequestParams model);
//
//    @POST(Expense_List)
//    Call<ExpenseResponse> getChuttiList(@Body AttendanceRequestParams model);
}
