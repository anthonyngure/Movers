package ke.co.thinksynergy.movers.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.directions.route.Route;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.ButterKnife;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;
import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.activity.utils.BottomSheetManager;
import ke.co.thinksynergy.movers.activity.utils.DrawerManager;
import ke.co.thinksynergy.movers.activity.utils.OriginDestinationInputManager;
import ke.co.thinksynergy.movers.model.Mover;
import ke.co.thinksynergy.movers.network.BackEnd;
import ke.co.thinksynergy.movers.tasks.MoversSaverTask;
import ke.co.thinksynergy.movers.utils.Utils;
import ke.co.toshngure.basecode.log.BeeLog;
import ke.co.toshngure.basecode.networking.ConnectionListener;

public class MainActivity extends BaseActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ConnectionListener, OnLocationUpdatedListener, BottomSheetManager.Listener {

    private static final String TAG = "MainActivity";
    // A default location (Think Synergy Limited, Mombasa Road, Nairobi, Kenia) and
    // default zoom to use when location permission is not granted.
    private static final LatLng DEFAULT_LOCATION = new LatLng(-1.331014, 36.881207);
    private static final int DEFAULT_ZOOM = 14;
    private static final int WIDER_ZOOM = 10;


    private GoogleMap mGoogleMap;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates;

    // Location updates intervals
    public static int UPDATE_INTERVAL = 3000; // 3 sec
    public static int FASTEST_INTERVAL = 3000; // 5 sec
    public static int DISPLACEMENT = 10; // 10 meters

    private GoogleApiClient mGoogleApiClient;
    private ArrayList<Mover> mTopMoverList;
    private ArrayList<Marker> mServiceMarkersList;
    private DrawerManager mDrawerManager;
    private boolean hasWiderZoom;
    private Marker mOriginMapMarker;
    private Marker mDestinationMarker;
    private LocationGooglePlayServicesProvider locationGooglePlayServicesProvider;
    private Location mLastLocation;
    private BottomSheetManager mBottomSheetManager;
    private OriginDestinationInputManager mOriginDestinationInputManager;
    private Polyline mPolyLine;
    private int mCurrentKeyboardHeight;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mTopMoverList = new ArrayList<>();
        mServiceMarkersList = new ArrayList<>();

        mDrawerManager = DrawerManager.init(this, getToolbar());

        mBottomSheetManager = BottomSheetManager.init(this, this);

        mOriginDestinationInputManager = OriginDestinationInputManager.init(this, mBottomSheetManager);

        //Check If Google Services Is Available
        if (servicesAvailable()) {
            // Building the GoogleApi client
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
            mBottomSheetManager.showNotification("Google Service Is Available!!");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
        mCoordinatorLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            mCoordinatorLayout.getWindowVisibleDisplayFrame(r);
            int screenHeight = mCoordinatorLayout.getRootView().getHeight();
            mCurrentKeyboardHeight = screenHeight - (r.bottom);
        });

    }


    @Override
    protected void setUpStatusBarColor() {
        //super.setUpStatusBarColor();
    }

    private int getDefaultZoom() {
        int zoom;
        if (hasWiderZoom) {
            zoom = WIDER_ZOOM;
        } else {
            zoom = DEFAULT_ZOOM;
        }
        return zoom;
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        servicesAvailable();

        // Resuming the periodic location updates
        /*if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRequestingLocationUpdates) {
            stopLocationUpdates();
            mRequestingLocationUpdates = false;
        }
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    public boolean servicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {

            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Cannot Connect To Play Services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;

        //mGoogleMap.getUiSettings().setAllGesturesEnabled(false);

        mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.blue_essense));

        mLastLocation = SmartLocation.with(this).location().getLastLocation();
        if (mLastLocation != null) {
            @SuppressLint("DefaultLocale") String loc = String.format("[From Cache] Latitude %.6f," +
                    " Longitude %.6f", mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mBottomSheetManager.showNotification(loc);
            addOriginMarker(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        } else {
            mBottomSheetManager.showNotification("Adding default location");
            addOriginMarker(DEFAULT_LOCATION);
        }
        //Uncomment To Show Google Location Blue Pointer
        //mGoogleMap.setMyLocationEnabled(true);
    }


    //Starting the location updates
    protected void startLocationUpdates() {
        if (mRequestingLocationUpdates) {
            stopLocationUpdates();
        }
        mBottomSheetManager.showNotification("startLocationUpdates");

        locationGooglePlayServicesProvider = new LocationGooglePlayServicesProvider();
        locationGooglePlayServicesProvider.setCheckLocationSettings(true);


        SmartLocation.with(this).location(locationGooglePlayServicesProvider).start(this);
    }

    @Override
    public void onLocationUpdated(Location location) {
        mBottomSheetManager.showNotification("onLocationUpdated!");
        // Assign the new location
        // Displaying the new location on UI
        mLastLocation = location;
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            //Add origin pointer to the map at location
            addOriginMarker(new LatLng(latitude, longitude));

            // We are going to get the address for the current position
            mBottomSheetManager.resolveRetrievedLastLocation(location);

            connect();

        } else {
            mBottomSheetManager.showNotification("Couldn't get the location. Make sure location is enabled on the device");
        }


    }


    @Override
    public void onResolvePredictedOrigin(@NonNull Location location) {
        mLastLocation = location;
        addOriginMarker(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onResolvePredictedDestination(@NonNull Location location) {
        if (mLastLocation != null) {
            LatLng origin = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            LatLng destination = new LatLng(location.getLatitude(), location.getLongitude());
            mBottomSheetManager.resolveDirections(origin, destination);
        } else {
            mBottomSheetManager.showNotification("Requesting direction and LastLocation is null");
        }
    }

    @Override
    public void onResolveRetrievedLastLocation(String retrievedLastLocationName) {
        mOriginDestinationInputManager.onResolveRetrievedLastLocation(retrievedLastLocationName);
    }

    @Override
    public void onShowMyLocation() {
        if (mLastLocation != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                    .zoom(DEFAULT_ZOOM)
                    .build();
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onShowRoute(@NonNull Route route, BottomSheetManager.ShowRoutesListener listener) {

        //Remove poly line
        if (mPolyLine != null) {
            mPolyLine.remove();
        }

        if (mOriginMapMarker != null) {
            mOriginMapMarker.remove();
        }

        if (mDestinationMarker != null) {
            mDestinationMarker.remove();
        }

        /*Add markers*/

        List<LatLng> points = route.getPoints();

        MarkerOptions destinationMarkerOptions = new MarkerOptions()
                .position(points.get(points.size() - 1));
        mDestinationMarker = mGoogleMap.addMarker(destinationMarkerOptions);

        MarkerOptions originMarkerOptions = new MarkerOptions()
                .position(points.get(0));
        mOriginMapMarker = mGoogleMap.addMarker(originMarkerOptions);


        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLACK);
        polylineOptions.addAll(route.getPoints());

        mPolyLine = mGoogleMap.addPolyline(polylineOptions);


        //MapHelper.zoomToFitMarkers(mGoogleMap, mOriginMapMarker, mDestinationMarker);

        FrameLayout mMapContainer = findViewById(R.id.mapContainer);
        View bottomSheet = findViewById(R.id.bottomSheet);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mOriginMapMarker.getPosition());
        builder.include(mDestinationMarker.getPosition());
        builder.include(route.getLatLgnBounds().northeast);
        builder.include(route.getLatLgnBounds().southwest);
        LatLngBounds bounds = builder.build();

        //int width = getResources().getDisplayMetrics().widthPixels;
        int width = mMapContainer.getWidth();
        BeeLog.i(TAG, "Map Container Width " + width);
        BeeLog.i(TAG, "Bottom Sheet Height " + bottomSheet.getHeight());
        BeeLog.i(TAG, "Map Container Height " + mMapContainer.getHeight());
        int height = mMapContainer.getHeight() - bottomSheet.getHeight();
        int padding = (int) (width * 0.10); //
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        //mGoogleMap.moveCamera(cameraUpdate);
        mGoogleMap.animateCamera(cameraUpdate);


        listener.onFinish();
    }


    //Stopping location updates
    protected void stopLocationUpdates() {
        mBottomSheetManager.showNotification("stopLocationUpdates");
        SmartLocation.with(this).location().stop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mBottomSheetManager.showNotification("GoogleApiClient onConnected");
        // Once connected with google api, get the location
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        BeeLog.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void connect() {
        super.connect();
        if (mOriginMapMarker != null) {
            RequestParams params = new RequestParams();
            params.put(BackEnd.Params.LATITUDE, mOriginMapMarker.getPosition().latitude);
            params.put(BackEnd.Params.LONGITUDE, mOriginMapMarker.getPosition().longitude);
            /*Client.getInstance().getClient().get(Client.absoluteUrl(BackEnd.EndPoints.SERVICES),
                    params, new ResponseHandler(this));*/
        }
    }

    @Override
    public void onConnectionStarted() {
        //super.onConnectionStarted();
    }

    @Override
    protected void onSuccessResponse(JSONArray data, JSONObject meta) {
        super.onSuccessResponse(data, meta);
        mTopMoverList.clear();
        try {
            mTopMoverList = new MoversSaverTask(serviceProviders -> {
                showServiceProvidersMarkers();
            }).execute(data).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    private void showServiceProvidersMarkers() {
        if (mGoogleMap != null) {

            /*Remove current service markers*/
            for (Marker marker : mServiceMarkersList) {
                marker.remove();
            }
            mServiceMarkersList.clear();

            /*Add new services markers*/
            for (Mover mover : mTopMoverList) {
                BeeLog.i(TAG, "Adding Marker for " + mover.getName());
                LatLng latLong = new LatLng(mover.getLatitude(), mover.getLongitude());
                Marker marker = mGoogleMap.addMarker(
                        new MarkerOptions()
                                .position(latLong)
                                .snippet(Utils.toDistanceString(mover.getDistance()))
                                .title(mover.getName())
                                .icon(BitmapDescriptorFactory.fromBitmap(createMakerIcon(Utils.getMoverDrawable(mover)))));
                mServiceMarkersList.add(marker);
                //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, DEFAULT_ZOOM));
            }
        }

    }


    private Bitmap createMakerIcon(@DrawableRes int iconRes) {
        //Set Custom BitMap for Pointer
        int height = 80;
        int width = 45;
        BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(iconRes);
        Bitmap b = bitmapDraw.getBitmap();
        return Bitmap.createScaledBitmap(b, width, height, false);
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (mDrawerManager.getDrawer() != null && mDrawerManager.getDrawer().isDrawerOpen()) {
            mDrawerManager.getDrawer().closeDrawer();
        } else if (mBottomSheetManager.isOpen()) {
            if (mDestinationMarker != null) {
                mDestinationMarker.remove();
            }
            if (mPolyLine != null) {
                mPolyLine.remove();
            }
            addOriginMarker(mOriginMapMarker.getPosition());
            mBottomSheetManager.close();
        } else if (mOriginDestinationInputManager.isOpen()) {
            mOriginDestinationInputManager.close();
        } else {
            super.onBackPressed();
        }
    }


    private void addOriginMarker(LatLng latLng) {

        if (mGoogleMap != null) {
            if (mOriginMapMarker != null) {
                mOriginMapMarker.remove();
                mOriginMapMarker = null;
            }
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)
                    .zoom(getDefaultZoom())
                    .build();
            hasWiderZoom = false;
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            mOriginMapMarker = addMarker(latLng);
        }
    }

    protected Marker addMarker(LatLng latLng) {
        return mGoogleMap.addMarker(new MarkerOptions().position(latLng));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (locationGooglePlayServicesProvider != null) {
            locationGooglePlayServicesProvider.onActivityResult(requestCode, resultCode, data);
        }
    }


}
