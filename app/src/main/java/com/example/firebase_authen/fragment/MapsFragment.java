package com.example.firebase_authen.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.firebase_authen.activity.MainActivity;
import com.example.firebase_authen.R;
import com.example.firebase_authen.activity.SiteDetailActivity;
import com.example.firebase_authen.model.Site;
import com.example.firebase_authen.model.UserProfile;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsFragment extends Fragment {

    public GoogleMap mMap;

    private ArrayList<Marker> markers = new ArrayList<>();

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(true);
        }
    };

    public void refreshSites(ArrayList<Site> sites) {
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
        LatLng latLng;
        for (Site site : sites) {
            latLng = new LatLng(site.getLatitude(), site.getLongitude());
            if (site.getLeaderID().equals(UserProfile.getInstance(null).getUid())){
                markers.add(
                        mMap.addMarker(new MarkerOptions().position(latLng).title(site.getSiteID()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                );
            } else if (site.getVolunteers().contains(UserProfile.getInstance(null).getUid())) {
                markers.add(
                        mMap.addMarker(new MarkerOptions().position(latLng).title(site.getSiteID()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)))
                );
            }else {
                markers.add(
                        mMap.addMarker(new MarkerOptions().position(latLng).title(site.getSiteID()))
                );
            }
            if (getContext().getClass() == MainActivity.class) {
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        Intent intent = new Intent(getContext(), SiteDetailActivity.class);
                        intent.putExtra("siteID", marker.getTitle().toString());
                        startActivity(intent);
                        return true;
                    }
                });
            }
        }
    }

    public void focusSite(Site site){
        LatLng siteCoordinate = new LatLng(site.getLatitude(), site.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(siteCoordinate,12));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}