package com.angeloparenteapp.earthquake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class MyWebView extends AppCompatActivity {

    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_web_view);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        mProgress = (ProgressBar) findViewById(R.id.progress_bar);
        WebView myWebView = (WebView) findViewById(R.id.webView);

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setSupportZoom(true);
        myWebView.getSettings().setBuiltInZoomControls(true);

        myWebView.loadUrl(url);

        myWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int progress) {
                super.onProgressChanged(view, progress);
                if(progress < 100 && mProgress.getVisibility() == ProgressBar.GONE){
                    mProgress.setVisibility(ProgressBar.VISIBLE);
                }

                mProgress.setProgress(progress);
                if(progress == 100) {
                    mProgress.setVisibility(ProgressBar.GONE);
                }
            }
        });
    }
}
