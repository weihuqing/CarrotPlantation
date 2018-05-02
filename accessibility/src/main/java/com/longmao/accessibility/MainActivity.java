package com.longmao.accessibility;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.longmao.accessibility.util.AccessibilityUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AccessibilityUtil.checkAccessibilityEnabled(this.getApplicationContext(), "com.longmao.accessibility");
        AccessibilityUtil.goAccess(this.getApplicationContext());
    }
}
