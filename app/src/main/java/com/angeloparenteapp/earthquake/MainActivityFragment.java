package com.angeloparenteapp.earthquake;

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

        ArrayList<EarthQuake> arrayList = new ArrayList<>();
        arrayList.add(new EarthQuake(8.9, "San Fran", 2010, "http"));
        arrayList.add(new EarthQuake(8.9, "San Fran2", 2010, "http"));
        arrayList.add(new EarthQuake(8.9, "San Fran3", 2010, "http"));
        arrayList.add(new EarthQuake(8.9, "San Fran4", 2010, "http"));
        arrayList.add(new EarthQuake(8.9, "San Fran5", 2010, "http"));

        ListView listView = (ListView) rootView.findViewById(R.id.listView);

        EarthQuakeAdapter earthQuakeAdapter = new EarthQuakeAdapter(rootView.getContext(), arrayList);

        listView.setAdapter(earthQuakeAdapter);

        return rootView;
    }
}
