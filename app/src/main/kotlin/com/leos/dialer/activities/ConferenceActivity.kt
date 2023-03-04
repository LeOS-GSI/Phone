package com.leos.dialer.activities

import android.os.Bundle
import com.leos.commons.helpers.NavigationIcon
import com.leos.dialer.R
import com.leos.dialer.adapters.ConferenceCallsAdapter
import com.leos.dialer.helpers.CallManager
import kotlinx.android.synthetic.main.activity_conference.*

class ConferenceActivity : SimpleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conference)

        conference_calls_list.adapter = ConferenceCallsAdapter(this, conference_calls_list, ArrayList(CallManager.getConferenceCalls())) {}
    }

    override fun onResume() {
        super.onResume()
        setupToolbar(conference_toolbar, NavigationIcon.Arrow)
    }
}
