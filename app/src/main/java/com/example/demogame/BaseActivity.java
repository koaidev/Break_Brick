package com.example.demogame;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    //double back to exit app
    boolean doubleBackToExitActivity = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitActivity) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitActivity = true;
        Toast.makeText(this, "Nhấn lần nữa để thoát.", Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitActivity = false;
            }
        }, 2000);
    }
    //end double back to exit




}
