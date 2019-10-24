package com.example.wroute;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    Button locateButton;
    Button routeButton;
    Button weatherButton;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    LatLng current;
    LatLng destination;
    ArrayList<LatLng> pointsArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        locateButton = findViewById(R.id.location);
        routeButton = findViewById(R.id.route);
        weatherButton = findViewById(R.id.weather);
        weatherButton.setVisibility(View.INVISIBLE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);



        Places.initialize(getApplicationContext(), "YOUR_API_KEY");
        PlacesClient placesClient = Places.createClient(this);
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG,Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng latLngDest = place.getLatLng();
                destination = latLngDest;
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLngDest);
                mMap.addMarker(markerOptions);
                CameraPosition cameraPosition1 = new CameraPosition.Builder()
                        .target(latLngDest)
                        .tilt(90)
                        .zoom(17)
                        .build();
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition1));


            }
            @Override
            public void onError(Status status) {
                Log.i("tag1", "An error occurred: " + status);
            }
        });

        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = makeUrl(current,destination);
                String result = "";
                DownloadTask task = new DownloadTask();
                try {
                    result = task.execute(URL).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pointsArr = parsePoints(result);
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.addAll(pointsArr);
                polylineOptions.width(5).color(Color.BLUE);
                mMap.addPolyline(polylineOptions);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(current);
                builder.include(destination);
                LatLngBounds bounds = builder.build();
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,50);
                mMap.animateCamera(cu);
                weatherButton.setVisibility(View.VISIBLE);





            }
        });

        mapFragment.getMapAsync(this);
    }

    private String makeUrl(LatLng current, LatLng destination) {
        String strCurrent = String.valueOf(current.latitude) + "," + String.valueOf(current.longitude);
        String strDestination = String.valueOf(destination.latitude) + "," + String.valueOf(destination.longitude);
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                + strCurrent + "&destination=" + strDestination
                + "&key=YOUR_API_KEY";
        return url;
    }

    private ArrayList<LatLng> parsePoints(String result){
        String points = "";
        ArrayList<LatLng> pointsArr = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray routesArr = (JSONArray) jsonObject.get("routes");
            JSONObject routes = routesArr.getJSONObject(0);
            JSONObject poly = routes.getJSONObject("overview_polyline");
            points = poly.getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pointsArr = (ArrayList<LatLng>) PolyUtil.decode(points);
        return pointsArr;
    }

    private void fetchLocation() {
        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(this)
                        .setTitle("Required Location Permission")
                        .setMessage("You have to give this permission to acess this feature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                LatLng currLoc = new LatLng(location.getLatitude(),
                                        location.getLongitude());
                                current = currLoc;
                                //mMap.clear();
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(currLoc);
                                mMap.addMarker(markerOptions);
                                CameraPosition cameraPosition1 = new CameraPosition.Builder()
                                        .target(currLoc)
                                        .tilt(90)
                                        .zoom(17)
                                        .build();
                                mMap.animateCamera(CameraUpdateFactory
                                        .newCameraPosition(cameraPosition1));

                            }
                        }
                    });

        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        locateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchLocation();
            }
        });

        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),WeatherActivity.class);
                intent.putExtra("latlngArray", pointsArr);
                startActivity(intent);
            }
        });

        }

}

