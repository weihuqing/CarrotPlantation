package com.longmao.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by Carrot on 2018/4/23.
 */

public class AccessibilityHelper {
    private static final String TAG = "Accessibility";

    private AccessibilityService service;

    private Context context;

    public AccessibilityHelper(AccessibilityService service) {
        this.service = service;
        this.context = service.getApplicationContext();
    }

    /**
     * 模拟点击事件
     *
     * @param nodeInfo nodeInfo
     */
    public void performViewClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }
        while (nodeInfo != null) {
            if (nodeInfo.isClickable()) {
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                break;
            }
            nodeInfo = nodeInfo.getParent();
        }
    }

    /**
     * 模拟下滑操作
     */
    public void performScrollBackward(AccessibilityNodeInfo info) {
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        Log.i(TAG, "performScrollBackward");
        info.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
    }

    /**
     * 模拟上滑操作
     */
    public void performScrollForward(AccessibilityNodeInfo info) {
        Log.i(TAG, "performScrollForward");
        info.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
    }

    /**
     * 模拟返回操作
     */
    public void performBackClick() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "performBackClick");
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    /**
     * 查找对应文本的View
     *
     * @param text text
     * @return View
     */
    public AccessibilityNodeInfo findViewByText(String text) {
        return findViewByText(text, false);
    }

    /**
     * 查找对应文本的View
     *
     * @param text      text
     * @param clickable 该View是否可以点击
     * @return View
     */
    public AccessibilityNodeInfo findViewByText(String text, boolean clickable) {
        AccessibilityNodeInfo accessibilityNodeInfo = service.getRootInActiveWindow();
        Log.i(TAG, "findViewByText : " + accessibilityNodeInfo.findAccessibilityNodeInfosByText(""));
        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByText(text);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null && (nodeInfo.isClickable() == clickable)) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    /**
     * 查找对应ID的View
     *
     * @param id id
     * @return View
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public AccessibilityNodeInfo findViewByID(String id) {
        AccessibilityNodeInfo accessibilityNodeInfo = service.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    return nodeInfo;
                }
            }
        }
        return null;
    }

    public void clickTextViewByText(AccessibilityNodeInfo info, String text) {
        Log.i(TAG, "clickTextViewByText accessibilityNodeInfo : " + info);
        if (info == null) {
            return;
        }
        List<AccessibilityNodeInfo> nodeInfoList = info.findAccessibilityNodeInfosByText(text);
        Log.i(TAG, "clickTextViewByText nodeInfoList : " + nodeInfoList);

        if (nodeInfoList != null && nodeInfoList.size() > 0) {
            performViewClick(nodeInfoList.get(0));
        }
    }

    public AccessibilityNodeInfo findNodeInfoByClass(AccessibilityNodeInfo info, String className) {
        Log.i(TAG, "clickTextViewByText findViewByClass : " + info);
        if (info.getClassName().equals(className)) {
            return info;
        }

        int count = info.getChildCount();
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo childInfo = findNodeInfoByClass(info.getChild(i), className);
            if (childInfo != null) {
                return childInfo;
            }
        }

        return null;
    }

    public AccessibilityNodeInfo findNodeInfoByText(AccessibilityNodeInfo info, String text) {
        Log.i(TAG, "clickTextViewByText findViewByText : " + info);
        List<AccessibilityNodeInfo> infolist = info.findAccessibilityNodeInfosByText(text);
        if (infolist != null && !infolist.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : infolist) {
//                if (nodeInfo != null && text.equals(nodeInfo.getText())) {
                if (nodeInfo != null) {
                    Log.e(TAG, "clickTextViewByText findViewByText match : " + nodeInfo);
                    return nodeInfo;
                }
            }
        }

        return null;
    }

    public void refresh(AccessibilityNodeInfo info) {
        Log.i(TAG,"refresh : " + info );
        if (info == null) {
            return;
        }

        int count = info.getChildCount();
        Log.i(TAG,"refresh child count : " + count );
        for (int i = 0; i < count; i++) {
            refresh(info.getChild(i));
        }

        info.refresh();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void clickTextViewByID(String id) {
        AccessibilityNodeInfo accessibilityNodeInfo = service.getRootInActiveWindow();
        if (accessibilityNodeInfo == null) {
            return;
        }
        List<AccessibilityNodeInfo> nodeInfoList = accessibilityNodeInfo.findAccessibilityNodeInfosByViewId(id);
        if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if (nodeInfo != null) {
                    performViewClick(nodeInfo);
                    break;
                }
            }
        }
    }

    /**
     * 模拟输入
     *
     * @param nodeInfo nodeInfo
     * @param text     text
     */
    public void inputText(AccessibilityNodeInfo nodeInfo, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", text);
            clipboard.setPrimaryClip(clip);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }
    }
}
