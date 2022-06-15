package NotificationTest;


import android.app.Activity;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dz.esisba.a2cpi_project.R;

public class FcmNotificationsSender  {

    String userFcmToken;
    String title;
    String body;

    Activity mActivity;

    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey ="AAAAC_Pg3wg:APA91bGcw7ZweRrXYjTEAMI3yQDl1UFjs10AB5N5BaLdVWJbDvvbKAM2il2Qxu8twfEnG6SLIwLCf3EDoU64JTexg0EaGJnMxXfTRnbM2p1bAxjQ9DsnVKXAyUW7RYKZY4vSmqsn0Inj";

    public FcmNotificationsSender(String userFcmToken, String title, String body, Activity mActivity) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.mActivity = mActivity;


    }

    public void SendNotifications() {

        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", userFcmToken);
            JSONObject notiObject = new JSONObject();
            notiObject.put("title", title);
            notiObject.put("body", body);
            notiObject.put("icon", R.drawable.splash_screen_logo); // enter icon that exists in drawable only



            mainObj.put("notification", notiObject);


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, response -> {

                // code run if got response

            }, error -> {
                // code run if got error

            }) {
                @Override
                public Map<String, String> getHeaders() {


                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + fcmServerKey);
                    return header;


                }
            };
            requestQueue.add(request);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}