package com.vignesh.chatapplication.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Context-Type:application/json",
            "Authorization:key =AAAAwsxkojk:APA91bH39HLXM4kibTfCLhWt3LweGX0BABBB0VqSwY4jHfSlB82PxNOqE7mM0BmdMmCp5Sy7NOudT0b2FlfEBp9ZuxTDyj60xyMvz65FuOfDdVpfVMlW7cdeBvwDpQANjVuda2IalX84"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
