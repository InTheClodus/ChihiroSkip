package com.chihiro.skip.button

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class CircularButton(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val buttonPaint = Paint()
    private val loadingPaint = Paint()
    private val successPaint = Paint()
    private val failurePaint = Paint()

    private val iconPath = Path()
    private var rotationAngle = 0f
    private var isLoading = false
    private var isLoadSuccess = false
    private var isLoadFailure = false

    init {
        // 初始化画笔
        buttonPaint.color = Color.BLUE
        buttonPaint.isAntiAlias = true
        loadingPaint.color = Color.GRAY
        loadingPaint.isAntiAlias = true
        successPaint.color = Color.GREEN
        successPaint.isAntiAlias = true
        failurePaint.color = Color.RED
        failurePaint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val radius = Math.min(width, height) / 2f

        // 绘制按钮背景
        canvas.drawCircle(width / 2f, height / 2f, radius, buttonPaint)

        if (isLoading) {
            drawLoadingAnimation(canvas, radius)
        } else if (isLoadSuccess) {
            canvas.drawCircle(width / 2f, height / 2f, radius, successPaint)
            drawCheckMark(canvas, radius)
        } else if (isLoadFailure) {
            canvas.drawCircle(width / 2f, height / 2f, radius, failurePaint)
            drawCrossMark(canvas, radius)
        } else {
            // 绘制未开始时的三角形图标
            drawTriangleIcon(canvas, radius)
        }
    }

    private fun drawTriangleIcon(canvas: Canvas, radius: Float) {
        val centerX = width / 2f
        val centerY = height / 2f

        // 绘制垂直线
        canvas.drawLine(centerX, centerY - radius * 0.4f, centerX, centerY + radius * 0.4f, loadingPaint)

        // 绘制向右的线段
        canvas.drawLine(centerX, centerY - radius * 0.4f, centerX + radius * 0.4f, centerY, loadingPaint)
        canvas.drawLine(centerX, centerY + radius * 0.4f, centerX + radius * 0.4f, centerY, loadingPaint)
    }


    private fun drawLoadingAnimation(canvas: Canvas, radius: Float) {
        val centerX = width / 2f
        val centerY = height / 2f

        // 圆环的宽度
        val strokeWidth = radius * 0.1f

        // 绘制背景圆环
        loadingPaint.strokeWidth = strokeWidth
        canvas.drawCircle(centerX, centerY, radius - strokeWidth / 2f, loadingPaint)

        // 绘制动态进度圆环
        val loadingRect = RectF(
            centerX - radius + strokeWidth / 2f,
            centerY - radius + strokeWidth / 2f,
            centerX + radius - strokeWidth / 2f,
            centerY + radius - strokeWidth / 2f
        )
        val sweepAngle = 360f * rotationAngle / 360f // 根据旋转角度计算扫描角度
        canvas.drawArc(loadingRect, -90f, sweepAngle, false, loadingPaint)
    }


    private fun drawCheckMark(canvas: Canvas, radius: Float) {
        // 绘制打勾图标
        val checkMarkSize = radius * 0.5f
        val startX = width / 2f - checkMarkSize * 0.6f
        val startY = height / 2f - checkMarkSize * 0.2f
        val endX = width / 2f - checkMarkSize * 0.1f
        val endY = height / 2f + checkMarkSize * 0.4f
        canvas.drawLine(startX, startY, endX, endY, successPaint)
        val secondEndX = width / 2f + checkMarkSize * 0.6f
        canvas.drawLine(endX, endY, secondEndX, height / 2f - checkMarkSize * 0.2f, successPaint)
    }

    private fun drawCrossMark(canvas: Canvas, radius: Float) {
        // 绘制打叉图标
        val crossMarkSize = radius * 0.5f
        val startX1 = width / 2f - crossMarkSize * 0.5f
        val startY1 = height / 2f - crossMarkSize * 0.5f
        val endX1 = width / 2f + crossMarkSize * 0.5f
        val endY1 = height / 2f + crossMarkSize * 0.5f

        val startX2 = width / 2f + crossMarkSize * 0.5f
        val startY2 = height / 2f - crossMarkSize * 0.5f
        val endX2 = width / 2f - crossMarkSize * 0.5f
        val endY2 = height / 2f + crossMarkSize * 0.5f

        canvas.drawLine(startX1, startY1, endX1, endY1, failurePaint)
        canvas.drawLine(startX2, startY2, endX2, endY2, failurePaint)
    }

    fun startLoading() {
        isLoading = true
        isLoadSuccess = false
        isLoadFailure = false
        rotationAngle = 0f
        // 启动加载动画
        startLoadingAnimation()
    }

    private fun startLoadingAnimation() {
        postDelayed({
            rotationAngle += 10f
            if (isLoading) {
                invalidate()
                startLoadingAnimation()
            }
        }, 50)
    }

    fun loadSuccess() {
        isLoading = false
        isLoadSuccess = true
        isLoadFailure = false
        invalidate()
    }

    fun loadFailure() {
        isLoading = false
        isLoadSuccess = false
        isLoadFailure = true
        invalidate()
    }
}

