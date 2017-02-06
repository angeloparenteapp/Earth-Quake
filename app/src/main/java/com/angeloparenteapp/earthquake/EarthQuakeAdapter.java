package com.angeloparenteapp.earthquake;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by angel on 06/02/2017.
 */

public class EarthQuakeAdapter extends ArrayAdapter<EarthQuake> {

    public EarthQuakeAdapter(Context context, List<EarthQuake> earthquakes) {
        super(context, 0, earthquakes);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.earthquake_list_item, parent, false);
        }

        TextView magnitudeTextView = (TextView) listItemView.findViewById(R.id.magnitude);
        TextView locationTextView = (TextView) listItemView.findViewById(R.id.primary_location);
        TextView timeTextView = (TextView) listItemView.findViewById(R.id.date);

        EarthQuake currentEarthQuake = getItem(position);

        if (currentEarthQuake != null) {
            double mag = currentEarthQuake.getMagnitude();
            String loc = currentEarthQuake.getLocation();
            long time = currentEarthQuake.getTimeInMilliseconds();
            String url = currentEarthQuake.getUrl();

            magnitudeTextView.setText("" + mag);
            locationTextView.setText("" + loc);
            timeTextView.setText("" + time);
        }

        return listItemView;
    }
}
