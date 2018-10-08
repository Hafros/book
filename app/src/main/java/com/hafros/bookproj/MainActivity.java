package com.hafros.bookproj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.aspsine.irecyclerview.IRecyclerView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cn.refactor.multistatelayout.MultiStateLayout;
import cn.refactor.multistatelayout.OnStateViewCreatedListener;

public class MainActivity extends AppCompatActivity {

    IRecyclerView recyclerView;

    private ArrayList<DataModel> mData = new ArrayList<>();

    private DataAdapter adapter;

    private JSONArray items;

    AppEventsLogger logger;

    CallbackManager callbackManager = CallbackManager.Factory.create();

    private MultiStateLayout multiStateLayout;

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.setApplicationId("332276177350526");

        logger = AppEventsLogger.newLogger(this);

       // FacebookSdk.sdkInitialize(getApplicationContext());
      //  AppEventsLogger.activateApp(this);
      //  AppEventsLogger.

        multiStateLayout = (MultiStateLayout) findViewById(R.id.multi_state_layout);

        multiStateLayout.setOnStateViewCreatedListener(new OnStateViewCreatedListener() {
            @Override
            public void onViewCreated(View view, int i) {

                if (i == MultiStateLayout.State.EMPTY){

                    RelativeLayout mainView = (RelativeLayout) view.findViewById(R.id.emptyView);

                    LoginButton loginButton = mainView.findViewById(R.id.login_button);

                    loginButton.setReadPermissions("email", "public_profile", "user_friends");


                    loginButton.registerCallback(callbackManager
                    , new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            showContent();
                            Log.d("FACEBOOK","LOGGED");
                        }

                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onError(FacebookException error) {
                            Log.d("FACEBOOK",""+error.getLocalizedMessage());
                        }
                    });
                    //mainView.setBackgroundColor(getResources().getColor(R.color.backgroundColor));

                }

//                if (i == MultiStateLayout.State.NETWORK_ERROR){
//
//                    RelativeLayout mainView = (RelativeLayout) view.findViewById(R.id.emptyView);
//                    mainView.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
//
//                }
//
//                if (i == MultiStateLayout.State.LOADING){
//
//                    ProgressWheel wheel = (ProgressWheel) view.findViewById(R.id.progress);
//
//                    wheel.setLinearProgress(false);
//                    wheel.setRimColor(getResources().getColor(R.color.preloader));
//                    wheel.setBarColor(getResources().getColor(R.color.backgroundColor));
//                    wheel.setBarWidth(10);
//                    wheel.setRimWidth(10);
//
//                    wheel.spin();
//
//                    wheel.requestLayout();
//
//                    RelativeLayout mainView = (RelativeLayout) view.findViewById(R.id.loadingView);
//
//                    mainView.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
//
//                }


            }
        });

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        showContent();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        if (!isLoggedIn() && App.getCache().getAsString("facebook") != null){

            multiStateLayout.setState(MultiStateLayout.State.EMPTY);

        }

        App.needUpdateHandler = new App.needUpdateHandler() {
            @Override
            public void callback() {
                checkConfiguration();
            }
        };

        App.eventHandler = new App.eventHandler() {
            @Override
            public void callback(App.eventType event) {

                switch (event){

                    case kEventDeposit:
                        logger.logEvent("COMPLETE_DEPOSIT");
                        break;

                    case kEventRegister:
                        logger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION);
                        break;

                    default:
                        break;
                }

            }
        };

        recyclerView = (IRecyclerView) findViewById(R.id.list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        float ratio = 16.0f/9.0f;

        float end = width/ratio;

//        Log.d("WIDTH = ",""+width);
//
//        Log.d("HEIGHT",""+ratio);

        adapter = new DataAdapter(mData, end);

        recyclerView.setAdapter(adapter);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

//                if ((mData.get(position).url != null && !mData.get(position).url.isEmpty())){
//
//                    Intent intent = new Intent(MainActivity.this, WebActivity.class);
//                    intent.putExtra("url", ""+mData.get(position).url);
//                    startActivity(intent);
//
//                }
//                else{
                    Intent intent = new Intent(MainActivity.this, WebActivity.class);
//
                    intent.putExtra("url", ""+mData.get(position).url);
                    startActivity(intent);
//                }

            }
        });

        updateItems();


        checkConfiguration();


    }

    private void showContent(){

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                multiStateLayout.setState(MultiStateLayout.State.CONTENT);
            }
        });

    }


    private void checkConfiguration(){

        RequestManager.checkConfiguration(new NetworkHandler() {
            @Override
            public void successHandler(String body) throws JSONException {

                try{
                    JSONArray array = new JSONArray(body);


                    if (!App.getCache().getAsString("configuration").equals(body)){
                        App.getCache().put("configuration",body);
                        updateItems();
                    }

                }
                catch (JSONException e){

                }

            }

            @Override
            public void failHandler(String error) {

            }
        });

    }


    private void updateAdapter(){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void updateItems(){

        mData.clear();

        try {
            JSONArray jsonArray = new JSONArray(App.getCache().getAsString("configuration"));
            items = jsonArray;

            for (int i = 0; i < items.length(); i++) {
                DataModel model = new DataModel(items.getJSONObject(i));
                mData.add(model);
            }

            Log.d("ITEMS", ""+items.length());

            updateAdapter();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
