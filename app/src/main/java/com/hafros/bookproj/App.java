package com.hafros.bookproj;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.afinal.simplecache.ACache;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class App extends Application {

    private static Context context;

    interface needUpdateHandler{
        void callback();
    }




    public enum eventType{
        kEventRegister,
        kEventDeposit
    }

    interface eventHandler{
        void callback(eventType event);
    }




    public static needUpdateHandler needUpdateHandler;

    public static eventHandler eventHandler;

    @Override public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationOpenedHandler(new OneSignal.NotificationOpenedHandler() {
                    @Override
                    public void notificationOpened(OSNotificationOpenResult result) {

                        JSONObject json_notification = result.notification.toJSONObject();

                        try {
                            JSONObject additionalData = json_notification.getJSONObject("payload").getJSONObject("additionalData");

                            if (additionalData.has("config")){

                                App.getCache().put("url_config",additionalData.getString("config"));

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        catch (NullPointerException e){
                            e.printStackTrace();
                        }

                        if (needUpdateHandler != null){
                            needUpdateHandler.callback();
                        }

                    }
                })
                .setNotificationReceivedHandler(new OneSignal.NotificationReceivedHandler() {
                    @Override
                    public void notificationReceived(OSNotification notification) {

                        Log.d("NOTIFICATION","RECEIVED = "+notification.toJSONObject().toString());

                        JSONObject json_notification = notification.toJSONObject();

                        try {
                            JSONObject additionalData = json_notification.getJSONObject("payload").getJSONObject("additionalData");

                            if (additionalData.has("config")){

                                App.getCache().put("url_config",additionalData.getString("config"));

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        catch (NullPointerException e){
                            e.printStackTrace();
                        }



                        if (needUpdateHandler != null){
                            needUpdateHandler.callback();
                        }

                    }
                })
                .init();

        if (!configurationExist()){

            String object = loadJSONFromAsset("configuration.json");

            getCache().put("configuration",object);

        }

        if (!urlExist()){
            getCache().put("url_config",getResources().getString(R.string.mainURL));
        }



    }

    public static void reportURL(String url){
        RequestManager.reportURL(url, new NetworkHandler() {
            @Override
            public void successHandler(String body) throws JSONException {

            }

            @Override
            public void failHandler(String error) {

            }
        });
    }


    public static void registerEvent(eventType event){

        if (eventHandler != null){
            eventHandler.callback(event);
        }

    }

    private static String loadJSONFromAsset(String filename) {
        String json = null;
        try {

            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public boolean configurationExist(){


        try {
            if (getCache().get("configuration") != null){

                return true;

            }
        } catch (FileNotFoundException e) {
            return false;
        }

        return false;

    }

    public boolean urlExist(){

        try {
            if (getCache().get("url_config") != null){

                return true;

            }
        } catch (FileNotFoundException e) {
            return false;
        }

        return false;

    }


    public static void loadImage(String url, ImageView imageView){

        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(imageView);

    }

    public static ACache getCache(){
        ACache mCache = ACache.get(context);
        return mCache;
    }

    public static int dpToPx(float dp, Resources resources) {
        float px =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

}
