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
    String primaryLocation;
    String locationOffset;

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
        TextView locationOffsetView = (TextView) listItemView.findViewById(R.id.location_offset);
        TextView primaryLocationView = (TextView) listItemView.findViewById(R.id.primary_location);
        TextView timeTextView = (TextView) listItemView.findViewById(R.id.date);
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);

        EarthQuake currentEarthQuake = getItem(position);

        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeTextView.getBackground();
        int magnitudeColor = getMagnitudeColor(currentEarthQuake.getMagnitude());
        magnitudeCircle.setColor(magnitudeColor);

        Double magnitude = currentEarthQuake.getMagnitude();
        String loc = currentEarthQuake.getLocation();
        long timeInMilliseconds = currentEarthQuake.getTimeInMilliseconds();

        if (loc.contains(LOCATION_SEPARATOR)) {
            String[] parts = loc.split(LOCATION_SEPARATOR);
            locationOffset = parts[0] + LOCATION_SEPARATOR;
            primaryLocation = parts[1];
        } else {
            locationOffset = getContext().getString(R.string.near_the);
            primaryLocation = loc;
        }

        Date dateObject = new Date(timeInMilliseconds);
        String dateToDisplay = formatDate(dateObject);
        String formattedTime = formatTime(dateObject);
        String formattedMagnitude = formatMagnitude(magnitude);

        magnitudeTextView.setText(formattedMagnitude);
        locationOffsetView.setText(locationOffset);
        primaryLocationView.setText(primaryLocation);
        timeTextView.setText(dateToDisplay);
        timeView.setText(formattedTime);

        return listItemView;
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    private String formatMagnitude(double magnitude) {
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(magnitude);
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }
}
