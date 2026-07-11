package com.example.tryloanify.data.remote.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object OtpBus {
    private val _otp = MutableStateFlow<String?>(null)
    val otp: StateFlow<String?> = _otp.asStateFlow()

    fun emit(code: String) {
        _otp.value = code
    }

    fun clear() {
        _otp.value = null
    }
}

class OtpReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION != intent.action) return
        val extras = intent.extras ?: return
        val status = extras.get(SmsRetriever.EXTRA_STATUS) as? Status ?: return
        if (status.statusCode != CommonStatusCodes.SUCCESS) return
        val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE).orEmpty()
        val otp = Regex("\\b\\d{6}\\b").find(message)?.value ?: return
        OtpBus.emit(otp)
    }
}
