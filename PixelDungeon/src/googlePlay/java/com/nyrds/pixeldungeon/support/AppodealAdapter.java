package com.nyrds.pixeldungeon.support;

import android.os.Build;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.InterstitialCallbacks;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.appodeal.ads.utils.Log;
import com.nyrds.pixeldungeon.ml.BuildConfig;
import com.nyrds.pixeldungeon.ml.EventCollector;
import com.nyrds.pixeldungeon.ml.R;
import com.watabou.noosa.Game;
import com.watabou.noosa.InterstitialPoint;
import com.watabou.pixeldungeon.PixelDungeon;


/**
 * Created by mike on 18.02.2017.
 * This file is part of Remixed Pixel Dungeon.
 */

public class AppodealAdapter {
    private static InterstitialPoint returnTo;

    private static boolean isAllowed() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static void loadRewardVideo() {
        Game.instance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Appodeal.cache(PixelDungeon.instance(), Appodeal.REWARDED_VIDEO);
                Appodeal.setAutoCache(Appodeal.REWARDED_VIDEO, true);
            }
        });
    }


    public static void init(int adType) {

        if (Appodeal.isInitialized(adType)) {
            return;
        }

        String appKey = Game.getVar(R.string.appodealRewardAdUnitId);

        //vungle disable due to strange build issue
        //mopub, mobvista & tapjoy due audiences mismatch
        //ogury - intersiteal
        String disableNetworks[] = {"facebook", "flurry", "startapp", "vungle", "mopub", "mobvista", "tapjoy", "ogury"};

        for (String net : disableNetworks) {
            Appodeal.disableNetwork(PixelDungeon.instance(), net);
        }
        Appodeal.disableLocationPermissionCheck();


        if (BuildConfig.DEBUG) {
            Appodeal.setLogLevel(Log.LogLevel.verbose);
            Appodeal.setTesting(true);
        }


        Appodeal.initialize(PixelDungeon.instance(), appKey, adType, EuConsent.getConsentLevel() == EuConsent.PERSONALIZED);
    }


    public static void initIntersiteal() {
        if (!isAllowed()) {
            return;
        }

        Game.instance().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (Appodeal.isInitialized(Appodeal.INTERSTITIAL)) {
                    return;
                }

                init(Appodeal.INTERSTITIAL);
                Appodeal.setAutoCache(Appodeal.INTERSTITIAL, true);


                Appodeal.setInterstitialCallbacks(
                        new InterstitialCallbacks() {
                            @Override
                            public void onInterstitialLoaded(boolean b) {

                            }

                            @Override
                            public void onInterstitialFailedToLoad() {

                            }

                            @Override
                            public void onInterstitialShown() {

                            }

                            @Override
                            public void onInterstitialClicked() {

                            }

                            @Override
                            public void onInterstitialClosed() {
                                returnTo.returnToWork(true);
                            }

                            @Override
                            public void onInterstitialExpired() {

                            }
                        });
            }
        });


    }

    public static void initRewardedVideo() {

        if (!isAllowed()) {
            return;
        }

        Game.instance().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (Appodeal.isInitialized(Appodeal.REWARDED_VIDEO)) {
                    return;
                }

                init(Appodeal.REWARDED_VIDEO);
                Appodeal.setAutoCache(Appodeal.REWARDED_VIDEO, false);

                EventCollector.startTiming("appodeal reward video");
                Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {

                    @Override
                    public void onRewardedVideoLoaded(boolean b) {
                        EventCollector.stopTiming("appodeal reward video", "appodeal reward video", "ok", "");
                    }

                    @Override
                    public void onRewardedVideoFailedToLoad() {
                        EventCollector.stopTiming("appodeal reward video", "appodeal reward video", "fail", "");
                    }

                    @Override
                    public void onRewardedVideoShown() {
                    }

                    @Override
                    public void onRewardedVideoFinished(double v, String s) {

                    }

                    @Override
                    public void onRewardedVideoClosed(final boolean finished) {
                        returnTo.returnToWork(finished);
                    }

                    @Override
                    public void onRewardedVideoExpired() {

                    }
                });
            }
        });
    }

    public static void showCinemaRewardVideo(InterstitialPoint ret) {
        returnTo = ret;
        Game.instance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isVideoReady()) {
                    Appodeal.show(PixelDungeon.instance(), Appodeal.REWARDED_VIDEO);
                } else {
                    returnTo.returnToWork(false);
                }
            }
        });
    }

    public static void showIntersitial(InterstitialPoint ret) {
        returnTo = ret;
        Game.instance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
                    Appodeal.show(PixelDungeon.instance(), Appodeal.INTERSTITIAL);
                } else {
                    returnTo.returnToWork(false);
                }
            }
        });
    }


    public static boolean isVideoReady() {
        return isAllowed() && Appodeal.isLoaded(Appodeal.REWARDED_VIDEO);
    }
}
