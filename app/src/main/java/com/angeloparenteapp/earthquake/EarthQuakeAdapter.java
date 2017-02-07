package com.angeloparenteapp.earthquake;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by angel on 06/02/2017.
 */

public class EarthQuakeAdapter extends ArrayAdapter<EarthQuake> {

    private static final String LOCATION_SEPARATOR = " of ";

    public EarthQuakeAdapter(Context context, List<EarthQuake> earthquakes) {
        super(context, 0, earthquakes);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Variable for split location
        String primaryLocation;
        String locationOffset;

        //If an empty view, populate
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.earthquake_list_item, parent, false);
        }

        //All views
        TextView magnitudeTextView = (TextView) listItemView.findViewById(R.id.magnitude);
        TextView locationOffsetView = (TextView) listItemView.findViewById(R.id.location_offset);
        TextView primaryLocationView = (TextView) listItemView.findViewById(R.id.primary_location);
        TextView timeTextView = (TextView) listItemView.findViewById(R.id.date);
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);

        //Current listView item to populate
        EarthQuake currentEarthQuake = getItem(position);

        //Custom circleView
        //Set the color of the circle depends on the magnitude strength
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeTextView.getBackground();
        int magnitudeColor = QueryUtils.getMagnitudeColor(currentEarthQuake.getMagnitude(), getContext());
        magnitudeCircle.setColor(magnitudeColor);

        //Get magnitude value
        Double magnitude = currentEarthQuake.getMagnitude();

        //Get location
        String loc = currentEarthQuake.getLocation();

        //Get time. Value in millisecond
        long timeInMilliseconds = currentEarthQuake.getTimeInMilliseconds();

        //Split location
        if (loc.contains(LOCATION_SEPARATOR)) {
            String[] parts = loc.split(LOCATION_SEPARATOR);
            locationOffset = parts[0] + LOCATION_SEPARATOR;
            primaryLocation = parts[1];
        } else {
            locationOffset = getContext().getString(R.string.near_the);
            primaryLocation = loc;
        }

        //Convert the date in a readable format and in a string
        Date dateObject = new Date(timeInMilliseconds);
        String dateToDisplay = QueryUtils.formatDate(dateObject);

        //Convert time in a readable format and in a string
        String formattedTime = QueryUtils.formatTime(dateObject);

        //Convert magnitude in a decimal and in a string
        String formattedMagnitude = QueryUtils.formatMagnitude(magnitude);

        //Set values in the view
        magnitudeTextView.setText(formattedMagnitude);
        locationOffsetView.setText(locationOffset);
        primaryLocationView.setText(primaryLocation);
        timeTextView.setText(dateToDisplay);
        timeView.setText(formattedTime);

        return listItemView;
    }
}
