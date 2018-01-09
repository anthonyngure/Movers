/*
 * Copyright (c) 2017. Laysan Incorporation
 * Website http://laysan.co.ke
 * Tel +254723203475/+254706356815
 */

package ke.co.thinksynergy.movers.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import ke.co.thinksynergy.movers.R;


public class FragmentActivity extends BaseActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_SUB_TITLE = "extra_sub_title";
    private static Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getStringExtra(EXTRA_TITLE) == null){
            throw new IllegalArgumentException("Title can not be null!");
        }

        setTitle(getIntent().getStringExtra(EXTRA_TITLE));

        if (getIntent().getStringExtra(EXTRA_SUB_TITLE) != null){
            getSupportActionBar().setSubtitle(getIntent().getStringExtra(EXTRA_SUB_TITLE));
        }


        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentsContainer, mFragment,
                        mFragment.getClass().getSimpleName())
                .commit();

    }

    public void addFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentsContainer, fragment,
                        fragment.getClass().getSimpleName())
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
    }

    public void addFragment(Fragment fragment, String title) {
        setTitle(title);
        addFragment(fragment);
    }

    public static void start(Context context, Fragment fragment, String title, @Nullable String subTitle) {
        Intent starter = new Intent(context, FragmentActivity.class);
        starter.putExtra(EXTRA_TITLE, title);
        starter.putExtra(EXTRA_SUB_TITLE, subTitle);
        mFragment = fragment;
        context.startActivity(starter);
    }

    public static void start(Context context, Fragment fragment, String title) {
        start(context, fragment, title, null);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }
}
