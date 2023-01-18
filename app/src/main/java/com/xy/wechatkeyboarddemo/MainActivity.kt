package com.xy.wechatkeyboarddemo

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.xy.wechatkeyboarddemo.utils.SoftKeyBoardUtil

enum class PanelState {
    HIDE,
    SHOW,
    HALF
}

class MainActivity : AppCompatActivity() {
    val SOFT_INPUT_HEIGHT = 835 // 软键盘高度
    val PANEL_HEIGHT = 1000 //表情面板高度
    lateinit var contentLayout: LinearLayout
    lateinit var listLayout: LinearLayout
    lateinit var inputEt: EditText
    lateinit var faceButton: ImageView
    private var panelAnimator: ObjectAnimator? = null
    private var panelState = PanelState.HIDE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        initListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        contentLayout = findViewById(R.id.layout_content)
        listLayout = findViewById(R.id.layout_list)
        inputEt = findViewById(R.id.et_input)
        faceButton=findViewById(R.id.face_btn)
        contentLayout.viewTreeObserver
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    contentLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    calculationLayoutSize()
                }
            })
        inputEt.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                panelHalf()
                SoftKeyBoardUtil.showSoftInput(this@MainActivity);
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
        layoutParams2.height = cHeight
        listLayout.layoutParams = layoutParams2
        layoutParams.height = cHeight + PANEL_HEIGHT
        contentLayout.layoutParams = layoutParams
    }

    private fun initListener() {
        val keyBoardInsetsCallBack = KeyBoardInsetsCallBack(object : KeyBoardListener {
            override fun onAnimStart() {
                if(SoftKeyBoardUtil.isSoftInputShown(this@MainActivity)){
                    panelState=PanelState.HIDE
                }
            }
            override fun onAnimDoing(offsetX: Int, offsetY: Int) {
                if (panelState != PanelState.SHOW && panelAnimator?.isRunning != true) {
                    contentLayout.translationY = offsetY.toFloat()
                }
            }

            override fun onAnimEnd() {}
        })
        ViewCompat.setWindowInsetsAnimationCallback(inputEt.rootView, keyBoardInsetsCallBack)
    }

    private fun showKeyBoardAndPanel() {
        if (panelState == PanelState.SHOW) {
            panelHalf()
            SoftKeyBoardUtil.showSoftInput(inputEt.context)
        } else {
            if (SoftKeyBoardUtil.isSoftInputShown(this@MainActivity)) {
                SoftKeyBoardUtil.hideSoftInput(this@MainActivity, inputEt.windowToken)
            }
            panelShow()
        }
    }

    private fun panelHalf() {
        if (panelState != PanelState.HALF) {
            panelAnimateTo(-SOFT_INPUT_HEIGHT)
            panelState = PanelState.HALF
        }
    }

    private fun panelShow() {
        if (panelState != PanelState.SHOW) {
            panelAnimateTo(-PANEL_HEIGHT)
            panelState = PanelState.SHOW
        }
    }

    private fun panelHide() {
        if (panelState != PanelState.HIDE) {
            panelAnimateTo(0)
            panelState = PanelState.HIDE
        }
    }

    private fun panelAnimateTo(offset: Int) {
        panelAnimator = ObjectAnimator.ofFloat(contentLayout, "translationY", offset.toFloat())
        panelAnimator?.interpolator = FastOutSlowInInterpolator()
        panelAnimator?.start()
    }

    override fun onBackPressed() {
        if(panelState != PanelState.HIDE){
            panelHide()
            return
        }
        super.onBackPressed()
    }
}