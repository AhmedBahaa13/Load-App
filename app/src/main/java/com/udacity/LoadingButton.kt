package com.udacity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {  // Attrs
    private var buttonText: String = ""
    private var loadingText: String = ""
    private var displayedText: String = ""

    private var progressBar = 0f
    private var circleAngle = 0f
    private val rectangle = RectF()

    private var widthSize = 0
    private var heightSize = 0

    private var borderRadius = 20f
    private val paintRectangle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
    }
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }
    private val paintProgressBar = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintCircle = Paint(Paint.ANTI_ALIAS_FLAG)

    private lateinit var valueAnimatorProgressBar: ValueAnimator
    private lateinit var valueAnimatorCircle: ValueAnimator
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                isClickable = false
                displayedText = loadingText
                startProgressBarAnimation()
            }
            ButtonState.Completed -> {
                displayedText = buttonText
                isClickable = true
            }
            ButtonState.Clicked -> {

            }
        }
    }


    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            paintRectangle.color =
                getColor(R.styleable.LoadingButton_backgroundColor, Color.rgb(67, 182, 172))
            paintCircle.color =
                getColor(R.styleable.LoadingButton_circleColor, Color.rgb(255, 171, 64))
            borderRadius = getFloat(R.styleable.LoadingButton_borderRadius, 20f)
            buttonText = getString(R.styleable.LoadingButton_android_text)
                ?: context.getString(R.string.download)
            loadingText = getString(R.styleable.LoadingButton_loadingText)
                ?: context.getString(R.string.button_loading)
            paintText.textSize = getFloat(R.styleable.LoadingButton_android_textSize, 50f)
            paintText.color = getColor(R.styleable.LoadingButton_color, Color.WHITE)
            paintProgressBar.color =
                getColor(R.styleable.LoadingButton_progressBarColor, Color.rgb(0, 137, 123))

        }
        setState(ButtonState.Completed)
    }

    private fun startProgressBarAnimation() {
        progressBar = 0f
        circleAngle = 0f
        valueAnimatorCircle = ValueAnimator.ofFloat(0f, 360f)
        valueAnimatorProgressBar = ValueAnimator.ofFloat(0f, widthSize.toFloat())
        valueAnimatorProgressBar.apply {
            duration = 2000
            repeatMode = ValueAnimator.RESTART
            start()
            addUpdateListener {
                progressBar = it.animatedValue as Float
                invalidate()
            }
        }
        valueAnimatorCircle.apply {
            duration = 2000
            addUpdateListener {
                circleAngle = it.animatedValue as Float
                invalidate()
            }

            doOnEnd {
                setState(ButtonState.Completed)
            }
            start()
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            canvas.drawRoundRect(rectangle, 25f, 25f, paintRectangle)

            if (buttonState == ButtonState.Loading) {
                val rectF = RectF(0f, 0f, progressBar, heightSize.toFloat())
                canvas.drawRoundRect(rectF, 25f, 25f, paintProgressBar)
                canvas.drawArc(
                    ((widthSize / 4) * 3 - 30).toFloat(),
                    (heightSize / 2 - 30).toFloat(),
                    ((widthSize / 4) * 3 + 30).toFloat(),
                    (heightSize / 2 + 30).toFloat(), 0f, circleAngle, true, paintCircle
                )
            }
            canvas.drawText(
                displayedText,
                widthSize / 2f,
                heightSize / 2f + paintText.textSize / 2f,
                paintText
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            View.MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rectangle.set(0f, 0f, width.toFloat(), height.toFloat())
    }

    @JvmName("setButtonState1")
    fun setState(state: ButtonState) {
        buttonState = state
    }


}