package com.chihiro.skip

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.hypot

class MainActivity : AppCompatActivity() {

    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var startButton: ImageButton
    private lateinit var constraintLayoutResult: ConstraintLayout
    private var isAnimating = false // 用于跟踪动画状态

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        window.statusBarColor = Color.TRANSPARENT
        constraintLayout = findViewById(R.id.constraintLayout)
        startButton = findViewById(R.id.start_button)
        constraintLayoutResult = findViewById(R.id.result_view)

        startButton.setOnClickListener {
            if (!isAnimating) { // 检查是否正在执行动画
                startAnimation()
            } else {
                reverseAnimation()
            }
        }
    }

    private fun startAnimation() {
        val orangeColor = getColor(R.color.orange) // 橙色
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

    private fun reverseAnimation() {
        val green = getColor(R.color.green)
        val orangeColor = getColor(R.color.orange)

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
}
