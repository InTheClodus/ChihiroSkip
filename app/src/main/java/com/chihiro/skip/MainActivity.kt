package com.chihiro.skip

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.chihiro.scrolllayout.ScrollLayout
import com.chihiro.skip.accessibility.isAccessibilityEnable
import com.chihiro.skip.accessibility.requireAccessibility
import com.chihiro.skip.service.MyAccessibilityService
import com.chihiro.skip.utils.ScreenUtil
import kotlin.math.hypot


class MainActivity : AppCompatActivity() {

    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var startButton: ImageButton
    private lateinit var constraintLayoutResult: ConstraintLayout
    private lateinit var relativeLayout: RelativeLayout
    private var isAnimating = false // 用于跟踪动画状态
    private lateinit var mScrollLayout: ScrollLayout
    val myAccessibilityService = MyAccessibilityService.getInstance()

    /// 提示信息文本
    private var textInfo : TextView ?= null

    private val mOnScrollChangedListener: ScrollLayout.OnScrollChangedListener =
        object : ScrollLayout.OnScrollChangedListener {
            override fun onScrollProgressChanged(currentProgress: Float) {
                if (currentProgress >= 0) {
                    var precent = 255 * currentProgress
                    if (precent > 255) {
                        precent = 255f
                    } else if (precent < 0) {
                        precent = 0f
                    }
                    mScrollLayout.background.alpha = 255 - precent.toInt()
                }

            }

            override fun onScrollFinished(currentStatus: ScrollLayout.Status) {
                if (currentStatus == ScrollLayout.Status.EXIT) {
                    Log.i("", "")
                }
            }

            override fun onChildScroll(top: Int) {}
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        relativeLayout.setOnClickListener { mScrollLayout.scrollToExit() }

        startButton.setOnClickListener {
            if(isAccessibilityEnable){
                if(myAccessibilityService.getExecuteHandleRootNode()){
                    reverseAnimation()
                    myAccessibilityService.setExecuteHandleRootNode(false)
                }else{
                    startAnimation()
                    myAccessibilityService.setExecuteHandleRootNode(true)
                }
            }else{
                requireAccessibility()
            }
        }
    }

    /*
    * 启动动画
     */
    private fun startAnimation() {
        val orangeColor = getColor(R.color.green) // 橙色
        val transparent = getColor(android.R.color.transparent) // 透明色

        // 获取ImageButton的中心坐标
        val centerX = startButton.left + startButton.width / 2
        val centerY = startButton.top + startButton.height / 2

        // 计算ConstraintLayout的对角线长度（作为动画结束时的扩散半径）
        val endRadius =
            hypot(constraintLayout.width.toDouble(), constraintLayout.height.toDouble()).toFloat()

        // 创建橙色背景
        val orangeBackground = View(this)
        orangeBackground.setBackgroundColor(orangeColor)
        orangeBackground.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        constraintLayout.addView(orangeBackground, 0) // 将橙色背景添加到constraintLayout的最底层

        // 创建裁剪动画
        val clipAnimator = ViewAnimationUtils.createCircularReveal(
            orangeBackground,
            centerX,
            centerY,
            0f,
            endRadius
        )
        clipAnimator.duration = 500 // 动画持续时间为1秒
        clipAnimator.interpolator = AccelerateInterpolator()

        // 添加动画结束监听器，以便在动画完成后将橙色背景视图移除，并清除constraintLayout的背景色
        clipAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                constraintLayout.removeView(orangeBackground) // 移除橙色背景
                constraintLayout.setBackgroundColor(transparent) // 清除constraintLayout的背景色
                constraintLayoutResult.visibility = View.VISIBLE
                constraintLayoutResult.setBackgroundColor(orangeColor)
                isAnimating = true
            }
        })
        // 启动裁剪动画
        clipAnimator.start()
    }

    /*
     * 反向动画(关闭动画)
     */
    private fun reverseAnimation() {
        val green = getColor(R.color.orange)
        val orangeColor = getColor(R.color.green)

        // 设置 constraintLayout 的背景为绿色
        constraintLayout.setBackgroundColor(green)

        // 隐藏结果视图
        constraintLayoutResult.visibility = View.INVISIBLE

        // 计算 ImageButton 的中心坐标
        val centerX = startButton.left + startButton.width / 2
        val centerY = startButton.top + startButton.height / 2

        // 计算 ConstraintLayout 的对角线长度（作为反向动画结束时的扩散半径）
        val endRadius =
            hypot(constraintLayout.width.toDouble(), constraintLayout.height.toDouble()).toFloat()

        // 创建橙色背景
        val orangeBackground = View(this)
        orangeBackground.setBackgroundColor(orangeColor)
        orangeBackground.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        constraintLayout.addView(orangeBackground, 0) // 将橙色背景添加到constraintLayout的最底层

        // 创建反向裁剪动画
        val reverseClipAnimator = ViewAnimationUtils.createCircularReveal(
            orangeBackground,
            centerX,
            centerY,
            endRadius,
            0f // 反向动画从扩散状态返回到0半径
        )
        reverseClipAnimator.duration = 500 // 反向动画持续时间为2秒，使其更平滑
        reverseClipAnimator.interpolator = AccelerateInterpolator()

        // 添加反向动画结束监听器，以便在反向动画完成后设置 isAnimating 为 false，并移除橙色背景
        reverseClipAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                isAnimating = false
                constraintLayout.removeView(orangeBackground) // 移除橙色背景
            }
        })

        // 启动反向裁剪动画
        reverseClipAnimator.start()
    }

    private fun initView() {

        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        mScrollLayout = findViewById(R.id.scroll_down_layout)
        relativeLayout = findViewById(R.id.root)
        /**设置 setting*/
        mScrollLayout.setMinOffset(350)
        mScrollLayout.setMaxOffset((ScreenUtil.getScreenHeight(this) * 0.5).toInt())
        mScrollLayout.setExitOffset(ScreenUtil.dip2px(this, 150F))
        mScrollLayout.setToOpen()

        mScrollLayout.setIsSupportExit(true)
        mScrollLayout.isAllowHorizontalScroll = true
        mScrollLayout.setOnScrollChangedListener(mOnScrollChangedListener)
        mScrollLayout.setToExit()

        mScrollLayout.background.alpha = 0
        window.statusBarColor = Color.TRANSPARENT
        constraintLayout = findViewById(R.id.constraintLayout)
        startButton = findViewById(R.id.start_button)
        constraintLayoutResult = findViewById(R.id.result_view)
        textInfo = findViewById(R.id.textView2)

    }


    override fun onResume() {
        super.onResume()
        if(isAccessibilityEnable){
            textInfo?.text  = getString(R.string.service_status_enable)
        }else{
            textInfo?.text  = getString(R.string.service_status_disable)
        }
    }
}
