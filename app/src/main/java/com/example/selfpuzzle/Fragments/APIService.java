package com.example.selfpuzzle.Fragments;

import com.example.selfpuzzle.Notifications.MyResponse;
import com.example.selfpuzzle.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA3I3yxz4:APA91bGFXemQ_7prmUZudGJy8i8liJck0dUJlnjEwPbAH8gvx4qw-U8R5MI27g6V2e5Kwgjh5im-RN97JIEfWSLKjaSkqp0kQTqR7kd1BFtbUhtn1AA5SuR1NTR95sgKtQtkWsLCfWxK"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
