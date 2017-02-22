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
import android.widget.TextView;
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

    //some variables
    ArrayList<EarthQuake> earthquakes = new ArrayList<>();
    EarthQuakeAdapter earthQuakeAdapter;
    RequestQueue queue;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    private TextView mEmptyStateTextView;
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

        listView = (ListView) rootView.findViewById(R.id.listView);

        mEmptyStateTextView = (TextView) rootView.findViewById(R.id.empty_view);

        listView.setEmptyView(mEmptyStateTextView);

        earthQuakeAdapter = new EarthQuakeAdapter(getContext(), earthquakes);

        if (QueryUtils.isOnline(getContext())) {
            startVolley();
        } else {
            mEmptyStateTextView.setText(R.string.no_internet);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (QueryUtils.isOnline(getContext())) {
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

                if (QueryUtils.isOnline(getContext())) {
                    EarthQuake currentEarthquake = earthQuakeAdapter.getItem(position);
                    String url = currentEarthquake.getUrl();
                    Intent websiteIntent = new Intent(getContext(), MyWebView.class);
                    websiteIntent.putExtra("url", url);
                    startActivity(websiteIntent);
                } else {
                    mEmptyStateTextView.setText(R.string.no_internet);                }
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
                            mEmptyStateTextView.setText(R.string.error_400);
                        } else {
                            mEmptyStateTextView.setText(R.string.some_problem);
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
