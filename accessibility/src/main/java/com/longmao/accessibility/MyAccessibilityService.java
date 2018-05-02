package com.longmao.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by Carrot on 2018/4/23.
 */
public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "Accessibility";
    private AccessibilityHelper helper;
    private Handler handler = new Handler(Looper.getMainLooper());

    private AccessibilityEvent mEvent;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getPackageName().equals("com.android.packageinstaller") || event.getPackageName().equals("com.miui.packageinstaller")) {
            Log.i(TAG, "onAccessibilityEvent getEventType : " + event.getEventType());
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                AccessibilityNodeInfo nodeInfo = event.getSource();
                Log.i(TAG, "onAccessibilityEvent install : " + nodeInfo);
                if (nodeInfo != null) {
                    helper.clickTextViewByText(event.getSource(), "安装");
                }
            }
        } else if (event.getPackageName().equals("com.tencent.mm") && event.getClassName().equals("com.tencent.mm.ui.LauncherUI")) {
            Log.i(TAG, "onAccessibilityEvent find mm");
            helper.clickTextViewByText(event.getSource(), "跳一跳");
        } else if (event.getPackageName().equals("com.tencent.tim") && event.getClassName().equals("com.tencent.mobileqq.activity.SplashActivity")) {
            Log.i(TAG, "onAccessibilityEvent find tim");
            helper.clickTextViewByText(event.getSource(), "大美牛");
        } else if (event.getPackageName().equals("com.longmao.listview") && event.getClassName().equals("com.longmao.listview.MainActivity")) {
            Log.i(TAG, "onAccessibilityEvent find listview");
//            helper.performScrollBackward();
//            helper.performBackClick();
            helper.performScrollForward(event.getSource());
//            helper.findNodeInfoByText(event.getSource(), "item5");
        } else if (event.getPackageName().equals("net.csdn.csdnplus")) {
            if (event.getClassName().equals("net.csdn.csdnplus.activity.MainSlidingActivity")) {
                Log.i(TAG, "onAccessibilityEvent find csdn");
                helper.clickTextViewByText(event.getSource(), "Facebook");
            } else if (event.getClassName().equals("net.csdn.csdnplus.activity.BlogContentActivity")) {
                Log.i(TAG, "onAccessibilityEvent find content");
                helper.clickTextViewByText(event.getSource(), "加关注");
            }
        } else if (event.getPackageName().equals("com.eg.android.AlipayGphone")) {
            if (event.getClassName().equals("com.eg.android.AlipayGphone.AlipayLogin")) {
                Log.i(TAG, "onAccessibilityEvent find alipay");
                helper.clickTextViewByText(event.getSource(), "转账");
            } else if (event.getClassName().equals("com.alipay.mobile.transferapp.ui.TransferHomeActivity_")) {
                Log.i(TAG, "onAccessibilityEvent find alipay to");
                helper.clickTextViewByText(event.getSource(), "转给我的朋友");
            } else if (event.getClassName().equals("com.alipay.mobile.socialcontactsdk.contact.select.page.CombinedSelectActivity")) {
                Log.i(TAG, "onAccessibilityEvent find alipay to friend : " + Thread.currentThread().getName());
//                SystemClock.sleep(500);
//                helper.refresh(event.getSource());
//                event.getSource().recycle();
//                event.getSource().refresh();
//                SystemClock.sleep(5000);
//                helper.refresh(event.getSource());

                while (true) {
                    try {
                        AccessibilityNodeInfo root = event.getSource();
                        helper.refresh(root);
                        Log.i(TAG, "onAccessibilityEvent root : " + root.hashCode());

                        AccessibilityNodeInfo info = helper.findNodeInfoByText(root, "曹严严");
                        if (info != null) {
                            Log.i(TAG, "onAccessibilityEvent info id : " + info.getViewIdResourceName());
                            helper.clickTextViewByText(event.getSource(), "曹严严");
                            Log.i(TAG, "onAccessibilityEvent info id list : " + root.findAccessibilityNodeInfosByViewId("com.alipay.mobile.socialcontactsdk:id/select_list"));
                            return;
                        } else {
                            helper.performScrollForward(root.findAccessibilityNodeInfosByViewId("com.alipay.mobile.socialcontactsdk:id/select_list").get(0));
                            Thread.sleep(3000);
                        }
                    } catch (InterruptedException e) {

                    } catch (Exception e) {
                        Log.e(TAG, "Exception", e);
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        helper = new AccessibilityHelper(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i(TAG, "onServiceConnected");
//        AccessibilityServiceInfo accessibilityServiceInfo = getServiceInfo();
//        if (accessibilityServiceInfo == null)
//            accessibilityServiceInfo = new AccessibilityServiceInfo();
//        accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
//        accessibilityServiceInfo.flags |= AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
//        accessibilityServiceInfo.packageNames = new String[] { WeChatFragment.WECHAT_PACKAGENAME };
//        accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
//        accessibilityServiceInfo.notificationTimeout = 10;
//        setServiceInfo(accessibilityServiceInfo);
    }

    @Override
    public void onInterrupt() {
    }
}
