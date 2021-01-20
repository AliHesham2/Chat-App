package com.example.chat.Fragment;

import com.example.chat.notification.MyResponse;
import com.example.chat.notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=\tAAAA_miQvO8:APA91bFSVJ1SYWlK86njOLNjY5zJq1yjDLG8WK6CzZ9s-0hkrjnA9IqBSuRsCNsbXoGNviTdCx3yWFPuVZ_FW5z3H1eIidBLX1oCg3glRT3fdg7E3_-KwU4P4UuwPeZsok6-lJpulKxT"

            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
