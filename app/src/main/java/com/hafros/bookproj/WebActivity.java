package com.hafros.bookproj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class WebActivity extends AppCompatActivity {

    WebView webView;
    String url;
    JSONArray events = null;

    private void loadEvents(JSONArray jsonObject){
        for (int i = 0; i < jsonObject.length(); i++) {

            try {
                JSONObject object = jsonObject.getJSONObject(i);



                if (object.has("url")){

                    Log.d("CURRENT S",""+object.getString("url"));

                    if (url.equals(object.getString("url"))){

                        events = object.getJSONArray("events");
                        break;

                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Intent intent = getIntent();
        url = Objects.requireNonNull(intent.getExtras()).getString("url");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        JSONArray jsonObject = null;

        try {
            jsonObject = new JSONArray(App.getCache().getAsString("configuration"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        loadEvents(jsonObject);

        Log.d("WEB URL",""+url);

        webView = (WebView) findViewById(R.id.webView);


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);


        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url){

                Log.d("CURRENT URL",""+url);

                App.reportURL(url);

                if (events != null && events.length() > 0){

                    for (int i = 0; i < events.length(); i++) {

                        try {
                            JSONObject event = events.getJSONObject(i);
                            if (url.contains(event.getString("url"))){

                                if (event.has("type")){

                                    if (event.getString("type").equals("register")){
                                        App.registerEvent(App.eventType.kEventRegister);
                                        Log.d("EVENT","REGISTER");
                                    }
                                    else if (event.getString("type").equals("deposit")){
                                        App.registerEvent(App.eventType.kEventDeposit);
                                        Log.d("EVENT","DEPOSIT");
                                    }

                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                }


                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                view.loadUrl(url);
                return false; // then it is not handled by default action
            }
        });

        if (url != null && url.length() > 0){

            webView.loadUrl(url);

        }

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

    @Override
    public void onBackPressed()
    {

        super.onBackPressed();

        super.finish();
    }
}
