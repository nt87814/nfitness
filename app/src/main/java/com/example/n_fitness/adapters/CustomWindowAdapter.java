package com.example.n_fitness.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.n_fitness.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Adapter for displaying a custom window adapter for a marker on the map
 */
public class CustomWindowAdapter implements GoogleMap.InfoWindowAdapter {

    LayoutInflater mInflater;
    Context mContext;
    Marker cMarker;

    public CustomWindowAdapter(LayoutInflater i, Context context) {
        mInflater = i;
        mContext = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v = mInflater.inflate(R.layout.custom_info_window, null);
        cMarker = marker;
        // Populate fields
        TextView title = (TextView) v.findViewById(R.id.tv_info_window_title);
        title.setText(marker.getTitle());

        TextView description = (TextView) v.findViewById(R.id.tv_info_window_description);
        description.setText(marker.getSnippet());
        // Return info window contents
        return v;
    }

}
