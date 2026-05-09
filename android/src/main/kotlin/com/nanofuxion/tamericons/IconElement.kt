package com.nanofuxion.tamericons

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.lynx.react.bridge.Dynamic
import com.lynx.react.bridge.ReadableType
import com.lynx.tasm.behavior.LynxContext
import com.lynx.tasm.behavior.LynxProp
import com.lynx.tasm.behavior.ui.LynxUI
import kotlin.math.roundToInt

class IconElement(context: LynxContext) : LynxUI<FrameLayout>(context) {

    private var iconSet = "material"
    private var iconName = ""
    private var iconColor = Color.BLACK
    private var iconSizeSp = 24f
    /** Material Symbols FILL axis: 0 = outline, 1 = filled. Null = font default. */
    private var symbolFill: Float? = null

    private lateinit var imageView: ImageView

    override fun createView(context: Context): FrameLayout {
        Log.d("TamerIcon", "createView called")
        val container = FrameLayout(context).apply {
            clipChildren = false
            clipToPadding = false
        }
        imageView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setColorFilter(null)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                imageTintList = null
            }
        }
        container.addView(imageView, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))
        return container
    }

    private fun getTypeface(): Typeface? {
        return try {
            val assetPath = when (iconSet) {
                "fontawesome", "fa" -> "fonts/fa-solid-900.ttf"
                "fontawesome_brands", "fab" -> "fonts/fa-brands-400.ttf"
                "material_symbols" -> "fonts/MaterialSymbolsOutlined.ttf"
                else -> "fonts/MaterialIcons-Regular.ttf"
            }
            Typeface.createFromAsset(lynxContext.context.assets, assetPath)
        } catch (e: Exception) {
            null
        }
    }

    private fun resolveCodepoint(): Int {
        return when (iconSet) {
            "fontawesome", "fa" -> {
                val key = iconName.removePrefix("fa-").replace("_", "-")
                (IconCodepoints.FONTAWESOME[key] ?: IconCodepoints.FONTAWESOME[iconName])?.code ?: 0
            }
            "fontawesome_brands", "fab" -> {
                val key = iconName.removePrefix("fa-").replace("_", "-")
                (IconCodepoints.FONTAWESOME_BRANDS[key] ?: IconCodepoints.FONTAWESOME_BRANDS[iconName])?.code ?: 0
            }
            "material_symbols" -> {
                val map = IconCodepoints.getMaterialSymbols(lynxContext.context.assets)
                map[iconName]
                    ?: map[iconName.replace("_", "-")]
                    ?: map[iconName.replace("-", "_")]
                    ?: 0
            }
            else -> {
                val map = IconCodepoints.getMaterialClassic(lynxContext.context.assets)
                map[iconName]
                    ?: map[iconName.replace("_", "-")]
                    ?: map[iconName.replace("-", "_")]
                    ?: 0
            }
        }
    }

    private fun applyIcon() {
        if (!::imageView.isInitialized) return
        val typeface = getTypeface()
        if (typeface == null) {
            Log.w("TamerIcon", "typeface null for set=$iconSet icon=$iconName")
            return
        }
        val codepoint = resolveCodepoint()
        if (codepoint == 0) {
            Log.w("TamerIcon", "codepoint=0 for icon=$iconName set=$iconSet")
        }
        val sizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            iconSizeSp,
            imageView.context.resources.displayMetrics
        ).toInt().coerceAtLeast(1)
        val variation =
            if (iconSet == "material_symbols" && symbolFill != null) {
                "'FILL' ${symbolFill!!.roundToInt().coerceIn(0, 1)}"
            } else {
                null
            }
        val drawable = IconDrawable(typeface, codepoint, iconColor, sizePx, variation)
        imageView.setImageDrawable(drawable)
        imageView.clearColorFilter()
    }

    @LynxProp(name = "icon")
    fun setIcon(value: String) {
        iconName = value
        applyIcon()
    }

    @LynxProp(name = "set")
    fun setIconSet(value: String) {
        iconSet = value.lowercase()
        applyIcon()
    }

    @LynxProp(name = "iconColor")
    fun setIconColor(value: Dynamic) {
        applyColorFromDynamic(value)
    }

    private fun applyColorFromDynamic(value: Dynamic) {
        if (value.type == ReadableType.Null) return
        iconColor = when (value.type) {
            ReadableType.Number -> (value.asDouble().toLong() and 0xFFFFFFFFL).toInt()
            ReadableType.String -> parseColorString(value.asString())
            else -> return
        }
        if (::imageView.isInitialized) {
            (imageView.drawable as? IconDrawable)?.setColor(iconColor)
                ?: applyIcon()
        }
    }

    private fun expandHex(hex: String): String =
        if (hex.length == 3) hex.map { "$it$it" }.joinToString("") else hex

    private fun parseColorString(value: String): Int = try {
        when {
            value.startsWith("#") -> Color.parseColor("#${expandHex(value.removePrefix("#"))}")
            value.startsWith("rgb") -> parseRgb(value)
            else -> Color.parseColor(value)
        }
    } catch (_: Exception) {
        Color.BLACK
    }

    @LynxProp(name = "size")
    fun setSize(value: Double) {
        iconSizeSp = value.toFloat()
        applyIcon()
    }

    @LynxProp(name = "fill")
    fun setFill(value: Dynamic) {
        symbolFill = when (value.type) {
            ReadableType.Null -> null
            ReadableType.Number -> value.asDouble().toFloat().coerceIn(0f, 1f)
            else -> null
        }
        applyIcon()
    }

    private fun parseRgb(value: String): Int {
        val parts = value.replace(Regex("[^0-9,.]"), "").split(",")
        val nums = parts.map { it.toFloatOrNull() ?: 0f }
        return when (nums.size) {
            3 -> Color.rgb(nums[0].toInt(), nums[1].toInt(), nums[2].toInt())
            4 -> Color.argb((nums[3] * 255).toInt(), nums[0].toInt(), nums[1].toInt(), nums[2].toInt())
            else -> Color.BLACK
        }
    }

    override fun onLayoutUpdated() {
        super.onLayoutUpdated()
        val paddingTop = mPaddingTop + mBorderTopWidth
        val paddingBottom = mPaddingBottom + mBorderBottomWidth
        val paddingLeft = mPaddingLeft + mBorderLeftWidth
        val paddingRight = mPaddingRight + mBorderRightWidth
        mView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
    }
}
