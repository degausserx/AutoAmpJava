package net.codejack.autoamp2;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.codejack.autoamp2.data.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;

public class RouteTrackingFragment extends Fragment implements OnMapReadyCallback {

    private DatabaseReference mDatabase;
    private FirebaseUser currentFirebaseUser;
    public static final String TAG = "RouteTracking";
    public static RouteTrackingFragment instance;
    private final int GPS_MIN_REFRESH = 10000;
    private final int GPS_MIN_DISTANCE = 1;
    protected boolean status = false;
    public String time = "";
    protected String name = "";
    public Student student;
    protected Location location;
    protected LocationManager locationManager;
    private GoogleMap mMap;
    private TextView user_text;
    private List<Double> latitude;
    private List<Double> longitude;
    private Timer timer;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_route_tracking, container, false);
        v = init(v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!status) {
            ((MainActivity) getActivity()).replaceScreen("Home");
        }
        ((MainActivity) this.getActivity()).setToolbarName(getResources().getString(R.string.app_name) + " - Route Tracker");
    }

    public RouteTrackingFragment() {

    }

    public static RouteTrackingFragment getInstance() {
        if (instance == null) instance = new RouteTrackingFragment();
        return instance;
    }

    protected void startTracking(String time, String name, Student student) {
        latitude = new ArrayList<Double>();
        longitude = new ArrayList<Double>();
        activity = ((MainActivity) this.getActivity());

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users/" + currentFirebaseUser.getUid()).child("routes");

        status = true;
        this.time = time;
        this.name = name;
        this.student = student;

        location = getLocation();
    }

    protected void stopTracking() {
        location = null;
        status = false;

        if (latitude.size() > 1) {
            mDatabase.child(time).child("student").setValue(student);
            mDatabase.child(time).child("coordinates").child("latitude").setValue(latitude);
            mDatabase.child(time).child("coordinates").child("longitude").setValue(longitude);
        }

        student = null;
        time = null;
        name = null;
    }

    private View init(View v) {
        user_text = (TextView) v.findViewById(R.id.start_tracking);
        if (status) user_text.setText(name);
        else user_text.setText("None");

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(instance);

        return v;
    }

    protected boolean isPermissive(Activity activity) {
        if (ActivityCompat.checkSelfPermission(MainActivity.context,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                return false;
            }
            return false;
        }
        return true;
    }

    public Location getLocation() {
        if (status) {
            if (location == null) {
                locationManager = (LocationManager) MainActivity.context.getSystemService(Context.LOCATION_SERVICE);
                if (locationManager != null) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return null;
                    }
                    Log.wtf(TAG, "SET LOCATION");
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_MIN_REFRESH, GPS_MIN_DISTANCE, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.wtf(TAG, "TEST");

                            Double lat = location.getLatitude();
                            Double lon = location.getLongitude();

                            latitude.add(lat);
                            longitude.add(lon);

                            LatLng current = new LatLng(lat, lon);
                            if (mMap != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
                            }
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    });
                }
            }
        }
        return location;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (location != null) {

            Double lat = location.getLatitude();
            Double lon = location.getLongitude();

            latitude.add(lat);
            longitude.add(lon);


            LatLng current = new LatLng(lat, lon);
            float zoomLevel = (float) 16.0;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, zoomLevel));

            String toast = "Your current coordinates are: LAT(" + lat + ") LONG(" + lon + ") ";
            Toast.makeText(MainActivity.context, toast,Toast.LENGTH_LONG).show();
        }

    }
}
