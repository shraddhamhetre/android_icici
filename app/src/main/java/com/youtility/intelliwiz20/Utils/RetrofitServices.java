package com.youtility.intelliwiz20.Utils;

import com.youtility.intelliwiz20.Model.LoginResponse;
import com.youtility.intelliwiz20.Model.LogoutResponse;
import com.youtility.intelliwiz20.Model.ResponseClientUrlData;
import com.youtility.intelliwiz20.Model.ResponseData;
import com.youtility.intelliwiz20.Model.ResponseReturnIdData;
import com.youtility.intelliwiz20.Model.UploadLoginParameters;
import com.youtility.intelliwiz20.Model.UploadParameters;
import com.youtility.intelliwiz20.Model.UploadServerSelectionParam;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface RetrofitServices {
    /*@FormUrlEncoded
    @POST("/service/")
    Call<LoginResponse> login(@Query("servicename") String servicename, @Query("story") String story,
                              @Field("loginid") String loginid, @Field("password") String password,
                              @Field("sitecode") String sitecode, @Field("deviceid") String deviceid);*/
    @POST("/service/")
    public Call<LoginResponse> login(@Query("servicename") String servicename, @Body UploadLoginParameters loginParameters);

    @POST("/service/")
    public Call<LogoutResponse> logout(@Query("servicename") String servicename, @Body UploadLoginParameters logoutParameters);

    @POST("/service/")
    public Call<ResponseClientUrlData> getServerUrl(@Query("servicename") String servicename, @Body UploadServerSelectionParam uploadParameters);

    @POST("api/method/sps.sps.api.sps_erpnext_rest_api?")
    Call<ResponseClientUrlData> getNotice(@Query("filters") String title);

    /*@POST("/service/")
    public Call<ResponseData> downloadResponse(@Query("servicename") String servicename, @Body UploadParameters assetParameters);*/

    @POST("/service/")
    public Call<ResponseData> getServerResponse(@Query("servicename") String servicename, @Body UploadParameters uploadParameters);

    @POST("/service/")
    public Call<ResponseData> getServerResponse1( @Body UploadParameters uploadParameters);

    @POST("/service/")
    public Call<ResponseData> getMoreTickets( @Body UploadParameters uploadParameters);

    @POST("/service/")
    public Call<ResponseData> getAutoCloseTicket( @Body UploadParameters uploadParameters);

    @POST("/service/")
    public Call<ResponseReturnIdData> getServerResponseReturnId(@Query("servicename") String servicename, @Body UploadParameters uploadParameters);

}
