package com.angeloparenteapp.earthquake;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A placeholder fragment.
 */
public class MainActivityFragment extends Fragment {

    ArrayList<EarthQuake> earthquakes = new ArrayList<>();
    EarthQuakeAdapter earthQuakeAdapter;
    RequestQueue queue;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView mEmptyStateTextView;
    AdView mAdView;
    InterstitialAd mInterstitialAd;
    int requestCode = 100;
    int count = 0;

    Bitmap bitmap;
    Intent intent;

    private static final String TAG = "QueueTag";
    private static final String BASE_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/";
    private String url = "";

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_name);

        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId("Your id");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                newInterstitialAd();
                super.onAdClosed();
            }
        });
        newInterstitialAd();

        bannerAd(rootView);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeLayout);
        listView = (ListView) rootView.findViewById(R.id.listView);
        mEmptyStateTextView = (TextView) rootView.findViewById(R.id.empty_view);

        listView.setEmptyView(mEmptyStateTextView);
        earthQuakeAdapter = new EarthQuakeAdapter(getContext(), earthquakes);

        if (QueryUtils.isOnline(getContext())) {
            bannerAd(rootView);
            startVolley();
        } else {
            mEmptyStateTextView.setText(R.string.no_internet);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (QueryUtils.isOnline(getContext())) {
                    bannerAd(rootView);
                    startVolley();
                } else {
                    earthQuakeAdapter.clear();
                    mEmptyStateTextView.setText(R.string.no_internet);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        listView.setAdapter(earthQuakeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                count++;

                if (QueryUtils.isOnline(getContext())) {

                    if (count <= 2) {
                        EarthQuake currentEarthquake = earthQuakeAdapter.getItem(position);

                        if (currentEarthquake != null) {
                            String url = currentEarthquake.getUrl();

                            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

                            builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                            builder.addDefaultShareMenuItem();

                            intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT, url);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                            builder.setActionButton(bitmap, "Share Link", pendingIntent, true);

                            CustomTabsIntent customTabsIntent = builder.build();
                            customTabsIntent.launchUrl(getActivity(), Uri.parse(url));
                        }
                    } else if (count > 2){
                        count = 0;

                        if (mInterstitialAd.isLoaded()){
                            mInterstitialAd.show();
                        }

                    }
                } else {
                    mEmptyStateTextView.setText(R.string.no_internet);
                }
            }
        });

        return rootView;
    }

    public void startVolley() {
        buildUrl();

        swipeRefreshLayout.setRefreshing(true);

        queue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            setEarthQuakes(response);
                            earthQuakeAdapter.notifyDataSetChanged();
                        } else {
                            mEmptyStateTextView.setText(R.string.json_error);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error == null) {
                            mEmptyStateTextView.setText(R.string.error_400);
                        } else {
                            mEmptyStateTextView.setText(getString(R.string.server_unknown_error));
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

        jsObjRequest.setTag(TAG);
        queue.add(jsObjRequest);
    }

    public ArrayList<EarthQuake> setEarthQuakes(JSONObject response) {

        earthQuakeAdapter.clear();

        try {
            JSONArray features = response.getJSONArray("features");

            for (int i = 0; i < features.length(); i++) {
                JSONObject current = features.getJSONObject(i);
                JSONObject properties = current.getJSONObject("properties");

                Double mag = properties.getDouble("mag");
                String place = properties.getString("place");
                long time = properties.getLong("time");
                String url = properties.getString("url");

                earthquakes.add(new EarthQuake(mag, place, time, url));
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        if (earthQuakeAdapter.isEmpty()) {
            mEmptyStateTextView.setText(R.string.no_earthquakes);
        }

        return earthquakes;
    }

    private void buildUrl() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        String date = sharedPrefs.getString(
                getString(R.string.date_picker_key),
                getString(R.string.date_picker_default));

        url = BASE_URL + date;

        Log.d("URL", url);
    }

    @Override
    public void onResume() {

        if (QueryUtils.isOnline(getContext())) {
            startVolley();
            mAdView.resume();
        } else {
            earthQuakeAdapter.clear();
            mEmptyStateTextView.setText(R.string.no_internet);
            swipeRefreshLayout.setRefreshing(false);
        }

        super.onResume();
    }

    @Override
    public void onStop() {

        if (queue != null) {
            queue.cancelAll(TAG);
        }

        super.onStop();
    }

    @Override
    public void onPause() {
        mAdView.pause();

        super.onPause();
    }

    @Override
    public void onDestroy() {

        mAdView.destroy();

        super.onDestroy();
    }

    private void newInterstitialAd() {
        AdRequest.Builder adRequest = new AdRequest.Builder();

        adRequest.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

        mInterstitialAd.loadAd(adRequest.build());
    }

    private void bannerAd(View view) {
        MobileAds.initialize(getContext(), "Your id");

        mAdView = (AdView) view.findViewById(R.id.adView);
        AdRequest.Builder adRequest = new AdRequest.Builder();

        adRequest.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

        mAdView.loadAd(adRequest.build());
    }
}