package com.penzart.associativity

import android.app.Application
import com.google.android.gms.ads.MobileAds

/**
 * Class of the Associativity application.
 *
 */
class AssociativityApplication : Application() {
    /**
     * Perform initialisation of the application.
     *
     * [MobileAds.initialize] method is called.
     *
     */
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(applicationContext)
    }
}
