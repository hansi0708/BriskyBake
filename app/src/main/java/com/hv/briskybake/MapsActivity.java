package com.hv.briskybake;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.hv.briskybake.Common.Common;
import com.hv.briskybake.databinding.ActivityMapsBinding;
import com.hv.briskybake.directionhelper.FetchURL;
import com.hv.briskybake.directionhelper.TaskLoadedCallback;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {

    MarkerOptions place1, place2;
    Polyline currentPolyline;
    Button getDirection;
    LatLng latLng_user, latLng_server;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Toolbar toolbar = binding.getRoot().findViewById(R.id.toolbar);
        toolbar.setTitle("Live Map");
        double userlat = Double.parseDouble(Common.currentUser.zlatitude);
        double userlong = Double.parseDouble(Common.currentUser.zlongitude);
        latLng_user = new LatLng(userlat, userlong);
        latLng_server = new LatLng(Common.server_latitude, Common.server_longitude);

/*

        LocationManager locationManager2 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location2 = locationManager2.getLastKnownLocation(locationManager2.getBestProvider(criteria, false));

        if (location2 != null)
        {
            double userlat=location2.getLatitude();
            double userlong=location2.getLongitude();
            Log.e("MapsActivity", "onCreate: "+ userlat+" , "+userlong);
        }
*/

//        new FetchURL(MapsActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location));


        place1 = new MarkerOptions().position(latLng_user).title("User");
        place2 = new MarkerOptions().position(latLng_server).title("Server");

        String url = getUrl(place1.getPosition(), place2.getPosition(), "walking");
        new FetchURL(MapsActivity.this).execute(url, "walking");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
   /* @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(26.805964, 80.886396);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Log.d("mylog", "Added Markers");
        mMap.addMarker(place1);
        mMap.addMarker(place2);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng_user));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

    }


    private String getUrl(LatLng origin, LatLng destination, String directionMode) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + destination.latitude + "," + destination.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        Log.e("MapsActivity", "getUrl: " + url);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

}