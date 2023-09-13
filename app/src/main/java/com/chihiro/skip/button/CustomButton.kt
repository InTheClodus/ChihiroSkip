package com.chihiro.skip.button

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.chihiro.skip.R
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.*

class CustomButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val button: Button
    private val progressIndicator: CircularProgressIndicator

    private var startImage: Drawable? = null
    private var stopImage: Drawable? = null

    private var currentState: State = State.START

    init {
        inflate(context, R.layout.custom_button_layout, this)

        button = findViewById(R.id.button)
        progressIndicator = findViewById(R.id.progress_indicator)

        // 设置点击监听器
        button.setOnClickListener {
            when (currentState) {
                State.START -> {
                    // 切换到加载中状态
                    setCurrentState(State.LOADING)
                    // 启动加载操作
                    startLoading()
                }
                State.LOADING -> {
                    // 切换到停止状态
                    setCurrentState(State.STOP)
                    // 停止加载操作
                    stopLoading()
                }
                State.STOP -> {
                    // 切换到开始状态
                    setCurrentState(State.START)
                    // 在这里执行开始操作
                    startOperation()
                }
            }
        }
    }

    // 设置按钮的开始和停止图片
    fun setImages(startImageResId: Int, stopImageResId: Int) {
        startImage = ContextCompat.getDrawable(context, startImageResId)
        stopImage = ContextCompat.getDrawable(context, stopImageResId)
        button.background = startImage
    }

    // 设置当前状态
    private fun setCurrentState(state: State) {
        currentState = state
        when (currentState) {
            State.START -> {
                button.background = startImage
                progressIndicator.isVisible = false
            }
            State.LOADING -> {
                button.background = null
                progressIndicator.isVisible = true
            }
            State.STOP -> {
                button.background = stopImage
                progressIndicator.isVisible = false
            }
        }
    }

    // 开始加载操作（模拟异步操作）
    private fun startLoading() {
        GlobalScope.launch(Dispatchers.IO) {
            delay(3000) // 模拟加载中状态持续3秒
            withContext(Dispatchers.Main) {
                setCurrentState(State.STOP)
            }
        }
    }

    // 停止加载操作（取消加载中状态）
    private fun stopLoading() {
        // 取消加载操作，这里可以根据实际需求进行处理
        // 取消加载中的异步任务，或者执行其他停止操作
        // 在这里只是简单切换状态示例
        setCurrentState(State.START)
    }

    // 开始操作（模拟异步操作）
    private fun startOperation() {
        GlobalScope.launch(Dispatchers.IO) {
            delay(2000) // 模拟操作持续2秒
            withContext(Dispatchers.Main) {
                setCurrentState(State.START)
            }
        }
    }

    private enum class State {
        START,
        LOADING,
        STOP
    }
}
