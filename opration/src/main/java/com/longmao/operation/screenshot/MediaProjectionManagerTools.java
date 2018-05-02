package com.longmao.operation.screenshot;

import android.content.Intent;
import android.media.projection.MediaProjectionManager;

/**
 * Created by Carrot on 2018/4/19.
 */

public class MediaProjectionManagerTools {
    private static MediaProjectionManager sMediaProjectionManager;
    private static int sResult;
    private static Intent sIntent;

    public static void setMediaProjectionManager(MediaProjectionManager mediaProjectionManager) {
        sMediaProjectionManager = mediaProjectionManager;
    }

    public static MediaProjectionManager getMediaProjectionManager() {
        return sMediaProjectionManager;
    }

    public static void setResult(int result) {
        sResult = result;
    }

    public static int getResult() {
        return sResult;
    }

    public static void setData(Intent data) {
        sIntent = data;
    }

    public static Intent getData() {
        return sIntent;
    }
}
