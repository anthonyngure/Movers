package ke.co.thinksynergy.movers.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;

import com.github.pinball83.maskededittext.MaskedEditText;
import com.hanks.htextview.typer.TyperTextView;
import com.jaeger.library.StatusBarUtil;
import com.synnapps.carouselview.CarouselView;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ke.co.thinksynergy.movers.R;

public class SignInActivity extends BaseActivity {


    private static final String TAG = "SignInActivity";
    private static final int SLIDE_INTERVAL = 3000;
    @BindView(R.id.carouselView)
    CarouselView carouselView;
    @BindView(R.id.phoneMET)
    MaskedEditText phoneMET;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.signInWithFacebookBtn)
    TextView signInWithFacebookBtn;
    private Random random = new Random();

    @BindView(R.id.slideTV)
    TyperTextView slideTV;

    int[] moveWithOptions = new int[]{
            R.string.motor_bike,
            R.string.pick_up,
            R.string.small_truck,
            R.string.medium_truck,
            R.string.big_truck,
            R.string.trailer_20ft,
            R.string.trailer_40ft,
    };

    int[] moveWithOptionImages = new int[]{
            R.drawable.pick_up,
            R.drawable.pick_up,
            R.drawable.pick_up,
            R.drawable.pick_up,
            R.drawable.pick_up,
            R.drawable.pick_up,
            R.drawable.pick_up,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        StatusBarUtil.setTranslucent(this);

        carouselView.setPageCount(moveWithOptionImages.length);
        carouselView.setSlideInterval(SLIDE_INTERVAL);
        carouselView.setPageCount(moveWithOptionImages.length);
        carouselView.setImageListener((position, imageView) -> {
            imageView.setImageResource(moveWithOptionImages[position]);
        });


        carouselView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String text = getString(moveWithOptions[position]);
                slideTV.animateText("Transport with: "+text);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        slideTV.animateText("Transport with: "+getString(moveWithOptions[0]));
    }

    @Override
    protected void setUpStatusBarColor() {
        //super.setUpStatusBarColor();
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, SignInActivity.class);
        context.startActivity(starter);
    }

    @OnClick(R.id.fab)
    public void signInWithPhoneNumber(View view) {
        //toastDebug(phoneMET.getText().toString());
        String phoneNumber = phoneMET.getText().toString();
        if (!TextUtils.isEmpty(phoneNumber) && Patterns.PHONE.matcher(phoneNumber).matches()
                && (phoneNumber.length() == 13)) {
            startNewTaskActivity(new Intent(this, MainActivity.class));
        } else {
            Snackbar.make(view, R.string.error_phone_number, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, view1 -> {}).show();
        }
    }

    @OnClick(R.id.signInWithFacebookBtn)
    public void signInWithFacebookBtn() {
        startNewTaskActivity(new Intent(this, MainActivity.class));
    }
}
