package ke.co.thinksynergy.movers.activity.utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.directions.route.Route;
import com.jaychang.srv.SimpleCell;
import com.jaychang.srv.SimpleRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.cell.TransportOptionCell;
import ke.co.thinksynergy.movers.model.TransportOption;
import ke.co.toshngure.basecode.utils.BaseUtils;

/**
 * Created by Anthony Ngure on 09/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class BottomSheetOptionsManager implements SimpleCell.OnCellClickListener<TransportOption> {

    @BindView(R.id.optionsRV)
    SimpleRecyclerView mOptionsRV;
    @BindView(R.id.confirmBtn)
    Button mConfirmBtn;
    @BindView(R.id.distanceTimeTV)
    TextView mDistanceTimeTV;
    List<TransportOption> mTransportOptions = new ArrayList<>();

    private AppCompatActivity mContext;

    private BottomSheetOptionsManager(AppCompatActivity activity) {
        this.mContext = activity;
        ButterKnife.bind(this, activity);
    }

    static BottomSheetOptionsManager init(AppCompatActivity activity) {
        return new BottomSheetOptionsManager(activity);
    }

    @SuppressLint("StaticFieldLeak")
    void update(Route route) {
        mTransportOptions.clear();
        mDistanceTimeTV.setText(mContext.getString(R.string.distance_time,
                route.getDistanceText(), route.getDurationText()));
        //Connect to the api to get options
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                InputStream is;
                byte[] buffer = new byte[0];
                try {
                    is = mContext.getAssets().open("test_transport_options.json");
                    int size = is.available();
                    buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String questionJsonString = new String(buffer);
                try {
                    JSONArray optionsJsonArray = new JSONArray(questionJsonString);
                    for (int i = 0; i < optionsJsonArray.length(); i++) {
                        JSONObject questionJsonObject = optionsJsonArray.getJSONObject(i);
                        TransportOption option = BaseUtils.getSafeGson().fromJson(questionJsonObject.toString(), TransportOption.class);
                        mTransportOptions.add(option);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (mTransportOptions.size() > 0){
                    //Select option one by default
                    onCellClicked(mTransportOptions.get(0));
                }
            }

        }.execute();
    }

    @Override
    public void onCellClicked(@NonNull TransportOption transportOption) {
        mConfirmBtn.setEnabled(false);

        List<TransportOptionCell> cells = new ArrayList<>();
        for (TransportOption option : mTransportOptions) {
            option.setSelected(option.getName().equalsIgnoreCase(transportOption.getName()));
            TransportOptionCell cell = new TransportOptionCell(option);
            cell.setOnCellClickListener(this);
            cells.add(cell);
        }
        mOptionsRV.removeAllCells();
        mOptionsRV.addCells(cells);

        mConfirmBtn.setEnabled(true);
        mConfirmBtn.setText(mContext.getString(R.string.confirm, transportOption.getName()));

    }
}
