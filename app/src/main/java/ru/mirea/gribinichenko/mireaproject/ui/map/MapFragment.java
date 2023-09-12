package ru.mirea.gribinichenko.mireaproject.ui.map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import ru.mirea.gribinichenko.mireaproject.databinding.FragmentMapBinding;

public class MapFragment extends Fragment {
    private MapView mapView = null;
    private boolean isWork = false;
    private FragmentMapBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = binding.mapView;

        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()));

        mapView.setZoomRounding(true);
        mapView.setMultiTouchControls(true);

        int loc1PermissionStatus = requireContext().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2PermissionStatus = requireContext().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (loc1PermissionStatus == PackageManager.PERMISSION_GRANTED && loc2PermissionStatus == PackageManager.PERMISSION_GRANTED) {
            isWork = true;
        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        if (isWork) {
            MyLocationNewOverlay locationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(requireContext()), mapView);
            locationNewOverlay.enableMyLocation();
            mapView.getOverlays().add(locationNewOverlay);

            locationNewOverlay.runOnFirstFix(new Runnable() {
                @Override
                public void run() {
                    try {
                        double lat = locationNewOverlay.getMyLocation().getLatitude();
                        double lon = locationNewOverlay.getMyLocation().getLongitude();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                IMapController mapController = mapView.getController();
                                mapController.setZoom(1.0);
                                GeoPoint point = new GeoPoint(lat, lon);
                                mapController.setCenter(point);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        CompassOverlay compassOverlay = new CompassOverlay(requireContext(), new InternalCompassOrientationProvider(requireContext()), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);

        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(mapView.getContext().getResources().getDisplayMetrics().widthPixels / 2, 10);
        mapView.getOverlays().add(scaleBarOverlay);

        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(55.794229, 37.700772));
        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                Toast.makeText(mapView.getContext(), "MIREA\nЧасы работы 09:00–21:00", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mapView.getOverlays().add(marker);
        marker.setIcon(getResources().getDrawable(org.osmdroid.library.R.drawable.osm_ic_follow_me_on));
        marker.setTitle("MIREA");

        Marker marker2 = new Marker(mapView);
        marker2.setPosition(new GeoPoint(55.698894, 37.805699));
        marker2.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                Toast.makeText(mapView.getContext(), "Buckets Empire\nЧасы работы 11:00–21:00", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mapView.getOverlays().add(marker2);
        marker2.setIcon(getResources().getDrawable(org.osmdroid.library.R.drawable.osm_ic_follow_me_on));
        marker2.setTitle("Buckets Empire");

        Marker marker3 = new Marker(mapView);
        marker3.setPosition(new GeoPoint(55.780249, 37.593135));
        marker3.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                Toast.makeText(mapView.getContext(), "ДЕПО\nЧасы работы 10:00–02:00", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mapView.getOverlays().add(marker3);
        marker3.setIcon(getResources().getDrawable(org.osmdroid.library.R.drawable.osm_ic_follow_me_on));
        marker3.setTitle("ДЕПО");
    }

    @Override
    public void onResume() {
        super.onResume();
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()));
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Configuration.getInstance().save(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()));
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            isWork = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
    }
}
