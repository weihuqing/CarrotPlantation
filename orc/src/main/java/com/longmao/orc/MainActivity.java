package com.longmao.orc;

import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

public class MainActivity extends AppCompatActivity {
    private Bitmap bitmap;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            final ImageView imageView = findViewById(R.id.image);
            final TextView textView = findViewById(R.id.text);

            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test4);
            imageView.setImageBitmap(bitmap);

            new Thread() {
                @Override
                public void run() {
                    super.run();
                    TessBaseAPI tessBaseAPI = new TessBaseAPI();
                    tessBaseAPI.init("/sdcard/", "chi_sim");
                    tessBaseAPI.setImage(bitmap);
                    final String text = tessBaseAPI.getUTF8Text();
                    handler.post(new Runnable() {
                        @Override
                        public void run()
                        {
                            imageView.setImageBitmap(bitmap);
                            textView.setText(text);
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Instrumentation inst = new Instrumentation();
        inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 100, 200, 0));
    }
}
