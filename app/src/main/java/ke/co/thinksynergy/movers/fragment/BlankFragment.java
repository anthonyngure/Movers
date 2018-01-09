package ke.co.thinksynergy.movers.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ke.co.thinksynergy.movers.R;

/**
 * Created by Anthony Ngure on 08/11/2017.
 * Email : anthonyngure25@gmail.com.
 */

public class BlankFragment extends Fragment {

    public BlankFragment() {
    }

    public static BlankFragment newInstance() {

        Bundle args = new Bundle();

        BlankFragment fragment = new BlankFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }
}
