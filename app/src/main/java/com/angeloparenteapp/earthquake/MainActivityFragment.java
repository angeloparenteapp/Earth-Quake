package com.angeloparenteapp.earthquake;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


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

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    Double latitude;
    Double longitude;

    Bitmap bitmap;
    Intent intent;

    private static final String TAG = "QueueTag";
    private static final String BASE_WORLD_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/";
    private String url = "";

    FusedLocationProviderClient mFusedLocationClient;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_name);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-2203645065011301/1277467675");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                newInterstitialAd();
                super.onAdClosed();
            }
        });

        newInterstitialAd();
        bannerAd(rootView);

        swipeRefreshLayout = rootView.findViewById(R.id.swipeLayout);
        listView = rootView.findViewById(R.id.listView);
        mEmptyStateTextView = rootView.findViewById(R.id.empty_view);

        listView.setEmptyView(mEmptyStateTextView);
        earthQuakeAdapter = new EarthQuakeAdapter(getContext(), earthquakes);

        if (QueryUtils.isOnline(getContext())) {
            bannerAd(rootView);
            newInterstitialAd();
            startVolley();
        } else {
            mEmptyStateTextView.setText(R.string.no_internet);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (QueryUtils.isOnline(getContext())) {
                    bannerAd(rootView);
                    newInterstitialAd();
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
                    } else if (count > 2) {
                        count = 0;
                        mInterstitialAd.show();
                    }
                } else {
                    if (getView() != null) {
                        Snackbar.make(getView(), getString(R.string.no_internet), Snackbar.LENGTH_LONG).show();
                    }
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
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            mEmptyStateTextView.setText(R.string.server_unknown_error);
                            swipeRefreshLayout.setRefreshing(false);
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean isLocationChecked = preferences.getBoolean(getString(R.string.use_my_location_key), true);

        if ((isLocationChecked) && checkLocationPermission()) {

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }

                    });

            url = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson" +
                    "&starttime=2000-01-01" +
                    "&endtime=2017-07-19" +
                    "&minmagnitude=2" +
                    "&latitude=" + latitude +
                    "&longitude=" + longitude +
                    "&maxradiuskm=300";

        } else {

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

            String date = sharedPrefs.getString(getString(R.string.date_picker_key),
                    getString(R.string.date_picker_default));

            url = BASE_WORLD_URL + date;
        }
    }

    private void newInterstitialAd() {
        AdRequest.Builder adRequest = new AdRequest.Builder();

        adRequest.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

        mInterstitialAd.loadAd(adRequest.build());
    }

    private void bannerAd(View view) {
        MobileAds.initialize(getContext(), "ca-app-pub-2203645065011301~2594044072");

        mAdView = view.findViewById(R.id.adView);
        AdRequest.Builder adRequest = new AdRequest.Builder();

        adRequest.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

        mAdView.loadAd(adRequest.build());
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);

                            }
                        }).create().show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        // Got last known location. In some rare situations this can be null.
                                        if (location != null) {
                                            latitude = location.getLatitude();
                                            longitude = location.getLongitude();
                                        }
                                    }
                                });
                    } else {
                        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

                        String date = sharedPrefs.getString(getString(R.string.date_picker_key),
                                getString(R.string.date_picker_default));

                        url = BASE_WORLD_URL + date;
                    }

                } else {
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

                    String date = sharedPrefs.getString(getString(R.string.date_picker_key),
                            getString(R.string.date_picker_default));

                    url = BASE_WORLD_URL + date;
                    startVolley();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        buildUrl();

        if (QueryUtils.isOnline(getContext())) {
            startVolley();
            mAdView.resume();
        } else {
            if (getView() != null) {
                Snackbar.make(getView(), getString(R.string.need_internet), Snackbar.LENGTH_LONG).show();
            }
            swipeRefreshLayout.setRefreshing(false);
        }
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
        buildUrl();

        mAdView.pause();

        super.onPause();
    }

    @Override
    public void onDestroy() {

        mAdView.destroy();

        super.onDestroy();
    }

}