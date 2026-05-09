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
        "envelope" to '\uf0e0',
        "envelope-open" to '\uf2b6',
        "link" to '\uf0c1',
        "globe" to '\uf0ac',
        "comment" to '\uf075',
    )

    val FONTAWESOME_BRANDS: Map<String, Char> = mapOf(
        "github" to '\uf09b',
        "discord" to '\uf392',
        "twitter" to '\uf099',
        "x-twitter" to '\ue61b',
        "youtube" to '\uf167',
        "linkedin" to '\uf08c',
        "npm" to '\uf3d4',
        "apple" to '\uf179',
        "android" to '\uf17b',
        "google" to '\uf1a0',
    )

    @Volatile
    private var materialClassicCache: Map<String, Int>? = null

    @Volatile
    private var materialSymbolsCache: Map<String, Int>? = null

    fun getMaterialClassic(assets: android.content.res.AssetManager): Map<String, Int> {
        return materialClassicCache ?: synchronized(this) {
            materialClassicCache ?: loadMaterialFromAssets(assets, "fonts/material-icons-codepoints.txt").also {
                materialClassicCache = it
            }
        }
    }

    fun getMaterialSymbols(assets: android.content.res.AssetManager): Map<String, Int> {
        return materialSymbolsCache ?: synchronized(this) {
            materialSymbolsCache ?: loadMaterialFromAssets(assets, "fonts/material-symbols-codepoints.txt").also {
                materialSymbolsCache = it
            }
        }
    }

    private fun loadMaterialFromAssets(assets: android.content.res.AssetManager, path: String): Map<String, Int> {
        return try {
            assets.open(path).use { stream ->
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
        } catch (_: Exception) {
            emptyMap()
        }
    }
}
