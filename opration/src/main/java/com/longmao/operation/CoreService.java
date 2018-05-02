package com.longmao.operation;

import android.app.Instrumentation;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.longmao.operation.screenshot.MediaProjectionManagerTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Carrot on 2018/4/18.
 */

public class CoreService extends Service {
    private String parseString;

    private Bitmap bitmap;

    private Instrumentation inst;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i("operation", "handler msg : " + msg.what);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("operation", "CoreService onStartCommand");
//        initView(getApplicationContext());
        operation();
        inst = new Instrumentation();
        return super.onStartCommand(intent, flags, startId);
    }

    private void operation() {
        MediaProjectionManager mediaProjectionManager = MediaProjectionManagerTools.getMediaProjectionManager();
        if (mediaProjectionManager == null) {
            return;
        }

        MediaProjection mediaProjection = mediaProjectionManager.getMediaProjection(MediaProjectionManagerTools.getResult(), MediaProjectionManagerTools.getData());

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int windowWidth = metrics.widthPixels;
        int windowHeight = metrics.heightPixels;

        Log.i("operation", "CoreService windowWidth : " + windowWidth);
        Log.i("operation", "CoreService windowHeight : " + windowHeight);

        ImageReader imageReader = ImageReader.newInstance(windowWidth, windowHeight, 0x1, 2);

        VirtualDisplay virtualDisplay = mediaProjection.createVirtualDisplay("ScreenCapture",
                windowWidth, windowHeight, metrics.densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(), new VirtualDisplay.Callback() {
                    @Override
                    public void onPaused() {
                        Log.i("operation", "virtualDisplay callback onPaused");
                        super.onPaused();
                    }

                    @Override
                    public void onResumed() {
                        Log.i("operation", "virtualDisplay callback onResumed");
                        super.onResumed();
                    }

                    @Override
                    public void onStopped() {
                        Log.i("operation", "virtualDisplay callback onStopped");
                        super.onStopped();
                    }
                }, handler);

        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(final ImageReader reader) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        SystemClock.sleep(5000);
                        Image image = reader.acquireLatestImage();
                        int width = image.getWidth();
                        int height = image.getHeight();
                        Image.Plane[] planes = image.getPlanes();

                        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        bitmap.copyPixelsFromBuffer(planes[0].getBuffer());
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
                        image.close();
                        Log.i("operation", "bitmap : " + bitmap.getByteCount());

                        if (bitmap != null) {
                            try {
                                String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
                                Log.i("operation", "dir : " + dir);
                                String path = dir + System.currentTimeMillis() + ".png";
                                File fileImage = new File(path);
                                if (!fileImage.exists()) {
                                    fileImage.createNewFile();
                                    Log.i("operation", "image file created");
                                }
                                FileOutputStream out = new FileOutputStream(fileImage);
                                if (out != null) {
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                                    out.flush();
                                    out.close();
                                    Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    Uri contentUri = Uri.fromFile(fileImage);
                                    media.setData(contentUri);
                                    CoreService.this.sendBroadcast(media);
                                    Log.i("operation", "screen image saved");
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
//                            parseString = parseImage(bitmap);
//                            Log.i("operation", "parseString : " + parseString);
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(CoreService.this, parseString, Toast.LENGTH_LONG).show();
//                                }
//                            });
                        }

                        reader.close();
                        Log.i("operation", "drag");
                        drag(360f, 360f, 300f, 500f, 100);
                    }
                }.start();
            }
        }, handler);
    }

    private String parseImage(Bitmap bitmap) {
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init("/sdcard/", "chi_sim");
        tessBaseAPI.setImage(bitmap);
        return tessBaseAPI.getUTF8Text();
    }

    private void drag(float fromX, float toX, float fromY, float toY,
                      int stepCount) {
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();
        float y = fromY;
        float x = fromX;
        float yStep = (toY - fromY) / stepCount;
        float xStep = (toX - fromX) / stepCount;
        MotionEvent event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, fromX, fromY, 0);
        try {
            inst.sendPointerSync(event);
        } catch (SecurityException ignored) {
        }
        for (int i = 0; i < stepCount; ++i) {
            y += yStep;
            x += xStep;
            eventTime = SystemClock.uptimeMillis();
            event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_MOVE, x, y, 0);
            try {
                inst.sendPointerSync(event);
            } catch (SecurityException ignored) {
            }
        }
        eventTime = SystemClock.uptimeMillis();
        event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, toX, toY, 0);
        try {
            inst.sendPointerSync(event);
        } catch (SecurityException ignored) {
        }
    }
}
