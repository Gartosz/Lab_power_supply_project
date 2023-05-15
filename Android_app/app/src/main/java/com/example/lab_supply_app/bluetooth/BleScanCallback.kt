package com.example.lab_supply_app.bluetooth

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult

class BleScanCallback : ScanCallback() {

    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        super.onScanResult(callbackType, result)
    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
    }

    override fun onBatchScanResults(results: MutableList<ScanResult>?) {
        super.onBatchScanResults(results)
    }
}