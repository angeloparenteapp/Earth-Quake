package com.angeloparenteapp.earthquake;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final ArrayList<EarthQuake> earthquakes = QueryUtils.extractEarthquakes();

        ListView listView = (ListView) rootView.findViewById(R.id.listView);

        final EarthQuakeAdapter earthQuakeAdapter = new EarthQuakeAdapter(rootView.getContext(), earthquakes);

        listView.setAdapter(earthQuakeAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if (isOnline()) {
                    EarthQuake currentEarthquake = earthQuakeAdapter.getItem(position);
                    String web = currentEarthquake.getUrl();
                    Intent websiteIntent = new Intent(getContext(), MyWebView.class);
                    websiteIntent.putExtra("url", web);
                    startActivity(websiteIntent);
                } else {
                    Toast.makeText(getContext(), "You need a network connection for this", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
