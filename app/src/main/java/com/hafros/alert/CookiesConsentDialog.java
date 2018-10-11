/*
 * Copyright (C) 2015 Carlos Piñar Hafner.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hafros.alert;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.hafros.bookproj.R;
import com.hafros.bookproj.TextViewClickMovement;

public class CookiesConsentDialog extends CookiesConsentAlert {

    private boolean cancelable = false;

    public CookiesConsentDialog(@NonNull final Activity activity) {
        this.activity = activity;
    }

    public CookiesConsentDialog setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public CookiesConsentDialog setPolicyUrl(@NonNull String policyUrl) {
        this.policyUrl = policyUrl;
        return this;
    }

    public CookiesConsentDialog setListener(CookiesConsentListener listener) {
        this.listener = listener;
        return this;
    }

    protected void show() {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setNegativeButton(R.string.exit_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .setPositiveButton(R.string.continue_button_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        policyAccepted();
                    }
                })
                .setTitle(R.string.dialog_title)
                .setMessage(getFormattedMessage())
                .setCancelable(cancelable)
                .show();

        TextViewClickMovement textViewClickMovement = new TextViewClickMovement(new TextViewClickMovement.OnTextViewClickMovementListener() {
            @Override
            public void onLinkClicked(String linkText, TextViewClickMovement.LinkType linkType) {


                ((TextView) dialog.findViewById(android.R.id.message)).setText("");

                LayoutInflater inflater = activity.getLayoutInflater();

                View view_webview = inflater.inflate(R.layout.web_view,null);

                //webView.setBackgroundColor(Color.RED);

                Button cancel = view_webview.findViewById(R.id.cancelBtn);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.finish();
                    }
                });

                Button agree = view_webview.findViewById(R.id.agreeBtn);

                agree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        policyAccepted();
                        dialog.dismiss();
                    }
                });

                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
//
                WebView webView1 = view_webview.findViewById(R.id.webView);

                //webView1.setBackgroundColor(Color.YELLOW);

                webView1.loadUrl(String.valueOf("http://myappterms.com/reader.php?lang=en&id=1"));

                webView1.setWebViewClient(new WebViewClient() {

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {

                        view.loadUrl(urlNewString);
                        return true;
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {

                    }
                });
//                webView.setLayoutParams(params);
//
//                dialog.setView(webView);

                dialog.addContentView(view_webview,params);
                return;

            }

            @Override
            public void onLongClick(String text) {

            }
        }, activity);


        ((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(textViewClickMovement);
    }
}
