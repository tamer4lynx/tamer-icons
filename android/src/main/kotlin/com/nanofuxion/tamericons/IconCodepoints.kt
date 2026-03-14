package com.nanofuxion.tamericons

import java.io.BufferedReader
import java.io.InputStreamReader

object IconCodepoints {

    val FONTAWESOME: Map<String, Char> = mapOf(
        "search" to '\uf002',
        "home" to '\uf015',
        "bars" to '\uf0c9',
        "arrow-left" to '\uf060',
        "xmark" to '\uf00d',
        "close" to '\uf00d',
        "plus" to '\uf067',
        "minus" to '\uf068',
        "cog" to '\uf013',
        "user" to '\uf007',
        "heart" to '\uf004',
        "share" to '\uf064',
        "trash" to '\uf1f8',
        "pen" to '\uf304',
        "check" to '\uf00c',
        "info" to '\uf129',
        "exclamation-triangle" to '\uf071',
        "circle-xmark" to '\uf057',
    )

    @Volatile
    private var materialCache: Map<String, Int>? = null

    fun getMaterial(assets: android.content.res.AssetManager): Map<String, Int> {
        return materialCache ?: synchronized(this) {
            materialCache ?: loadMaterialFromAssets(assets).also { materialCache = it }
        }
    }

    private fun loadMaterialFromAssets(assets: android.content.res.AssetManager): Map<String, Int> {
        return try {
            assets.open("fonts/material-codepoints.txt").use { stream ->
                BufferedReader(InputStreamReader(stream)).use { reader ->
                    buildMap {
                        reader.lineSequence().forEach { line ->
                            val space = line.indexOf(' ')
                            if (space > 0) {
                                val name = line.substring(0, space)
                                val hex = line.substring(space + 1).trim()
                                if (hex.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }) {
                                    put(name, hex.toInt(16))
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
