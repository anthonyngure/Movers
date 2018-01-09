package ke.co.thinksynergy.movers.presenters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.Task;
import com.jaychang.srv.SimpleCell;
import com.jaychang.srv.SimpleRecyclerView;
import com.otaliastudios.autocomplete.AutocompletePresenter;

import java.util.ArrayList;

import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.cell.AutocompletePredictionCell;
import ke.co.toshngure.basecode.log.BeeLog;
import ke.co.toshngure.basecode.utils.BaseUtils;

/**
 * Created by Anthony Ngure on 04/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class AutocompletePredictionPresenter extends AutocompletePresenter<AutocompletePrediction>
        implements SimpleCell.OnCellClickListener<AutocompletePrediction> {

    private static final String TAG = "PlacesSuggestions";

    private AppCompatActivity mContext;
    private SimpleRecyclerView mSimpleRecyclerView;

    /**
     * Current results returned by this adapter.
     */
    private ArrayList<AutocompletePrediction> mResultList;

    /**
     * Handles autocomplete requests.
     */
    private GeoDataClient mGeoDataClient;


    /**
     * The autocomplete filter used to restrict queries to a specific set of place types.
     */
    private AutocompleteFilter mPlaceFilter;
    private ClickProvider<AutocompletePrediction> mClickProvider;

    public AutocompletePredictionPresenter(AppCompatActivity context) {
        super(context);
        this.mContext = context;
        mGeoDataClient = Places.getGeoDataClient(context, null);
        mPlaceFilter = new AutocompleteFilter.Builder()
                .setCountry("KE")
                //.setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();
    }

    @Override
    protected ViewGroup getView() {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.view_autocoplete_predictions, null);
        mSimpleRecyclerView = viewGroup.findViewById(R.id.autocompletePredictionsRV);
        return viewGroup;
    }

    @Override
    protected void onViewShown() {

    }

    @Override
    protected void onQuery(@Nullable CharSequence query) {
        BeeLog.i(TAG, "Starting autocomplete query for: " + query);
        // Query the autocomplete API for the (query) search string.
        Task<AutocompletePredictionBufferResponse> task =
                mGeoDataClient.getAutocompletePredictions(String.valueOf(query),
                        null, mPlaceFilter);
        task.addOnSuccessListener(autocompletePredictions -> {
            BeeLog.i(TAG, "Query completed. Received " + autocompletePredictions.getCount() + " predictions.");
            // Freeze the results immutable representation that can be stored safely.
            mSimpleRecyclerView.removeAllCells(true);
            mResultList = DataBufferUtils.freezeAndClose(autocompletePredictions);
            for (AutocompletePrediction prediction : mResultList) {
                AutocompletePredictionCell predictionCell = new AutocompletePredictionCell(prediction);
                predictionCell.setOnCellClickListener(this);
                mSimpleRecyclerView.addCell(predictionCell);
            }
            autocompletePredictions.release();
        });

    }

    @Override
    protected void onViewHidden() {

    }

    @Override
    public void onCellClicked(@NonNull AutocompletePrediction autocompletePrediction) {
        mClickProvider.click(autocompletePrediction);
    }

    @Override
    protected void registerClickProvider(ClickProvider<AutocompletePrediction> provider) {
        super.registerClickProvider(provider);
        this.mClickProvider = provider;
    }

    @Override
    protected PopupDimensions getPopupDimensions() {
        PopupDimensions dims = new PopupDimensions();
        dims.width = BaseUtils.getScreenWidth(mContext);
        dims.height = ViewGroup.LayoutParams.MATCH_PARENT;
        return dims;
    }
}
