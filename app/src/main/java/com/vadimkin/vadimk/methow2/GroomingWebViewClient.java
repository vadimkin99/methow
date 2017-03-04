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

    private final String conditionsUrl = "http://methowtrailsgrooming.org/conditions-iphone.php";

    @SuppressWarnings("deprecation")
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (url.startsWith(conditionsUrl)) {
            try {
                URL urlObj = new URL(url);
                return intercept(urlObj);
            } catch (MalformedURLException ex) {
                Log.e(Constants.LOG_TAG, "Bad grooming URL " + url);
            }
        } else if (url.startsWith("https://store.methowtrails.org/info.php?redirect_to=%2Fdonate.php")) {
            startExternalBrowser(url);
            return interceptToConditionsPage();
        }

        return null;
    }

    /**
     * Unconditional intercept to the Grooming page. For use in intercepting links from the Grooming
     * page for which we start the external browser. After firing off the intent to the browser,
     * we need to get back to Grooming.
     * @return
     */
    private WebResourceResponse interceptToConditionsPage() {
        URL conditionsUrlObj;
        try {
            conditionsUrlObj = new URL(conditionsUrl);
        } catch (Exception ex) {
            Log.e(Constants.LOG_TAG, "Bad grooming URL " + conditionsUrl);
            return null;
        }

        return intercept(conditionsUrlObj);
    }

    private void startExternalBrowser(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        activity.startActivity(i);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return shouldInterceptRequest(view, request.getUrl().toString());
    }

    private void activateProgressSpinner() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.progressBar.setVisibility(View.VISIBLE);
                activity.progressBar.getIndeterminateDrawable().setColorFilter(0xFFcccccc, android.graphics.PorterDuff.Mode.SRC_ATOP);
            }
        });
    }

    private void killProgressSpinner() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private WebResourceResponse intercept(URL url) {

        try {
            activateProgressSpinner();

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
                    // Something went wrong, just take the unaltered content
                    return null;
                }
            } catch (IOException ex) {
                Log.e(Constants.LOG_TAG, "Network error intercepting Grooming request: " + ex.getMessage());
            }

            // Something went wrong, just take the unaltered content
            return null;

        } finally {
            killProgressSpinner();
        }
    }
}
