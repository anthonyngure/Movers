package ke.co.thinksynergy.movers.activity.utils;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import ke.co.thinksynergy.movers.R;

/**
 * Created by Anthony Ngure on 06/01/2018.
 * Email : anthonyngure25@gmail.com.
 */

public class DrawerManager {

    private AppCompatActivity mContext;
    private Drawer mDrawer;

    private DrawerManager(AppCompatActivity activity, Toolbar toolbar) {
        this.mContext = activity;
        init(toolbar);
    }

    public static DrawerManager init(AppCompatActivity activity, Toolbar toolbar){
        return new DrawerManager(activity, toolbar);
    }

    private void init(Toolbar toolbar) {
//User user = getUser();

        AccountHeaderBuilder accountHeaderBuilder = new AccountHeaderBuilder()
                .withCloseDrawerOnProfileListClick(false)
                .withNameTypeface(Typeface.DEFAULT_BOLD)
                .withHeaderBackground(R.drawable.header)
                .withTextColor(Color.WHITE)
                .withActivity(mContext)
                .withThreeSmallProfileImages(true)
                .withAlternativeProfileHeaderSwitching(false)
                .withProfileImagesClickable(false)
                .withSelectionListEnabled(false)
                .withCloseDrawerOnProfileListClick(false);

        //*Add user*//*
        accountHeaderBuilder.addProfiles(new ProfileDrawerItem()
                .withName(mContext.getString(R.string.app_name))
                .withIcon(R.drawable.profile)
                .withTextColorRes(R.color.colorAccent)
                .withNameShown(true)
                .withEmail(mContext.getString(R.string.ask_for_transport)));

        mDrawer = new DrawerBuilder().withActivity(mContext)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(true)
                .withAccountHeader(accountHeaderBuilder.build())
                .withHeaderPadding(true)
                .withCloseOnClick(false)
                .withActionBarDrawerToggle(true)
                //.withShowDrawerUntilDraggedOpened(true)
                .build();

        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mDrawerManager.getHeader().setPadding(0, 24, 0, 0);
        }*/
    }

    public Drawer getDrawer() {
        return mDrawer;
    }
}
