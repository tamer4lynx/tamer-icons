package com.nanofuxion.tamericons

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.Typeface

/**
 * Drawable that renders a single icon font character, scaled and centered in bounds.
 * Color handling follows Android-Iconics: color is stored in a ColorStateList and
 * applied to the paint in draw() so the icon color is not overridden by framework tint.
 */
class IconDrawable(
    private val typeface: Typeface,
    private val codepoint: Int,
    color: Int,
    private val sizePx: Int
) : Drawable() {

    private var colorList: ColorStateList = ColorStateList.valueOf(color)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.LEFT
        this.typeface = this@IconDrawable.typeface
    }

    private val path = Path()
    private val pathBounds = RectF()

    private val charSequence: CharSequence
        get() = if (codepoint <= 0xFFFF) Character.toString(codepoint.toChar())
        else String(Character.toChars(codepoint))

    init {
        setBounds(0, 0, sizePx, sizePx)
    }

    fun setColor(value: Int) {
        if (colorList.defaultColor != value) {
            colorList = ColorStateList.valueOf(value)
            invalidateSelf()
        }
    }

    override fun draw(canvas: Canvas) {
        if (codepoint == 0) return
        paint.color = colorList.getColorForState(state, colorList.defaultColor)
        paint.colorFilter = null
        val b = bounds
        if (b.width() <= 0 || b.height() <= 0) return

        val seq = charSequence
        path.reset()
        var textSize = b.height().toFloat()
        paint.textSize = textSize
        paint.getTextPath(seq.toString(), 0, seq.length, 0f, 0f, path)
        path.computeBounds(pathBounds, true)

        val pathW = pathBounds.width().coerceAtLeast(1f)
        val pathH = pathBounds.height().coerceAtLeast(1f)
        val scaleW = b.width() / pathW
        val scaleH = b.height() / pathH
        val scale = minOf(scaleW, scaleH).coerceAtMost(1f)
        textSize *= scale
        paint.textSize = textSize
        path.reset()
        paint.getTextPath(seq.toString(), 0, seq.length, 0f, 0f, path)
        path.computeBounds(pathBounds, true)

        val offsetX = b.left + (b.width() - pathBounds.width()) / 2 - pathBounds.left
        val offsetY = b.top + (b.height() - pathBounds.height()) / 2 - pathBounds.top
        path.offset(offsetX, offsetY)
        canvas.drawPath(path, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = android.graphics.PixelFormat.TRANSLUCENT

    override fun getIntrinsicWidth(): Int = sizePx
    override fun getIntrinsicHeight(): Int = sizePx
}
