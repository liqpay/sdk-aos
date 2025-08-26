package ua.privatbank.liqpay.sample

import android.app.Application
import kotlinx.coroutines.DEBUG_PROPERTY_NAME
import kotlinx.coroutines.DEBUG_PROPERTY_VALUE_ON
import ua.privatbank.liqpay.LiqPay

class SampleApp : Application() {

    init {
        System.setProperty(DEBUG_PROPERTY_NAME, DEBUG_PROPERTY_VALUE_ON)
        System.setProperty("kotlinx.coroutines.stacktrace.recovery", "true")
    }

    override fun onCreate() {
        super.onCreate()
        System.setProperty(DEBUG_PROPERTY_NAME, DEBUG_PROPERTY_VALUE_ON)
        System.setProperty("kotlinx.coroutines.stacktrace.recovery", "true")
        LiqPay.init(this)
    }
}