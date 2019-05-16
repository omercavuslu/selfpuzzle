package com.omercavuslu.selfpuzzle.Fragments;

import com.omercavuslu.selfpuzzle.Notifications.MyResponse;
import com.omercavuslu.selfpuzzle.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAABFOTs70:APA91bHFeTs__larB3j5rGcTt7y0xA2OG3v88q00QITf8kbxYAbqfAQCujnHOAT3x7R1A3Nm1zMYzINrgPoTc6apyVEHLuIr5NBYN3A7CJsWKh1veeAPRDuT6MB9rMyh1dLf_mNWHUBe"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
