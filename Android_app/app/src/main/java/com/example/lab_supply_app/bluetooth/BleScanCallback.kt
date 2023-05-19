package com.example.lab_supply_app.bluetooth

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log

class BleScanCallback(private val onBatchScanResultAction: (MutableList<ScanResult>?) -> Unit = {},
                      private val onScanFailedAction: (Int) -> Unit = {},
                      private val onScanResultAction: (ScanResult?) -> Unit = {}) : ScanCallback() {

    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        super.onScanResult(callbackType, result)
        onScanResultAction(result)
    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
        Log.d("scan failed", errorCode.toString())
        onScanFailedAction(errorCode)
    }

    override fun onBatchScanResults(results: MutableList<ScanResult>?) {
        super.onBatchScanResults(results)
        onBatchScanResultAction(results)
    }
}