package com.angeloparenteapp.earthquake;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    ArrayList<EarthQuake> earthquakes = new ArrayList<>();
    EarthQuakeAdapter earthQuakeAdapter;
    RequestQueue queue;
    ListView listView;
    public static final String TAG = "MyTag";

    final String url = "http://earthquake.usgs.gov/fdsnws/event/1/" +
            "query?format=geojson&" +
            "starttime=2017-01-01&" +
            "endtime=2017-01-31&" +
            "orderby=time-asc&" +
            "minlatitude=37&" +
            "maxlatitude=46&" +
            "minlongitude=6&" +
            "maxlongitude=18";

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        startVolley();

        listView = (ListView) rootView.findViewById(R.id.listView);

        earthQuakeAdapter = new EarthQuakeAdapter(getContext(), earthquakes);

        listView.setAdapter(earthQuakeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if (isOnline()) {
                    EarthQuake currentEarthquake = earthQuakeAdapter.getItem(position);
                    String url = currentEarthquake.getUrl();
                    Intent websiteIntent = new Intent(getContext(), MyWebView.class);
                    websiteIntent.putExtra("url", url);
                    startActivity(websiteIntent);
                } else {
                    Toast.makeText(getContext(), "You need a network connection for this", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    public void startVolley() {
        queue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        setEarthQuakes(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        jsObjRequest.setTag(TAG);
        queue.add(jsObjRequest);
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public ArrayList<EarthQuake> setEarthQuakes(JSONObject response) {

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
        return earthquakes;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }
}
