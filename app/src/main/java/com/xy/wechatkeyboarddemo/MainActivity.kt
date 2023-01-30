package com.xy.wechatkeyboarddemo

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewTreeObserver
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.xy.wechatkeyboarddemo.listener.KeyBoardInsetsCallBack
import com.xy.wechatkeyboarddemo.listener.KeyBoardListener
import com.xy.wechatkeyboarddemo.utils.SoftKeyBoardUtil

enum class HandleType {
    ONLY_KEYBOARD_DOWN,
    ONLY_KEYBOARD_UP,
    ONLY_PANEL_UP,
    ONLY_PANEL_DOWN,
    KU_PD,
    KD_PU
}

class MainActivity : AppCompatActivity() {
    var PANEL_HEIGHT = 1000 //表情面板高度
    lateinit var contentLayout: LinearLayout
    lateinit var listLayout: LinearLayout
    lateinit var inputEt: EditText
    lateinit var faceButton: ImageView
    private var panelAnimator: ObjectAnimator? = null
    private var isPanelShow = false
    private var handleType = HandleType.ONLY_KEYBOARD_UP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        contentLayout = findViewById(R.id.layout_content)
        listLayout = findViewById(R.id.layout_list)
        inputEt = findViewById(R.id.et_input)
        faceButton = findViewById(R.id.face_btn)
        contentLayout.viewTreeObserver
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    contentLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    calculationLayoutSize()
                    initListener()
                }
            })
        inputEt.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                handleType = if (isPanelShow) {
                    HandleType.KU_PD
                } else {
                    HandleType.ONLY_KEYBOARD_UP
                }
                SoftKeyBoardUtil.showSoftInput(inputEt.context)
            }
            false
        }
        faceButton.setOnClickListener {
            showKeyBoardAndPanel()
        }

    }

    private fun calculationLayoutSize() {
        val layoutParams = contentLayout.layoutParams as FrameLayout.LayoutParams
        val layoutParams2 = listLayout.layoutParams as LinearLayout.LayoutParams
        val cHeight: Int = contentLayout.height
        PANEL_HEIGHT=(cHeight*0.45).toInt()
        layoutParams2.height = cHeight
        listLayout.layoutParams = layoutParams2
        layoutParams.height = cHeight + PANEL_HEIGHT
        contentLayout.layoutParams = layoutParams
    }

    private fun initListener() {
        val keyBoardInsetsCallBack =
            KeyBoardInsetsCallBack(object :
                KeyBoardListener {
                override fun onAnimStart(moveDistance: Int) {
                    isPanelShow = when (handleType) {
                        HandleType.ONLY_PANEL_DOWN -> false
                        HandleType.ONLY_PANEL_UP -> true
                        HandleType.ONLY_KEYBOARD_DOWN -> false
                        HandleType.ONLY_KEYBOARD_UP -> false
                        HandleType.KD_PU -> {
                            panelAnimateTo(-PANEL_HEIGHT)
                            true
                        }
                        HandleType.KU_PD -> {
                            panelAnimateTo(-moveDistance)
                            false
                        }
                    }
                }

                override fun onAnimDoing(offsetX: Int, offsetY: Int) {
                    if (handleType != HandleType.KU_PD && handleType != HandleType.KD_PU) {
                        contentLayout.translationY = offsetY.toFloat()
                    }

                }

                override fun onAnimEnd() {
                    handleType = HandleType.ONLY_KEYBOARD_UP
                }
            })
        ViewCompat.setWindowInsetsAnimationCallback(window.decorView, keyBoardInsetsCallBack)
    }

    private fun showKeyBoardAndPanel() {
        if (isPanelShow) {
            handleType = HandleType.KU_PD
            SoftKeyBoardUtil.showSoftInput(inputEt.context)
        } else {
            if (SoftKeyBoardUtil.isSoftInputShown(this@MainActivity)) {
                SoftKeyBoardUtil.hideSoftInput(this@MainActivity, inputEt.windowToken)
                handleType = HandleType.KD_PU
            } else {
                handleType = HandleType.ONLY_PANEL_UP
                panelAnimateTo(-PANEL_HEIGHT)
            }
            isPanelShow = true
        }
    }

    private fun panelAnimateTo(offset: Int) {
        panelAnimator = ObjectAnimator.ofFloat(contentLayout, "translationY", offset.toFloat())
        panelAnimator?.interpolator = FastOutSlowInInterpolator()
        panelAnimator?.start()
    }

    override fun onBackPressed() {
        if (isPanelShow) {
            handleType = HandleType.ONLY_PANEL_DOWN
            panelAnimateTo(0)
            return
        }
        super.onBackPressed()
    }
}