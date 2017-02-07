package com.vip.camerademo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * {@doc}
 * <p/>
 * Created by Yin Yong on 17/2/6.
 */
public class Entrance extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);
    }

    public void sysIntent(View view) {
        startActivity(new Intent(this, UseSysIntentDemo.class));
    }

    public void cameraApi(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
