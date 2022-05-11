package likeNotification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type: application/json",
                    "Authorization:Key=AAAA__EKjFk:APA91bGb7jv0cPB5wd-1fMU5jqF_uiXaCnxNulgOWItGO8sFsguI440-J81Lib25H2O9Tdi7zGCCEjGJTPb_xf6CRVBlm_YbwuNADN7LSnoP1WoKinwurBW53E4ku8GqLdQbzj8nv8aG\t\n"

            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotifcation (@Body NotificationSender body);

}
