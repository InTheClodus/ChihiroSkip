package com.chihiro.skip.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.chihiro.skip.accessibility.AnalyzeSourceResult
import com.chihiro.skip.accessibility.EventWrapper
import com.chihiro.skip.accessibility.FastAccessibilityService
import com.chihiro.skip.accessibility.notificationBar
import com.chihiro.skip.manager.AnalyticsManager
import com.chihiro.skip.skipInterface.ParameterCheckInterface

/**
 * Author: CoderPig
 * Date: 2023-03-24
 * Desc:
 */
class MyAccessibilityService : FastAccessibilityService(), ParameterCheckInterface {
    companion object {
        private var instance: MyAccessibilityService? = null

        fun getInstance(): MyAccessibilityService {
            if (instance == null) {
                instance = MyAccessibilityService()
            }
            return instance!!
        }

        private const val TAG = "CpFastAccessibility--->"
    }

    private val path = Path()
    private val rect = Rect()
    private var executeHandleRootNode = false
    override val enableListenApp = true

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.d(TAG, "executeHandleRootNode: $executeHandleRootNode")
        if(executeHandleRootNode){
            try {
                if (!AnalyticsManager.isPerformScan(getCurrentRootNode().packageName.toString())) return

                val skipNodes = handleRootNodeByPackageName()
                if (skipNodes.isNotEmpty()) {
                    skipNodes[0].getBoundsInScreen(rect)
                    click(this, rect.exactCenterX(), rect.exactCenterY())
                }

                AnalyticsManager.increaseScanCount()
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    override fun analyzeCallBack(wrapper: EventWrapper?, result: AnalyzeSourceResult) {
        Log.d(TAG, "analyzeCallBack: $result")
    }



    override fun handleRootNodeByPackageName(): MutableList<AccessibilityNodeInfo> {
        return when (getCurrentRootNode().packageName.toString()) {
            "com.qiyi.video.lite", "com.qiyi.video" -> getCurrentRootNode().findAccessibilityNodeInfosByText("关闭")
            else -> getCurrentRootNode().findAccessibilityNodeInfosByText("跳过")
        }
    }

    @Synchronized
    override fun setExecuteHandleRootNode(value: Boolean) {
        executeHandleRootNode = value
        Log.d(TAG, "setExecuteHandleRootNode: $executeHandleRootNode")
        Log.d(TAG, "value: $value")
    }
    @Synchronized
    fun getExecuteHandleRootNode(): Boolean {
        return executeHandleRootNode
    }

    private fun getCurrentRootNode(): AccessibilityNodeInfo {
        val rootNode = rootInActiveWindow
        if (rootNode != null) return rootNode
        else throw IllegalStateException("No valid root node available");
    }
    private fun click(accessibilityService: AccessibilityService, x: Float, y: Float) {
        path.reset()
        path.moveTo(x, y)
        path.lineTo(x, y)

        val builder = GestureDescription.Builder()
        builder.addStroke(GestureDescription.StrokeDescription(path, 0, 1))
        val gesture = builder.build()

        accessibilityService.dispatchGesture(
            gesture,
            object : GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription) {
                    super.onCompleted(gestureDescription)

                    Toast.makeText(accessibilityService, "已为您跳过广告", Toast.LENGTH_SHORT).show()
                    AnalyticsManager.setShowToastCount()

                }
            },
            null
        )
    }
}