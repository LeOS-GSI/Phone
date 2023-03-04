package com.leos.dialer.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.leos.dialer.activities.CallActivity
import com.leos.dialer.helpers.ACCEPT_CALL
import com.leos.dialer.helpers.CallManager
import com.leos.dialer.helpers.DECLINE_CALL

class CallActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACCEPT_CALL -> {
                context.startActivity(CallActivity.getStartIntent(context))
                CallManager.accept()
            }
            DECLINE_CALL -> CallManager.reject()
        }
    }
}
