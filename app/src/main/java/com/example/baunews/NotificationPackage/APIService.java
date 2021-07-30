package com.example.baunews.NotificationPackage;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAABtIEO-c:APA91bEL9Sje5iGHgTK8ebWENw91ghbsD9v8R-n8rFAlPCGWOiM3D9J1UOcMI_R7obUcPGh4qMU60ZdcKPB3ukHWBCgI2Iz5wBVaIVCf__mF3TXAspsWgjrh1Ott335p0w2s0HRZ0L4S"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}