package ke.co.thinksynergy.movers.activity.utils;

import android.location.Address;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.geocoding.utils.LocationAddress;
import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.utils.Spanny;
import ke.co.toshngure.basecode.log.BeeLog;

/**
 * Created by Anthony Ngure on 06/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class BottomSheetManager {

    private static final String TAG = "BottomSheetManager";
    private static final int MAX_RESOLVE_RETRY = 5;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.bottomSheet)
    View mBottomSheet;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.mapContainer)
    FrameLayout mMapContainer;
    @BindView(R.id.errorTV)
    TextView mErrorTV;

    private Listener mListener;
    private AppCompatActivity mContext;
    private BottomSheetBehavior mBottomSheetBehavior;
    private boolean isShowingOptionsView;
    private int mResolvePredictedOriginRetryCount;
    private int mResolvePredictedDestinationRetryCount;
    private int mAppBarHeight;
    private BottomSheetOptionsManager mBottomSheetOptionsManager;

    private BottomSheetManager(AppCompatActivity activity, Listener listener) {
        this.mContext = activity;
        this.mListener = listener;
        this.mBottomSheetOptionsManager = BottomSheetOptionsManager.init(activity);
        mAppBarHeight = activity.getResources().getDimensionPixelSize(R.dimen.main_app_bar_height);
        this.init();
    }

    private void init() {

        ButterKnife.bind(this, mContext);

        //listen for states callback
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        BeeLog.d(TAG, "STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        BeeLog.d(TAG, "STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        BeeLog.d(TAG, "STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        BeeLog.d(TAG, "STATE_HIDDEN");
                        break;
                    default:
                        BeeLog.d(TAG, "STATE_SETTLING");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        mFab.setOnClickListener(view -> mListener.onShowMyLocation());

        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void onStartLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void onFinishLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    private void onError(Object error) {
        mErrorTV.setVisibility(View.VISIBLE);
        mErrorTV.setText(String.valueOf(error));
    }

    public static BottomSheetManager init(AppCompatActivity appCompatActivity, Listener listener) {
        return new BottomSheetManager(appCompatActivity, listener);
    }

    public void resolveRetrievedLastLocation(Location retrievedLocation) {
        onStartLoading();
        showNotification("resolveRetrievedLastLocation");
        SmartLocation.with(mContext).geocoding()
                .reverse(retrievedLocation, (location, results) -> {
                    if (results.size() > 0) {
                        Address result = results.get(0);
                        StringBuilder builder = new StringBuilder();
                        List<String> addressElements = new ArrayList<>();
                        for (int i = 0; i <= result.getMaxAddressLineIndex(); i++) {
                            addressElements.add(result.getAddressLine(i));
                        }
                        builder.append(TextUtils.join(", ", addressElements));

                        mListener.onResolveRetrievedLastLocation(builder.toString());
                        showNotification(("resolveRetrievedLastLocation = " + builder.toString()));
                        onFinishLoading();
                    } else {
                        showNotification("Unable to resolve retrieved last location and will not retry ");
                        //showNotification("resolveRetrievedLastLocation, RETRY");
                        //resolveRetrievedLastLocation(location);
                    }
                });
    }

    public void resolvePredictedDestination(AutocompletePrediction destinationPrediction) {
        hideOptionsView();
        mResolvePredictedDestinationRetryCount = 0;
        showNotification("resolvePredictedDestination");
        resolvePredictedDestination(destinationPrediction.getFullText(null).toString());
    }

    private void resolvePredictedDestination(String destination) {
        onStartLoading();
        SmartLocation.with(mContext).geocoding()
                .direct(destination, (original, results) -> {
                    // name is the same you introduced in the parameters of the call
                    // results could come empty if there is no match, so please add some checks around that
                    // LocationAddress is a wrapper class for Address that has a Location based on its data
                    if (results.size() > 0) {
                        LocationAddress locationAddress = results.get(0);
                        Spanny spanny = new Spanny("Formatted Address=\n");
                        spanny.append(locationAddress.getFormattedAddress())
                                .append("Address=\n")
                                .append(locationAddress.getAddress().toString());
                        showNotification(spanny);
                        mListener.onResolvePredictedDestination(locationAddress.getLocation());
                    } else {
                        showNotification("resolvePredictedDestination, RETRY = " + mResolvePredictedDestinationRetryCount);
                        if (mResolvePredictedDestinationRetryCount <= MAX_RESOLVE_RETRY) {
                            mResolvePredictedDestinationRetryCount++;
                            resolvePredictedDestination(original);
                        } else {
                            onError("Unable to resolve Predicted Destination");
                        }
                    }
                });
    }

    public void resolvePredictedOrigin(AutocompletePrediction originPrediction) {
        hideOptionsView();
        mResolvePredictedOriginRetryCount = 0;
        showNotification("resolveEnteredOrigin");
        resolvePredictedOrigin(originPrediction.getFullText(null).toString());
    }

    private void resolvePredictedOrigin(String originPrediction) {
        onStartLoading();
        SmartLocation.with(mContext).geocoding()
                .direct(originPrediction, (original, results) -> {
                    // name is the same you introduced in the parameters of the call
                    // results could come empty if there is no match, so please add some checks around that
                    // LocationAddress is a wrapper class for Address that has a Location based on its data
                    if (results.size() > 0) {
                        LocationAddress locationAddress = results.get(0);
                        Spanny spanny = new Spanny("Formatted Address=\n");
                        spanny.append(locationAddress.getFormattedAddress())
                                .append("Address=\n")
                                .append(locationAddress.getAddress().toString());
                        showNotification(spanny);
                        mListener.onResolvePredictedOrigin(locationAddress.getLocation());
                        // TODO: 09/01/2018 if destination is not null get load directions
                        onFinishLoading();
                    } else {
                        showNotification("resolvePredictedOrigin, RETRY = " + mResolvePredictedOriginRetryCount);
                        if (mResolvePredictedOriginRetryCount <= MAX_RESOLVE_RETRY) {
                            mResolvePredictedOriginRetryCount++;
                            resolvePredictedOrigin(original);
                        } else {
                            onError("Unable to resolve Predicted Origin");
                        }

                    }
                });
    }

    public void resolveDirections(LatLng origin, LatLng destination) {
        showNotification("resolveDirections");
        new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                .waypoints(origin, destination)
                .alternativeRoutes(false)
                .withListener(new RoutingListener() {
                    @Override
                    public void onRoutingFailure(RouteException e) {
                        showNotification("onRoutingFailure, Message = " + e.getMessage() + " Status = " + e.getStatusCode());
                        onFinishLoading();
                    }

                    @Override
                    public void onRoutingStart() {
                        showNotification("onRoutingStart");
                        onStartLoading();
                    }

                    @Override
                    public void onRoutingSuccess(ArrayList<Route> routes, int shortestRouteIndex) {
                        showNotification("onRoutingSuccess");
                        showOptions(routes.get(0));
                        onFinishLoading();
                    }

                    @Override
                    public void onRoutingCancelled() {
                        showNotification("onRoutingCancelled");
                        onFinishLoading();
                    }
                })
                .key(mContext.getString(R.string.google_maps_key))
                .build().execute();

        /*GoogleDirection.withServerKey(mContext.getString(R.string.google_maps_key))
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        showNotification("onDirectionSuccess, isOK = " + direction.isOK());
                        if (direction.isOK()) {
                            // Do something
                            showOptions(direction);
                        } else {
                            // Do something
                            showNotification("Status = " + direction.getStatus() + "\nMessage\n" + direction.getErrorMessage());
                            onFinishLoading();
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something
                        showNotification("onDirectionFailure " + String.valueOf(t.getMessage()));
                        onFinishLoading();
                    }
                });*/
    }

    private void hideOptionsView() {
        isShowingOptionsView = true;
        //Create an anchor point that will allow showing the map
        CoordinatorLayout.LayoutParams mapContainerLayoutParams = (CoordinatorLayout.LayoutParams)
                mMapContainer.getLayoutParams();
        mapContainerLayoutParams.setMargins(0, mAppBarHeight, 0, 0);
        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void showOptionsView() {
        isShowingOptionsView = true;
        //Create an anchor point that will allow showing the map
        CoordinatorLayout.LayoutParams mapContainerLayoutParams = (CoordinatorLayout.LayoutParams)
                mMapContainer.getLayoutParams();
        int peekHeight = mBottomSheet.getHeight();
        mapContainerLayoutParams.setMargins(0, mAppBarHeight, 0, peekHeight);
        mBottomSheetBehavior.setPeekHeight(peekHeight);
        mBottomSheetBehavior.setHideable(false);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void showOptions(Route route) {
        showOptionsView();
        mBottomSheetOptionsManager.update(route);
        //Now the map is at the top, request listener to show the route
        mListener.onShowRoute(route, () -> mProgressBar.setVisibility(View.GONE));
    }


    public synchronized void showNotification(Object msg) {
        BeeLog.i(TAG, String.valueOf(msg));
        /*Snackbar.make(mCoordinatorLayout, String.valueOf(msg), Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok, view -> {
                }).show();*/
    }

    public void close() {
        hideOptionsView();
    }

    public boolean isOpen() {
        return isShowingOptionsView;
    }

    public interface Listener {

        void onResolvePredictedOrigin(@NonNull Location location);

        void onResolvePredictedDestination(@NonNull Location location);

        void onResolveRetrievedLastLocation(String location);

        void onShowRoute(@NonNull Route routes, ShowRoutesListener showRoutesListener);

        void onShowMyLocation();
    }

    //Used to listen when {Lis}
    public interface ShowRoutesListener {
        void onFinish();
    }

}
