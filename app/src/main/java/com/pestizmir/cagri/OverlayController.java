package com.pestizmir.cagri;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;

import android.content.Intent;

public class OverlayController {

    private static View overlayView;
    private static WebView overlayWeb;
    private static WindowManager windowManager;
    private static TelephonyManager telephonyManager;
    private static PhoneStateListener phoneListener;
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public static void show(final Context context, final String number) {
        handler.post(new Runnable() {
            @Override public void run() { showInternal(context, number); }
        });
    }

    private static void showInternal(Context appContext, String number) {
        hideInternal();

        if (!Settings.canDrawOverlays(appContext)) {
            return; // overlay izni yoksa gösteremeyiz
        }

        // WebView'in düzgün render etmesi için temalı bir context.
        Context ctx = new ContextThemeWrapper(appContext, android.R.style.Theme_DeviceDefault_Light);

        windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);

        FrameLayout root = new FrameLayout(ctx);

        overlayWeb = new WebView(ctx);
        WebSettings s = overlayWeb.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        overlayWeb.setBackgroundColor(Color.parseColor("#0f1115"));
        overlayWeb.setWebViewClient(new LinkClient(appContext));

        String safe = (number == null) ? "" : number;
        String url = Config.BASE_URL + "?phone=" + Uri.encode(safe);
        overlayWeb.loadUrl(url);

        FrameLayout.LayoutParams webLp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        root.addView(overlayWeb, webLp);

        Button close = new Button(ctx);
        close.setText("\u2715"); // ✕
        close.setAllCaps(false);
        close.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { hide(); }
        });
        FrameLayout.LayoutParams btnLp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP | Gravity.END);
        root.addView(close, btnLp);

        int height = (int) (appContext.getResources().getDisplayMetrics().heightPixels * 0.80);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                height,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP;

        overlayView = root;
        try {
            windowManager.addView(overlayView, params);
        } catch (Exception e) {
            overlayView = null;
            overlayWeb = null;
            return;
        }

        registerCallEndListener(appContext);
        handler.postDelayed(new Runnable() {
            @Override public void run() { hide(); }
        }, 120000); // güvenlik: 2 dk sonra kapan
    }

    private static void registerCallEndListener(Context context) {
        try {
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            phoneListener = new PhoneStateListener() {
                @Override public void onCallStateChanged(int state, String phoneNumber) {
                    if (state == TelephonyManager.CALL_STATE_IDLE) {
                        hide();
                    }
                }
            };
            telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        } catch (Exception ignored) {}
    }

    public static void hide() {
        handler.post(new Runnable() {
            @Override public void run() { hideInternal(); }
        });
    }

    private static void hideInternal() {
        try {
            if (telephonyManager != null && phoneListener != null) {
                telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
            }
        } catch (Exception ignored) {}
        phoneListener = null;

        if (overlayView != null && windowManager != null) {
            try { windowManager.removeView(overlayView); } catch (Exception ignored) {}
        }
        if (overlayWeb != null) {
            try { overlayWeb.destroy(); } catch (Exception ignored) {}
        }
        overlayView = null;
        overlayWeb = null;
    }

    // http dışı linkleri (tel: gibi) sistem uygulamasına yönlendirir.
    static class LinkClient extends WebViewClient {
        private final Context app;
        LinkClient(Context app) { this.app = app; }
        @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String u = request.getUrl().toString();
            if (u.startsWith("http")) return false;
            try {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(u));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                app.startActivity(i);
            } catch (Exception ignored) {}
            return true;
        }
    }
}
