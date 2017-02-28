package com.vadimkin.vadimk.methow2;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/**
 * Created by Administrator on 2/17/2017.
 */

public class GroomingWebViewClient extends WebViewClient {
    private MainActivity activity = null;

    public GroomingWebViewClient(MainActivity activity) {
        this.activity = activity;
    }
//
//    @Override
//    @SuppressWarnings("deprecation")
//    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//        activity.startActivity(intent);
//        return true;
//    }
//
//    @TargetApi(Build.VERSION_CODES.N)
//    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//        Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
//        activity.startActivity(intent);
//        return true;
//    }

    @SuppressWarnings("deprecation")
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (url.startsWith("http://methowtrailsgrooming.org/conditions-iphone.php")) {
            try {
                URL urlObj = new URL(url);
                return intercept(urlObj);
            } catch (MalformedURLException ex) {
                Log.e(Constants.LOG_TAG, "Bad grooming URL " + url);
            }
        }

        return null;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return shouldInterceptRequest(view, request.getUrl().toString());
    }

    private WebResourceResponse intercept(URL url) {

        try {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.progressBar.setVisibility(View.VISIBLE);
                    activity.progressBar.getIndeterminateDrawable().setColorFilter(0xFFcccccc, android.graphics.PorterDuff.Mode.SRC_ATOP);
                }
            });

            String mimeType = "text/html";
            String encoding = "";

            StringBuilder sb = null;
            try {
                URLConnection urlConnection = url.openConnection();
                InputStream input = urlConnection.getInputStream();

                if (input != null) {
                    BufferedReader br = null;
                    sb = new StringBuilder();

                    String line;
                    try {

                        br = new BufferedReader(new InputStreamReader(input));
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (br != null) {
                            try {
                                br.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                if (sb != null) {
                    String inputString = sb.toString();
                    inputString = inputString.replace("<a href=\"http://www.methowdata.com/mvsta/waxing/waxing.cfm\">wax of the day</a>", "");
                    inputString = inputString.replace("<br>", "");
                    InputStream stream = new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8));
                    WebResourceResponse response = new WebResourceResponse(mimeType, encoding, stream);
                    return response;
                } else {
                    return null;
                }
            } catch (IOException ex) {
                Log.e(Constants.LOG_TAG, "Network error interpting Grooming request: " + ex.getMessage());
            }

            return null;
        } finally {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}
