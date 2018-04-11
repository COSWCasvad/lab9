package cosw.eci.edu.lab9;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;

import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    @SuppressLint("RestrictedApi")
    private final LocationRequest locationRequest = new LocationRequest();
    private TextView address;

    private final static int REQUEST_CODE = 10;
    private final static int GPS_INTENT = 60;
    private final int ACCESS_LOCATION_PERMISSION_CODE = 1;
    public final int ADD_LOCATION_INTENT =435;

    private Location location;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private FusedLocationProviderClient mFusedLocationClient;

    private boolean finded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        finded=true;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        address = (TextView) findViewById( R.id.address );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //changeLocation(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
                //setupMyLocation();
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, GPS_INTENT);
            }
        };

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 100, locationListener);
        //mySolution
        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //TeacherSolution
        googleApiClient =
                new GoogleApiClient.Builder( this ).addConnectionCallbacks( this ).addOnConnectionFailedListener(
                        this ).addApi( LocationServices.API ).build();
        locationRequest.setInterval( 10000 );
        locationRequest.setFastestInterval( 5000 );
        locationRequest.setPriority( LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY );
        googleApiClient.connect();

    }

    /**
     * Change the location to the given location
     * @param location, the new location
     */
    private void changeLocation(Location location) {
        this.location = location;
        mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),15));
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setupMyLocation();
        //mMap.addMarker(new MarkerOptions().position(new LatLng(locationGpsListener.getLatitude(), locationGpsListener.getLongitude())).title("My Location"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(locationGpsListener.getLatitude(), locationGpsListener.getLongitude())));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(locationGpsListener.getLatitude(), locationGpsListener.getLongitude())));

    }


    public void setupMyLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                }, REQUEST_CODE);
            }
        } else {
            showMyLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showMyLocation();
                }
                return;
        }
    }


    //mySolution
    /*@SuppressLint("MissingPermission")
    public void showMyLocation() {
        mMap.setMyLocationEnabled(true);
        location = getBestLastKnownLocation();

        if(location!=null){
            changeLocation(location);
        }else{
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                changeLocation(location);
                            }
                        }
                    });
        }
    }*/

    @SuppressLint("MissingPermission")
    public void showMyLocation()
    {
        if ( mMap != null && finded)
        {
            finded=false;
            String[] permissions = { android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION };
            if ( hasPermissions( this, permissions ) )
            {
                mMap.setMyLocationEnabled( true );
                Location lastLocation = LocationServices.FusedLocationApi.getLastLocation( googleApiClient );
                if ( lastLocation != null )
                {
                    addMarkerAndZoom( lastLocation, "My Location", 15 );
                }
            }
            else
            {
                ActivityCompat.requestPermissions( this , permissions, ACCESS_LOCATION_PERMISSION_CODE );
            }
        }
    }

    public static boolean hasPermissions( Context context, String[] permissions )
    {
        for ( String permission : permissions )
        {
            if ( ContextCompat.checkSelfPermission( context, permission ) == PackageManager.PERMISSION_DENIED )
            {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case GPS_INTENT:
                setupMyLocation();
                return;
            case ADD_LOCATION_INTENT:
                if(resultCode==this.RESULT_OK){
                    Location newLocation = (Location) data.getExtras().get("location");
                    String title = (String) data.getExtras().get("name");
                    title+=(String) data.getExtras().get("description");
                    addMarkerAndZoom(newLocation,title,5);
                }
        }
    }

    private Location getBestLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission") Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    public void addMarkerAndZoom( Location location, String title, int zoom  )
    {
        LatLng myLocation = new LatLng( location.getLatitude(), location.getLongitude() );
        mMap.addMarker( new MarkerOptions().position( myLocation ).title( title ) );
        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom( myLocation, zoom ) );
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        stopLocationUpdates();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        startLocationUpdates();
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates( googleApiClient, new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged( Location location ) {}
        } );
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates(){
        LocationServices.FusedLocationApi.requestLocationUpdates( googleApiClient, locationRequest,
                new com.google.android.gms.location.LocationListener() {
                    @Override
                    public void onLocationChanged( Location location ) {

                        showMyLocation();
                        stopLocationUpdates();
                    }
                });
    }


    public void onButtonFindAddressClicked(View view) {
        startFetchAddressIntentService();
    }

    @SuppressLint("MissingPermission")
    private void startFetchAddressIntentService() {
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if ( lastLocation != null ) {
            AddressResultReceiver addressResultReceiver = new AddressResultReceiver( new Handler() );
            addressResultReceiver.setAddressResultListener( new AddressResultListener() {
                @Override
                public void onAddressFound( final String address ){
                    runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            MapsActivity.this.address.setText( address );
                            MapsActivity.this.address.setVisibility( View.VISIBLE );
                        }
                    });
                }
            });
            Intent intent = new Intent( this, FetchAddressIntentService.class );
            intent.putExtra( FetchAddressIntentService.RECEIVER, addressResultReceiver );
            intent.putExtra( FetchAddressIntentService.LOCATION_DATA_EXTRA, lastLocation );
            startService( intent );
        }
    }

    public void onClickActionButton(View v){
        redirectToOtherActivity();
    }

    private void redirectToOtherActivity(){

        Intent intent = new Intent(this, AddLocationActivity.class);

        startActivityForResult(intent,ADD_LOCATION_INTENT);
    }



}
