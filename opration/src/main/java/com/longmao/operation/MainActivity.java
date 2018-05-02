package com.longmao.operation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.longmao.operation.screenshot.MediaProjectionManagerTools;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_MEDIA_PROJECTION = 1;
    private MediaProjectionManagerTools mediaProjectionManagerTools;
    private MediaProjectionManager mediaProjectionManager;
    private int count = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.SYSTEM_ALERT_WINDOW
        };

        String[] PERMISSIONS_WINDOW = {
                Manifest.permission.SYSTEM_ALERT_WINDOW
        };

        if (Build.VERSION.SDK_INT >= 23) {
            boolean flag = true;
            for (String p : PERMISSIONS_STORAGE) {
                if (ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                    flag = false;
                    break;
                }
            }

            if (!flag) {
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, 1);
            }


        }

        mediaProjectionManager = (MediaProjectionManager)
                getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (data != null && resultCode != 0) {
                Log.i("operation", "onActivityResult : " + resultCode);
                mediaProjectionManagerTools.setMediaProjectionManager(mediaProjectionManager);
                mediaProjectionManagerTools.setData(data);
                mediaProjectionManagerTools.setResult(resultCode);
                startService(new Intent(this, CoreService.class));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
