package com.chihiro.skip.skipInterface

import android.view.accessibility.AccessibilityNodeInfo

interface  ParameterCheckInterface {
    fun handleRootNodeByPackageName(): MutableList<AccessibilityNodeInfo>
    fun setExecuteHandleRootNode(value: Boolean)
}