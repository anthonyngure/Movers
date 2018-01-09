package ke.co.thinksynergy.movers.activity;

import android.content.Intent;
import android.os.Bundle;

import ke.co.thinksynergy.movers.R;
import ke.co.thinksynergy.movers.utils.PrefUtils;


public class SplashActivity extends BaseActivity
{

    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (PrefUtils.getInstance().getUser() == null) {
            SignInActivity.start(this);
        } else {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        }

        SplashActivity.this.finish();

    }


    @Override
    public void startActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        super.startActivity(intent);
        overridePendingTransition(0, 0);
    }

}
