package ke.co.thinksynergy.movers.cell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaychang.srv.SimpleCell;
import com.jaychang.srv.SimpleViewHolder;
import com.jaychang.srv.Updatable;

import butterknife.BindView;
import butterknife.ButterKnife;
import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.model.TransportOption;
import ke.co.thinksynergy.movers.utils.Spanny;
import ke.co.toshngure.basecode.images.NetworkImage;

/**
 * Created by Anthony Ngure on 04/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class TransportOptionCell extends SimpleCell<TransportOption, TransportOptionCell.TransportOptionViewHolder>
implements Updatable<TransportOption>{

    public TransportOptionCell(@NonNull TransportOption item) {
        super(item);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.cell_transport_option;
    }

    @NonNull
    @Override
    protected TransportOptionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, @NonNull View view) {
        return new TransportOptionViewHolder(view);
    }



    @Override
    protected void onBindViewHolder(@NonNull TransportOptionViewHolder transportOptionViewHolder, int i, @NonNull Context context, Object o) {
        transportOptionViewHolder.bind(getItem());
    }

    @Override
    public boolean areContentsTheSame(@NonNull TransportOption transportOption) {
        return getItem().isSelected() && transportOption.isSelected();
    }

    @Override
    public Object getChangePayload(@NonNull TransportOption transportOption) {
        return transportOption;
    }

    static class TransportOptionViewHolder extends SimpleViewHolder {

        @BindView(R.id.avatarIV)
        NetworkImage avatarIV;
        @BindView(R.id.nameTV)
        TextView nameTV;
        @BindView(R.id.weightTV)
        TextView weightTV;
        @BindView(R.id.volumeTV)
        TextView volumeTV;
        @BindView(R.id.costTV)
        TextView costTV;
        @BindView(R.id.checkedIV)
        ImageView checkedIV;
        private TransportOption option;

        TransportOptionViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        void bind(TransportOption item) {
            this.option = item;
            nameTV.setText(item.getName());
            costTV.setText("KES " + String.format("%,.2f", item.getCost()));
            Spanny weightSpanny = new Spanny("MW ")
                    .append(String.format("%,.2f", item.getMaxWight())).append("Kg");
            weightTV.setText(weightSpanny);
            Spanny volumeSpanny = new Spanny("MV ")
                    .append(String.format("%,.2f", item.getMaxVolume()))
                    .append(" m").append("3", new SuperscriptSpan(), new AbsoluteSizeSpan(10, false));
            volumeTV.setText(volumeSpanny);
            checkedIV.setVisibility(item.isSelected() ? View.VISIBLE : View.GONE);
        }
    }
}
