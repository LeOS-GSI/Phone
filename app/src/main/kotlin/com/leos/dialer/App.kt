package com.leos.dialer

import android.app.Application
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.PurchaseInfo
import com.leos.commons.extensions.checkUseEnglish
import com.leos.commons.extensions.toast

class App : Application() {

    lateinit var billingProcessor: BillingProcessor

    override fun onCreate() {
        super.onCreate()
        instance = this
        checkUseEnglish()

        // automatically restores purchases
        billingProcessor = BillingProcessor(
            this, BuildConfig.GOOGLE_PLAY_LICENSING_KEY,
            object : BillingProcessor.IBillingHandler {
                override fun onProductPurchased(productId: String, details: PurchaseInfo?) {}

                override fun onPurchaseHistoryRestored() {
                }

                override fun onBillingError(errorCode: Int, error: Throwable?) {}

                override fun onBillingInitialized() {}
            })
    }

    override fun onTerminate() {
        super.onTerminate()
        billingProcessor.release()
    }

    companion object {
        private var instance: App? = null

        fun isProVersion(): Boolean {
            return instance!!.billingProcessor.isPurchased(BuildConfig.PRODUCT_ID_X1)
                || instance!!.billingProcessor.isPurchased(BuildConfig.PRODUCT_ID_X2)
                || instance!!.billingProcessor.isPurchased(BuildConfig.PRODUCT_ID_X3)
        }
    }
}
