// Copyright (c) 2018-2019 Frederic Rezeau
// Copyright (c) 2018-2019 Litemint LLC.
// Distributed under the MIT software license.
// https://github.com/litemint/litemint-android/blob/master/LICENSE.

package com.litemint.app;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;

public class AppWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(request.getUrl().toString()));
        view.getContext().startActivity(intent);
        return true;
    }
}