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
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {  // Attrs

    private var buttonText: String = ""
    private var loadingText: String = ""
    private var displayedText: String = ""
    private var buttonBackgroundColor : Int

    private var progressBar = 0f
    private val rectangle = RectF()
    private var progress: Float = 0f


    private var widthSize = 0
    private var heightSize = 0

    private var borderRadius = 20f
    private val paintRectangle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.colorPrimary)
    }
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 45f
    }
    private val paintProgressBar = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
    }
    private val paintProgressBarBackground = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    }

    private var valueAnimator = ValueAnimator()
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                isClickable = false
                displayedText = loadingText
                buttonBackgroundColor = Color.parseColor("#004349")
                invalidate()
                requestLayout()

                valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                    addUpdateListener {
                        progress = animatedValue as Float
                        invalidate()
                    }
                    repeatMode = ValueAnimator.REVERSE
                    repeatCount = ValueAnimator.INFINITE
                    duration = 3000
                    start()
                }
                this.isEnabled = false
            }
            ButtonState.Completed -> {
                displayedText = buttonText
                isClickable = true
                buttonBackgroundColor = Color.parseColor("#07c2AA")
                invalidate()
                requestLayout()

                valueAnimator.cancel()

                progress = 0f
                this.isEnabled = true
            }
            ButtonState.Clicked -> {

            }
        }
    }


    init {
       buttonBackgroundColor = ContextCompat.getColor(context, R.color.colorPrimary)
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            paintRectangle.color =
                getColor(R.styleable.LoadingButton_backgroundColor, ContextCompat.getColor(context, R.color.colorPrimary))
             borderRadius = getFloat(R.styleable.LoadingButton_borderRadius, 20f)
            buttonText = getString(R.styleable.LoadingButton_android_text)
                ?: context.getString(R.string.download)
            loadingText = getString(R.styleable.LoadingButton_loadingText)
                ?: context.getString(R.string.button_loading)
            paintText.textSize = getFloat(R.styleable.LoadingButton_android_textSize, 50f)
            paintText.color = getColor(R.styleable.LoadingButton_color, Color.WHITE)
            paintProgressBar.color =
                getColor(R.styleable.LoadingButton_progressBarColor,Color.YELLOW)

        }
        setState(ButtonState.Completed)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            val cornerRadius = 10.0f
            drawColor(buttonBackgroundColor)
            rectangle.set(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
            canvas.drawRoundRect(
                rectangle,
                cornerRadius,
                cornerRadius,
                paintRectangle
            )

            if (buttonState == ButtonState.Loading) {
               var progressValue = progress * measuredWidth.toFloat()
                val rectF = RectF(
                    0f,
                    0f,
                    progressValue,
                    measuredHeight.toFloat()
                )
                canvas.drawRoundRect(
                    rectF,
                    cornerRadius,
                    cornerRadius,
                    paintProgressBarBackground)
                val diameter =cornerRadius * 2f
                val arcRectSize = measuredHeight.toFloat() - paddingBottom.toFloat() - diameter

                progressValue = progress * 360f
                canvas.drawArc(
                    paddingStart + diameter,
                    paddingTop.toFloat() + diameter,
                    arcRectSize,
                    arcRectSize,
                    0f,
                    progressValue,
                    true,
                    paintProgressBar
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
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        widthSize = w
        heightSize = h
        rectangle.set(0f, 0f, width.toFloat(), height.toFloat())
    }

    @JvmName("setButtonState1")
    fun setState(state: ButtonState) {
        buttonState = state
    }


}