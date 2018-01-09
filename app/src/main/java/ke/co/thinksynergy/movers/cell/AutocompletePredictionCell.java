package ke.co.thinksynergy.movers.cell;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.jaychang.srv.SimpleCell;
import com.jaychang.srv.SimpleViewHolder;

import ke.co.thinksynergy.movers.R;
import ke.co.toshngure.basecode.view.SimpleListItemView;

/**
 * Created by Anthony Ngure on 04/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class AutocompletePredictionCell extends SimpleCell<AutocompletePrediction,
        AutocompletePredictionCell.AutocompletePredictionViewHolder> {

    public AutocompletePredictionCell(@NonNull AutocompletePrediction item) {
        super(item);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.cell_autocomplete_prediction;
    }

    @NonNull
    @Override
    protected AutocompletePredictionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, @NonNull View view) {
        return new AutocompletePredictionViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull AutocompletePredictionViewHolder autocompletePredictionViewHolder, int i, @NonNull Context context, Object o) {
        ((SimpleListItemView) autocompletePredictionViewHolder.itemView).setTitle(getItem()
                .getPrimaryText(null).toString());
        ((SimpleListItemView) autocompletePredictionViewHolder.itemView).setSubTitle(getItem()
                .getSecondaryText(null).toString());
    }

    class AutocompletePredictionViewHolder extends SimpleViewHolder {
        AutocompletePredictionViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
