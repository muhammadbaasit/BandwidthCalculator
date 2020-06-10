package com.area51.bandwidthcal

import android.net.TrafficStats
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mLastRxBytes: Long = 0
    private var mLastTxBytes: Long = 0
    private var mLastTime: Long = 0
    private var mSpeed: Speed? = null

    private val mHandler = Handler()

    private val mHandlerRunnable: Runnable = object : Runnable {

        override fun run() {

            var currentRxBytes = TrafficStats.getTotalRxBytes()
            var currentTxBytes = TrafficStats.getTotalTxBytes()
            var usedRxBytes = currentRxBytes - mLastRxBytes
            var usedTxBytes = currentTxBytes - mLastTxBytes
            var currentTime = System.currentTimeMillis()
            var usedTime = currentTime - mLastTime
            mLastRxBytes = currentRxBytes
            mLastTxBytes = currentTxBytes
            mLastTime = currentTime
            mSpeed!!.calcSpeed(usedTime, usedRxBytes, usedTxBytes)

            mHandler.postDelayed(this, 1000)

            Log.d("SPEEDVAL","Down :"+mSpeed?.down?.speedValue+"Up :"+mSpeed?.up?.speedValue)

            tvSpeed.text = "Down :"+mSpeed?.down?.speedValue+"  Up :"+mSpeed?.up?.speedValue

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mLastRxBytes = TrafficStats.getTotalRxBytes()
        mLastTxBytes = TrafficStats.getTotalTxBytes()
        mLastTime = System.currentTimeMillis()
        mSpeed = Speed(this)
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacks(mHandlerRunnable)
    }

    override fun onResume() {
        super.onResume()
        mHandler.removeCallbacks(mHandlerRunnable)
        mHandler.post(mHandlerRunnable)
    }

    override fun onStop() {
        super.onStop()
        mHandler.removeCallbacks(mHandlerRunnable)
    }
}
