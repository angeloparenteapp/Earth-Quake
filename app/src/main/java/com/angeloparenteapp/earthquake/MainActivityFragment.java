package com.angeloparenteapp.earthquake;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
 * A placeholder fragment.
 */
public class MainActivityFragment extends Fragment {

    ArrayList<EarthQuake> earthquakes = new ArrayList<>();
    EarthQuakeAdapter earthQuakeAdapter;
    RequestQueue queue;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    public static final String TAG = "QueueTag";

    final String url = "http://earthquake.usgs.gov/fdsnws/event/1/" +
            "query?format=geojson&" +
            "starttime=2017-01-01&" +
            "endtime=2017-12-31&" +
            "minmagnitude=3&" +
            "orderby=time&" +
            "minlatitude=31&" +
            "maxlatitude=69&" +
            "minlongitude=-141&" +
            "maxlongitude=-53";

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeLayout);

        if (QueryUtils.isOnline(getContext())) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    startVolley();
                }
            });
        } else {
            Toast.makeText(getContext(), "You need an internet connection", Toast.LENGTH_SHORT).show();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (QueryUtils.isOnline(getContext())) {
                    startVolley();
                } else {
                    Toast.makeText(getContext(), "You need a network connection for this", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        listView = (ListView) rootView.findViewById(R.id.listView);

        earthQuakeAdapter = new EarthQuakeAdapter(getContext(), earthquakes);

        listView.setAdapter(earthQuakeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if (QueryUtils.isOnline(getContext())) {
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
        swipeRefreshLayout.setRefreshing(true);

        queue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        setEarthQuakes(response);
                        earthQuakeAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse.statusCode == 400) {
                            Toast.makeText(getContext(), "Error 400: Bed Request", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Some problem", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);

                    }
                });
        jsObjRequest.setTag(TAG);
        queue.add(jsObjRequest);
    }

    public ArrayList<EarthQuake> setEarthQuakes(JSONObject response) {

        if (earthquakes != null || earthQuakeAdapter != null) {
            earthQuakeAdapter.clear();
            earthquakes.clear();
        }

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
