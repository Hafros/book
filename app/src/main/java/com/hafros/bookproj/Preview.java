package com.hafros.bookproj;

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
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Objects;

public class Preview extends AppCompatActivity {

    ImageView imageView;
    Button continueBtn;
    Button continueBtnSecond;
    WebView webView;

    String url;
    String imgURL;
    String description;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = (WebView) findViewById(R.id.text);
        webView.setBackgroundColor(Color.TRANSPARENT);




        Intent intent = getIntent();
        url = Objects.requireNonNull(intent.getExtras()).getString("url");

        imgURL = Objects.requireNonNull(intent.getExtras()).getString("img");

        if (hasKey(intent,"description")){
            description = Objects.requireNonNull(intent.getExtras()).getString("description");
            Log.d("DESCRITPION",""+description);
            webView.loadData(description, "text/html; charset=utf-8", "UTF-8");
        }



        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        float ratio = 16.0f/9.0f;

        float end = width/ratio;

        imageView = (ImageView) findViewById(R.id.imageView);

        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();

        layoutParams.height = (int) end;

        App.loadImage(imgURL,imageView);

        continueBtn = (Button) findViewById(R.id.continueBtn);

        continueBtnSecond = (Button) findViewById(R.id.continueBtnSecond);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        continueBtnSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        if (description == null || description.length() == 0 || description.equals("{}")){
            continueBtnSecond.setVisibility(View.GONE);
            webView.setVisibility(View.GONE);
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
