package ke.co.thinksynergy.movers.fragment;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ke.co.thinksynergy.movers.activity.BaseActivity;
import ke.co.thinksynergy.movers.model.User;
import ke.co.thinksynergy.movers.network.BackEnd;
import ke.co.thinksynergy.movers.utils.PrefUtils;
import ke.co.toshngure.basecode.fragment.BaseAppFragment;
import ke.co.toshngure.basecode.log.BeeLog;

/**
 * Created by Anthony Ngure on 11/06/2017.
 * Email : anthonyngure25@gmail.com.
 * Company : VibeCampo Social Network..
 */

public class BaseFragment extends BaseAppFragment {

    public static final String TAG = BaseFragment.class.getSimpleName();

    public BaseFragment() {
    }

    protected User getUser() {
        return PrefUtils.getInstance().getUser();
    }

    public void signOut() {
        ((BaseActivity) getActivity()).signOut();
    }


    protected void onSuccessResponse(JSONObject data, JSONObject meta) {

    }

    protected void onSuccessResponse(JSONArray data, JSONObject meta) {

    }

    protected void onErrorResponse(JSONObject meta){
    }

    @Override
    public void onConnectionSuccess(JSONObject response) {
        BeeLog.d(TAG, "onConnectionSuccess, Response = " + String.valueOf(response));
        hideProgressDialog();
        try {
            if (response.get(BackEnd.Response.DATA) instanceof JSONObject) {
                //Data is Object
                onSuccessResponse(response.getJSONObject(BackEnd.Response.DATA), response.getJSONObject(BackEnd.Response.META));
            } else {
                //Data is Array
                onSuccessResponse(response.getJSONArray(BackEnd.Response.DATA), response.getJSONObject(BackEnd.Response.META));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    protected boolean isLoggedIn() {
        return ((BaseActivity) getActivity()).isLoggedIn();
    }
}
