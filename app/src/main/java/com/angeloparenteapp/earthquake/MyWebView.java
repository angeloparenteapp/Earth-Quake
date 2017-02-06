package com.angeloparenteapp.earthquake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class MyWebView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_web_view);

        Intent intent = getIntent();
        String prova = intent.getStringExtra("url");

        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.loadUrl(prova);
    }
}
