package ke.co.thinksynergy.movers.activity.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.otaliastudios.autocomplete.Autocomplete;
import com.otaliastudios.autocomplete.AutocompleteCallback;
import com.otaliastudios.autocomplete.AutocompletePolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.presenters.AutocompletePredictionPresenter;
import ke.co.thinksynergy.movers.utils.PrefUtils;
import ke.co.thinksynergy.movers.view.RoundedView;
import ke.co.toshngure.basecode.utils.BaseUtils;

/**
 * Created by Anthony Ngure on 06/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class OriginDestinationInputManager {

    private final AppCompatActivity mContext;
    @BindView(R.id.destinationET)
    EditText mDestinationET;
    @BindView(R.id.originRV)
    RoundedView mOriginRV;
    @BindView(R.id.destinationRV)
    RoundedView mDestinationRV;
    @BindView(R.id.originET)
    EditText mOriginET;
    private BottomSheetManager mBottomSheetManager;
    private String mLastUsedOriginLocationName;
    private String mRetrievedLastLocationName;

    private Autocomplete<AutocompletePrediction> mOriginAutocomplete;
    private Autocomplete<AutocompletePrediction> mDestinationAutocomplete;

    private OriginDestinationInputManager(AppCompatActivity activity, BottomSheetManager bottomSheetManager) {
        this.mContext = activity;
        this.mBottomSheetManager = bottomSheetManager;
        ButterKnife.bind(this, activity);
        initForOriginET();
        initForDestinationET();
    }

    public static OriginDestinationInputManager init(AppCompatActivity activity, BottomSheetManager bottomSheetManager) {
        return new OriginDestinationInputManager(activity, bottomSheetManager);
    }

    private void initForOriginET() {

        mLastUsedOriginLocationName = PrefUtils.getInstance().getString(R.string.pref_last_used_origin_location_name);

        BaseUtils.cacheInput(mOriginET, R.string.pref_last_used_origin_location_name, PrefUtils.getInstance());

        //Since the last used origin location name will be inserted in the

        AutocompleteCallback<AutocompletePrediction> originCallback = new AutocompleteCallback<AutocompletePrediction>() {
            @Override
            public boolean onPopupItemClicked(Editable editable, AutocompletePrediction item) {
                editable.clear();
                editable.append(item.getPrimaryText(new StyleSpan(Typeface.BOLD)));
                mOriginET.setSelection(0);
                mBottomSheetManager.resolvePredictedOrigin(item);
                return true;
            }

            public void onPopupVisibilityChanged(boolean shown) {
                if (!shown){
                    hideKeyboard();
                }
            }
        };

        AutocompletePolicy originAutocompletePolicy = new AutocompletePolicy() {
            @Override
            public boolean shouldShowPopup(Spannable text, int cursorPos) {
                return !text.toString().equalsIgnoreCase(mLastUsedOriginLocationName) &&
                        !text.toString().equalsIgnoreCase(mRetrievedLastLocationName);
            }

            @Override
            public boolean shouldDismissPopup(Spannable text, int cursorPos) {
                return text.toString().equalsIgnoreCase(mLastUsedOriginLocationName)
                        || text.toString().equalsIgnoreCase(mRetrievedLastLocationName);
            }

            @Override
            public CharSequence getQuery(Spannable text) {
                return text;
            }

            @Override
            public void onDismiss(Spannable text) {
            }
        };

        mOriginAutocomplete = Autocomplete.<AutocompletePrediction>on(mOriginET).with(6f)
                .with(new ColorDrawable(Color.WHITE))
                .with(originAutocompletePolicy)
                .with(new AutocompletePredictionPresenter(mContext))
                .with(originCallback).build();
    }

    private void hideKeyboard() {
        View view = mContext.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void initForDestinationET() {

        mDestinationRV.setChecked(true);

        AutocompleteCallback<AutocompletePrediction> destinationCallback = new AutocompleteCallback<AutocompletePrediction>() {
            @Override
            public boolean onPopupItemClicked(Editable editable, AutocompletePrediction item) {
                editable.clear();
                editable.append(item.getPrimaryText(new StyleSpan(Typeface.BOLD)));
                mDestinationET.setSelection(0);
                mBottomSheetManager.resolvePredictedDestination(item);
                return true;
            }

            public void onPopupVisibilityChanged(boolean shown) {
                if (!shown){
                    hideKeyboard();
                }
            }
        };
        mDestinationAutocomplete = Autocomplete.<AutocompletePrediction>on(mDestinationET).with(6f)
                .with(new ColorDrawable(Color.WHITE))
                .with(new AutocompletePredictionPresenter(mContext))
                .with(destinationCallback).build();
    }

    public void onResolveRetrievedLastLocation(String retrievedLastLocationName) {
        mRetrievedLastLocationName = retrievedLastLocationName;
        mOriginET.setText(mRetrievedLastLocationName);
        mOriginET.setSelection(0);
    }

    public void close() {
        mOriginAutocomplete.dismissPopup();
        mDestinationAutocomplete.dismissPopup();
    }

    public boolean isOpen() {
        return mOriginAutocomplete.isPopupShowing() || mDestinationAutocomplete.isPopupShowing();
    }
}
