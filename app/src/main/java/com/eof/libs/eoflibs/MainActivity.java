package com.eof.libs.eoflibs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.eof.libs.base.debug.Log;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testLibBase();
    }

    private void testLibBase() {
        Log.d(TAG, "testLibBase()");
    }
}
