package com.example.firebase_authen.fragment;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.example.firebase_authen.R;
import com.example.firebase_authen.activity.MainActivity;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
            defaultFocus();
        }
    };

    public static String getAddress(Context context, Double longitude, Double latitude) throws IOException {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
        return addresses.get(0).getAddressLine(0);
    }

    public static SearchView.OnQueryTextListener searchLocation(Context context, SearchView searchView, MapsFragment map) {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;
                Geocoder geocoder = new Geocoder(context);
                try {
                    addressList = geocoder.getFromLocationName(location, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    map.focusLatLogZoom(new LatLng(address.getLatitude(), address.getLongitude()), 15);
                } else {
                    Toast.makeText(context, "Location not found", Toast.LENGTH_SHORT).show();
                    map.defaultFocus();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };
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

    public void refreshSites(ArrayList<Site> sites) {
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
        LatLng latLng;
        for (Site site : sites) {
            latLng = new LatLng(site.getLatitude(), site.getLongitude());
            if (site.getLeaderID().equals(UserProfile.getInstance(null).getUid())) {
                markers.add(
                        mMap.addMarker(new MarkerOptions().position(latLng).title(site.getSiteID()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                );
            } else if (site.getVolunteers().contains(UserProfile.getInstance(null).getUid())) {
                markers.add(
                        mMap.addMarker(new MarkerOptions().position(latLng).title(site.getSiteID()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)))
                );
            } else {
                markers.add(
                        mMap.addMarker(new MarkerOptions().position(latLng).title(site.getSiteID()))
                );
            }
            if (getContext().getClass() == MainActivity.class) {
                mMap.setOnMarkerClickListener(marker -> {
                    Intent intent = new Intent(getContext(), SiteDetailActivity.class);
                    intent.putExtra("siteID", marker.getTitle());
                    startActivity(intent);
                    return true;
                });
            }
        }
    }

    public void focusSite(Site site) {
        LatLng siteCoordinate = new LatLng(site.getLatitude(), site.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(siteCoordinate, 12));
    }

    public void defaultFocus() {
        double defaultLong = 106.660172;
        double defaultLat = 10.762622;
        LatLng siteCoordinate = new LatLng(defaultLat, defaultLong);
        focusLatLogZoom(siteCoordinate, 11);
    }

    public void focusLatLogZoom(LatLng latLng, int zoom) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
}