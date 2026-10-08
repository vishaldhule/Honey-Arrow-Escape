package com.example.ads

/**
 * Centralized Google AdMob Configuration for HONEY ARROW ESCAPE.
 *
 * All Ad Unit IDs, test configurations, and ad behavior settings are stored
 * exclusively in this file. Never duplicate Ad Unit IDs across screens.
 */
object AdMobConfig {

    /**
     * Controls whether the game loads Google test ads or production ads.
     * Keep set to TRUE during development, testing, and debugging to comply
     * with Google AdMob policies (preventing invalid traffic penalties).
     *
     * Set to FALSE before official Play Store production release to enable
     * the production ad unit.
     */
    var USE_TEST_ADS: Boolean = true

    /**
     * Production Rewarded Ad Unit ID provided by developer.
     */
    const val PRODUCTION_REWARDED_AD_UNIT_ID = "ca-app-pub-4708647684942494/8048633481"

    /**
     * Official Google AdMob sample test ad unit for Rewarded Ads.
     * Guaranteed safe for automated tests and continuous development.
     */
    const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

    /**
     * Official Google AdMob sample test ad unit for Interstitial Ads.
     */
    const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    /**
     * Active Rewarded Ad Unit ID based on the test/production configuration.
     */
    val rewardedAdUnitId: String
        get() = if (USE_TEST_ADS) TEST_REWARDED_AD_UNIT_ID else PRODUCTION_REWARDED_AD_UNIT_ID

    /**
     * Active Interstitial Ad Unit ID based on the test/production configuration.
     */
    val interstitialAdUnitId: String
        get() = TEST_INTERSTITIAL_AD_UNIT_ID

    /**
     * Minimum interval (in milliseconds) between interstitial ads to protect
     * player experience and prevent ad fatigue.
     */
    const val INTERSTITIAL_INTERVAL_MS = 120_000L // 2 minutes minimum between interstitials

    /**
     * Number of levels cleared before a single non-intrusive interstitial may appear.
     */
    const val LEVELS_BETWEEN_INTERSTITIALS = 4

    /**
     * Maximum revives allowed per single level attempt to prevent infinite loops.
     */
    const val MAX_REVIVES_PER_LEVEL = 2

    /**
     * Extra time added upon revival or extra moves reward.
     */
    const val EXTRA_TIME_SECONDS = 30
}
