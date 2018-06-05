package com.example.util;

import android.content.Context;

import com.app.yellowpages.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class PopUpAds {

    public static void ShowInterstitialAds(Context context) {

        Constant.AD_COUNT += 1;
        if (Constant.AD_COUNT == Constant.AD_COUNT_SHOW) {
            final InterstitialAd mInterstitial = new InterstitialAd(context);
            mInterstitial.setAdUnitId(context.getResources().getString(R.string.admob_interstitial_id));
            mInterstitial.loadAd(new AdRequest.Builder().build());
            mInterstitial.show();
            Constant.AD_COUNT = 0;
            if (!mInterstitial.isLoaded()) {
                AdRequest adRequest1 = new AdRequest.Builder()
                        .build();
                mInterstitial.loadAd(adRequest1);
            }
            mInterstitial.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mInterstitial.show();
                }
            });
        }
    }
}
