// Copyright (c) 2018-2019 Frederic Rezeau
// Copyright (c) 2018-2019 Litemint LLC.
// Distributed under the MIT software license.
// https://github.com/litemint/litemint-android/blob/master/LICENSE.

package com.litemint.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }
}
