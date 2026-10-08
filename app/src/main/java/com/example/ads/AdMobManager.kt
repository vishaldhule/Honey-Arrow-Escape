package com.example.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Professional, player-friendly AdMob Manager for HONEY ARROW ESCAPE.
 *
 * Implements:
 * - Centralized Ad loading & lifecycle management.
 * - Strict reward verification (reward granted ONLY after AdMob confirms completion).
 * - Anti-abuse protection (exactly one reward per ad view).
 * - Graceful fallback & error handling (no crashes when offline or ad fails).
 * - Automatic preloading of next rewarded ad upon dismissal.
 * - Player-first frequency controls.
 */
object AdMobManager {

    private const val TAG = "AdMobManager"

    private var isInitialized = false
    private var rewardedAd: RewardedAd? = null
    private var interstitialAd: InterstitialAd? = null

    private var isRewardedLoading = false
    private var isInterstitialLoading = false

    private val _isRewardedReady = MutableStateFlow(false)
    val isRewardedReady: StateFlow<Boolean> = _isRewardedReady.asStateFlow()

    private val _adStatusMessage = MutableStateFlow<String?>(null)
    val adStatusMessage: StateFlow<String?> = _adStatusMessage.asStateFlow()

    private var lastInterstitialTimeMs: Long = 0
    private var levelsClearedSinceLastInterstitial: Int = 0

    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * Initializes the Mobile Ads SDK on app startup.
     */
    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            // Set emulator test device ID for development reliability
            val testDeviceIds = listOf(AdRequest.DEVICE_ID_EMULATOR)
            val requestConfig = RequestConfiguration.Builder()
                .setTestDeviceIds(testDeviceIds)
                .build()
            MobileAds.setRequestConfiguration(requestConfig)

            MobileAds.initialize(context.applicationContext) { initStatus ->
                Log.d(TAG, "AdMob MobileAds initialized: $initStatus")
                isInitialized = true
                loadRewardedAd(context.applicationContext)
                loadInterstitialAd(context.applicationContext)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing MobileAds SDK", e)
        }
    }

    /**
     * Preloads a Rewarded Ad in the background.
     */
    fun loadRewardedAd(context: Context) {
        if (isRewardedLoading || rewardedAd != null) {
            return
        }

        try {
            isRewardedLoading = true
            val adRequest = AdRequest.Builder().build()
            val adUnitId = AdMobConfig.rewardedAdUnitId

            Log.d(TAG, "Loading Rewarded Ad (Test=${AdMobConfig.USE_TEST_ADS}, Unit=$adUnitId)")

            RewardedAd.load(
                context.applicationContext,
                adUnitId,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        Log.d(TAG, "Rewarded ad successfully loaded")
                        rewardedAd = ad
                        isRewardedLoading = false
                        _isRewardedReady.value = true
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.w(TAG, "Rewarded ad failed to load: ${loadAdError.message} (code ${loadAdError.code})")
                        rewardedAd = null
                        isRewardedLoading = false
                        _isRewardedReady.value = false

                        // Schedule silent retry after 30 seconds
                        mainHandler.postDelayed({
                            loadRewardedAd(context.applicationContext)
                        }, 30_000L)
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception while requesting RewardedAd.load", e)
            isRewardedLoading = false
            _isRewardedReady.value = false
        }
    }

    /**
     * Shows a Rewarded Ad with strict reward callbacks.
     *
     * @param activity Foreground activity to present the ad
     * @param rewardType What the player chose to watch the ad for
     * @param onRewardEarned Called ONLY when AdMob confirms user earned the reward
     * @param onAdDismissed Called when the ad is closed (boolean indicating if reward was earned)
     * @param onAdUnavailable Called if the ad could not be shown or is not ready yet
     */
    fun showRewardedAd(
        activity: Activity,
        rewardType: RewardedAdRewardType,
        onRewardEarned: (RewardedAdRewardType) -> Unit,
        onAdDismissed: (earned: Boolean) -> Unit = {},
        onAdUnavailable: (message: String) -> Unit = {}
    ) {
        if (activity.isFinishing || activity.isDestroyed) {
            Log.w(TAG, "Activity is finishing or destroyed, cannot show ad")
            return
        }

        val currentAd = rewardedAd

        if (currentAd == null) {
            Log.w(TAG, "Rewarded ad requested but not ready yet")
            val message = "Reward ad isn't available right now. Please try again."
            _adStatusMessage.value = message
            onAdUnavailable(message)
            loadRewardedAd(activity.applicationContext)
            return
        }

        var rewardGranted = false

        currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Rewarded ad shown for: ${rewardType.name}")
                rewardedAd = null
                _isRewardedReady.value = false
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "Rewarded ad failed to show: ${adError.message}")
                rewardedAd = null
                _isRewardedReady.value = false
                val message = "Reward ad isn't available right now. Please try again."
                _adStatusMessage.value = message
                onAdUnavailable(message)
                loadRewardedAd(activity.applicationContext)
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Rewarded ad dismissed. Reward earned: $rewardGranted")
                onAdDismissed(rewardGranted)
                // Automatically preload next ad for seamless experience
                loadRewardedAd(activity.applicationContext)
            }
        }

        try {
            currentAd.show(activity) { rewardItem ->
                Log.d(TAG, "User completed ad! Amount: ${rewardItem.amount} ${rewardItem.type}")
                if (!rewardGranted) {
                    rewardGranted = true
                    onRewardEarned(rewardType)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during ad presentation", e)
            rewardedAd = null
            _isRewardedReady.value = false
            val message = "Reward ad isn't available right now. Please try again."
            _adStatusMessage.value = message
            onAdUnavailable(message)
            loadRewardedAd(activity.applicationContext)
        }
    }

    /**
     * Preloads an Interstitial Ad.
     */
    private fun loadInterstitialAd(context: Context) {
        if (isInterstitialLoading || interstitialAd != null) return

        isInterstitialLoading = true
        val adRequest = AdRequest.Builder().build()
        val adUnitId = AdMobConfig.interstitialAdUnitId

        InterstitialAd.load(
            context.applicationContext,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    isInterstitialLoading = false
                }
            }
        )
    }

    /**
     * Records a level completion and displays an interstitial ad ONLY if the strict
     * frequency cap conditions are met (never interrupting puzzle gameplay).
     */
    fun recordLevelClearedAndShowInterstitialIfAllowed(
        activity: Activity,
        onComplete: () -> Unit = {}
    ) {
        levelsClearedSinceLastInterstitial++
        val now = System.currentTimeMillis()
        val timeSinceLast = now - lastInterstitialTimeMs

        // Strictly respect player-first frequency caps
        if (levelsClearedSinceLastInterstitial >= AdMobConfig.LEVELS_BETWEEN_INTERSTITIALS &&
            timeSinceLast >= AdMobConfig.INTERSTITIAL_INTERVAL_MS &&
            interstitialAd != null
        ) {
            val ad = interstitialAd
            interstitialAd = null
            lastInterstitialTimeMs = now
            levelsClearedSinceLastInterstitial = 0

            ad?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    loadInterstitialAd(activity)
                    onComplete()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    loadInterstitialAd(activity)
                    onComplete()
                }
            }
            ad?.show(activity)
        } else {
            onComplete()
        }
    }

    fun clearStatusMessage() {
        _adStatusMessage.value = null
    }
}
