package com.hafros.bookproj;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;

import com.hafros.bookproj.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class Preview extends AppCompatActivity {

    ImageView imageView;
    WebView webView;

    String url;
    String imgURL;
    String description;

    Context mContext;

    private boolean hasKey(Intent intent, String key){

        try{
            String string = intent.getExtras().getString(key);
            return true;
        }
        catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }



    }


    private boolean loadFromDeepLink(){

        try {
            Intent intent = getIntent();

            String action = intent.getAction();
            Uri data = intent.getData();

            String path = data.getPath();

            String idStr = path.substring(path.lastIndexOf('/') + 1);

//            AlertDialog.Builder builder;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
//            } else {
//                builder = new AlertDialog.Builder(this);
//            }
//            builder.setTitle("Delete entry")
//                    .setMessage(idStr)
//                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // continue with delete
//                        }
//                    })
//                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // do nothing
//                        }
//                    })
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .show();


            String items = App.getCache().getAsString("configuration");

            JSONArray jsonArray = new JSONArray(items);

            boolean find = false;

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject object = jsonArray.getJSONObject(i);

                if (object.has("name") && object.getString("name").equals(idStr)){

                    this.url = object.getString("url");

                    this.description = object.getString("description");

                    this.imgURL = object.getString("image");

                    find = true;

                    break;

                }

            }

            if (find){
                webView.loadData(description, "text/html; charset=utf-8", "UTF-8");

                App.loadImage(imgURL,imageView);

                if (description == null || description.length() == 0 || description.equals("{}")){
                    webView.setVisibility(View.GONE);
                }

            }

            Log.d("DEEP",""+data.toString());

            return true;
        }
        catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }
        catch (JSONException e){
            e.printStackTrace();
            return false;
        }

        // return false;

    }

    private void loadFromData(){

        Intent intent = getIntent();
        url = Objects.requireNonNull(intent.getExtras()).getString("url");

        imgURL = Objects.requireNonNull(intent.getExtras()).getString("img");

        if (hasKey(intent,"description")){
            description = Objects.requireNonNull(intent.getExtras()).getString("description");
            Log.d("DESCRITPION",""+description);
            webView.loadData(description, "text/html; charset=utf-8", "UTF-8");
        }



        App.loadImage(imgURL,imageView);

        if (description == null || description.length() == 0 || description.equals("{}")){
            webView.setVisibility(View.GONE);
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        mContext = this;

//
//        AppLinkData.fetchDeferredAppLinkData(this, new AppLinkData.CompletionHandler() {
//            @Override
//            public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
//
//                if (appLinkData != null){
//                    Log.d("DEEP",""+appLinkData.toString());
//                }
//
//
//                Log.d("DEEP1","");
//            }
//        });
//
//        AppLinkData.fetchDeferredAppLinkData(this, getResources().getString(R.string.facebook_app_id), new AppLinkData.CompletionHandler() {
//            @Override
//            public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
//                if (appLinkData != null){
//                    Log.d("DEEP2",""+appLinkData.toString());
//                }
//                Log.d("DEEP2","");
//
//            }
//        });



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = (WebView) findViewById(R.id.text);
        webView.setBackgroundColor(Color.TRANSPARENT);


        if (isBrowser()){



            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadsImagesAutomatically(true);

            webView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String weburl) {

                    try {

                        Intent intent = new Intent(Preview.this, WebActivity.class);
//
                        intent.putExtra("url", ""+weburl);
                        startActivity(intent);

                        return true;
                        // do whatever you want to do on a web link click

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }

            });
        }






        imageView = (ImageView) findViewById(R.id.imageView);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        float ratio = 16.0f/6.0f;

        float end = width/ratio;

        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();

        layoutParams.height = (int) end;

        if (loadFromDeepLink()){
            return;
        }

        loadFromData();





    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return false;
        }
    }

    private boolean isBrowser(){
        try {
            JSONArray jsonArray = new JSONArray(App.getCache().getAsString("configuration"));

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                if (object.has("browser")){
                    return object.getBoolean("browser");

                }



            }
        } catch (JSONException e) {

            e.printStackTrace();
            return App.browser;
        }

        return App.browser;
    }


    @Override
    public void onBackPressed()
    {

        super.onBackPressed();

        super.finish();
    }
}
