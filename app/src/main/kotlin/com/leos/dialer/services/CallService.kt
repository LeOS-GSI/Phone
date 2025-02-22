package com.leos.dialer.services

import android.app.KeyguardManager
import android.content.Context
import android.telecom.CallAudioState
import android.telecom.Call
import android.telecom.DisconnectCause
import android.telecom.InCallService
import com.leos.dialer.activities.CallActivity
import com.leos.dialer.extensions.config
import com.leos.dialer.extensions.isOutgoing
import com.leos.dialer.extensions.powerManager
import com.leos.dialer.extensions.showMessageNotification
import com.leos.dialer.helpers.CallManager
import com.leos.dialer.helpers.CallNotificationManager
import com.leos.dialer.helpers.NoCall
import com.leos.dialer.helpers.getCallContact

class CallService : InCallService() {
    private val callNotificationManager by lazy { CallNotificationManager(this) }

    private val callListener = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            super.onStateChanged(call, state)
            if (state == Call.STATE_DISCONNECTED || state == Call.STATE_DISCONNECTING) {
                callNotificationManager.cancelNotification()
            } else {
                callNotificationManager.setupNotification()
            }
        }
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        CallManager.onCallAdded(call)
        CallManager.inCallService = this
        call.registerCallback(callListener)

        val isScreenLocked = (getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager).isDeviceLocked
        if (!powerManager.isInteractive || call.isOutgoing() || isScreenLocked || config.showIncomingCallsFullScreen) {
            try {
                callNotificationManager.setupNotification(true)
                startActivity(CallActivity.getStartIntent(this))
            } catch (e: Exception) {
                // seems like startActivity can throw AndroidRuntimeException and ActivityNotFoundException, not yet sure when and why, lets show a notification
                callNotificationManager.setupNotification()
            }
        } else {
            callNotificationManager.setupNotification()
        }
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        call.unregisterCallback(callListener)
        callNotificationManager.cancelNotification()
        val wasPrimaryCall = call == CallManager.getPrimaryCall()
        CallManager.onCallRemoved(call)
        if (CallManager.getPhoneState() == NoCall) {
            CallManager.inCallService = null
            callNotificationManager.cancelNotification()
        } else {
            callNotificationManager.setupNotification()
            if (wasPrimaryCall) {
                startActivity(CallActivity.getStartIntent(this))
            }
        }
        call.details?.let {
            if (call.details.disconnectCause.code == DisconnectCause.MISSED && config.missedCallNotifications) {
                getCallContact(this.applicationContext, call) { callContact ->
                    showMessageNotification(callContact)
                }
            }
        }
    }

    override fun onCallAudioStateChanged(audioState: CallAudioState?) {
        super.onCallAudioStateChanged(audioState)
        if (audioState != null) {
            CallManager.onAudioStateChanged(audioState)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        callNotificationManager.cancelNotification()
    }
}

