// Copyright (c) 2018-2019 Frederic Rezeau
// Copyright (c) 2018-2019 Litemint LLC.
// Distributed under the MIT software license.
// https://github.com/litemint/litemint-android/blob/master/LICENSE.

package com.litemint.app;

import android.content.Context;
import android.webkit.WebView;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Handler;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.view.View;
import android.animation.AnimatorListenerAdapter;
import android.animation.Animator;
import android.net.Uri;
import android.content.ActivityNotFoundException;

public class WebAppInterface {
    WebView mView;
    int mTitleBarHeight;
    Handler mHandler;
    MainActivity mActivity;
    WebAppInterface(MainActivity context, WebView view, int titleBarHeight, Handler handler) {
        mView = view;
        mActivity = context;
        mTitleBarHeight = titleBarHeight;
        mHandler = handler;
    }

    /** set webpage ready */
    @JavascriptInterface
    public void setReady() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mView.animate().setDuration(200).alpha(1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    /** rate the app */
    @JavascriptInterface
    public void rate() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Uri uri = Uri.parse("market://details?id=" + mActivity.getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    mActivity.startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + mActivity.getPackageName())));
                }
            }
        });
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mActivity, toast, Toast.LENGTH_SHORT).show();
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void share(String code, String issuer, String deposit, String title) {
        final String fCode = code;
        final String fIssuer = issuer;
        final String fDeposit = deposit;
        final String fTitle = title;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = fCode + "\n" + fIssuer + "\n" + fDeposit;
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                mActivity.startActivity(Intent.createChooser(sharingIntent, fTitle));
            }
        });
    }

    /** Show a notification */
    @JavascriptInterface
    public void showNotification(String message) {
        final String sMessage = message;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mActivity.showNotification(sMessage);
            }
        });
    }

    /** Get title bar height */
    @JavascriptInterface
    public void getBarHeight(){
        final String height = "\"" + Integer.toString(mTitleBarHeight) + "\"";
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mView.evaluateJavascript("javascript:onBarHeightChange(" + height + ");", null);
            }
        });
    }

    /** Copy to clipboard */
    @JavascriptInterface
    public void copyToClipboard(String label, String data, String confirm){
        final String localLabel = label;
        final String localData = data;
        final String localConfirm = confirm;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(localLabel, localData);
                clipboard.setPrimaryClip(clip);
                if(localConfirm != ""){
                    Toast.makeText(mActivity, localConfirm, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /** Retrieve clipboard data */
    @JavascriptInterface
    public void retrieveClipboardData(){
        ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = clipboard.getPrimaryClip();
        ClipData.Item item = clipData.getItemAt(0);
        final String text = "\"" + item.getText().toString() + "\"";
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mView.evaluateJavascript("javascript:onRetrieveClipboardData(" + text + ");", null);
            }
        });
    }

    /** Scan QR Code */
    @JavascriptInterface
    public void scanQRCode(){
        final String height = "\"" + Integer.toString(mTitleBarHeight) + "\"";
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mActivity.scanQRCode();
            }
        });
    }

    /** Store item */
    @JavascriptInterface
    public void setStorageItem(String item, String value){
        SharedPreferences.Editor editor = mActivity.getSharedPreferences("app_data", Context.MODE_PRIVATE).edit();
        editor.putString(item, value);
        editor.apply();

        getStorageItem(item);
    }

    /** Retrieve item */
    @JavascriptInterface
    public void getStorageItem(String item){
        SharedPreferences prefs = mActivity.getSharedPreferences("app_data", Context.MODE_PRIVATE);
        String retrievedItem = prefs.getString(item, null);

        if(retrievedItem != null){
            mView.evaluateJavascript("javascript:onStorageItem(\"" + item + "\", \"" + retrievedItem + "\");", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                }
            });
        }
    }
}