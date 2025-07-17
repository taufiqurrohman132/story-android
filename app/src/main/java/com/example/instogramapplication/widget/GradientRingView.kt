package com.example.instogramapplication.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.View
import com.example.instogramapplication.R


class GradientRingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var ringThickness = 16f
    private var ringColors: IntArray = intArrayOf(Color.WHITE, Color.WHITE, Color.WHITE)

    private val outterPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val clearPaint = Paint().apply {
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.GradientRingView,
            0,
            0
        ).apply {
            try {
                ringThickness = getDimension(R.styleable.GradientRingView_ringThickness, 16f)

                val startColor = getColor(R.styleable.GradientRingView_ringStartColor, Color.WHITE)
                val centerColor = getColor(R.styleable.GradientRingView_ringCenterColor, Color.WHITE)
                val endColor = getColor(R.styleable.GradientRingView_ringEndColor, Color.WHITE)

                ringColors = intArrayOf(startColor, centerColor, endColor)

            }finally {
                recycle()
            }
        }

        // applay gradient
        outterPaint.shader = SweepGradient(0f, 0f, ringColors, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2f
        val cy = height / 2f
        val outerRadius = minOf(cx, cy)
        val innerRadius = outerRadius - ringThickness

        val layer = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)

        canvas.apply {
            translate(cx, cy)
            drawCircle(0f, 0f, outerRadius, outterPaint)
            drawCircle(0f, 0f, innerRadius, clearPaint)

            restoreToCount(layer)
        }
    }


}