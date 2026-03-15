#!/usr/bin/env node
import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const pkgDir = path.resolve(__dirname, '..')
const MATERIAL_SYMBOLS_VERSION = '2026-03-14'
const codepointsUrl =
  'https://raw.githubusercontent.com/google/material-design-icons/refs/heads/master/variablefont/MaterialSymbolsOutlined%5BFILL%2CGRAD%2Copsz%2Cwght%5D.codepoints'

async function fetchCodepoints() {
  const cachePath = path.join(pkgDir, '.cache', 'tamer-icons', MATERIAL_SYMBOLS_VERSION, 'material-codepoints.txt')
  const cacheDir = path.dirname(cachePath)
  if (fs.existsSync(cachePath)) {
    return fs.readFileSync(cachePath, 'utf8')
  }
  const res = await fetch(codepointsUrl)
  if (!res.ok) throw new Error(`Failed to fetch codepoints: ${res.status}`)
  const text = await res.text()
  fs.mkdirSync(cacheDir, { recursive: true })
  fs.writeFileSync(cachePath, text)
  return text
}

function parseCodepoints(text) {
  const lines = []
  for (const line of text.split('\n')) {
    const trimmed = line.trim()
    if (!trimmed) continue
    const space = trimmed.indexOf(' ')
    if (space <= 0) continue
    const name = trimmed.slice(0, space)
    const hex = trimmed.slice(space + 1).trim()
    if (!/^[a-f0-9]+$/i.test(hex)) continue
    lines.push(`${name} ${hex}`)
  }
  return lines
}

const text = await fetchCodepoints()
const lines = parseCodepoints(text)
const assetsFonts = path.join(pkgDir, 'android/src/main/assets/fonts')
fs.mkdirSync(assetsFonts, { recursive: true })
fs.writeFileSync(path.join(assetsFonts, 'material-codepoints.txt'), lines.join('\n'))
console.log(`Wrote material-codepoints.txt (${lines.length} icons)`)
