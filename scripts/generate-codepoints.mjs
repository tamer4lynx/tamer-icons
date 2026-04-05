#!/usr/bin/env node
import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const pkgDir = path.resolve(__dirname, '..')
const MATERIAL_SYMBOLS_VERSION = '2026-03-14'

const SYMBOLS_CODEPOINTS_URL =
  'https://raw.githubusercontent.com/google/material-design-icons/refs/heads/master/variablefont/MaterialSymbolsOutlined%5BFILL%2CGRAD%2Copsz%2Cwght%5D.codepoints'
const CLASSIC_CODEPOINTS_URL =
  'https://raw.githubusercontent.com/google/material-design-icons/master/font/MaterialIcons-Regular.codepoints'

async function fetchTextCached(url, cacheFile) {
  const cacheDir = path.dirname(cacheFile)
  if (fs.existsSync(cacheFile)) {
    return fs.readFileSync(cacheFile, 'utf8')
  }
  const res = await fetch(url)
  if (!res.ok) throw new Error(`Failed to fetch ${url}: ${res.status}`)
  const text = await res.text()
  fs.mkdirSync(cacheDir, { recursive: true })
  fs.writeFileSync(cacheFile, text)
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

const symbolsCache = path.join(
  pkgDir,
  '.cache',
  'tamer-icons',
  MATERIAL_SYMBOLS_VERSION,
  'material-symbols-codepoints.raw.txt'
)
const classicCache = path.join(
  pkgDir,
  '.cache',
  'tamer-icons',
  'material-icons-classic',
  'MaterialIcons-Regular.codepoints'
)

const symbolsText = await fetchTextCached(SYMBOLS_CODEPOINTS_URL, symbolsCache)
const classicText = await fetchTextCached(CLASSIC_CODEPOINTS_URL, classicCache)

const symbolsLines = parseCodepoints(symbolsText)
const classicLines = parseCodepoints(classicText)

const assetsFonts = path.join(pkgDir, 'android/src/main/assets/fonts')
const iosResources = path.join(pkgDir, 'ios/tamericons/tamericons/Resources')
fs.mkdirSync(assetsFonts, { recursive: true })
fs.mkdirSync(iosResources, { recursive: true })

const symOut = symbolsLines.join('\n')
const clsOut = classicLines.join('\n')

fs.writeFileSync(path.join(assetsFonts, 'material-symbols-codepoints.txt'), symOut)
fs.writeFileSync(path.join(assetsFonts, 'material-icons-codepoints.txt'), clsOut)
fs.writeFileSync(path.join(iosResources, 'material-symbols-codepoints.txt'), symOut)
fs.writeFileSync(path.join(iosResources, 'material-icons-codepoints.txt'), clsOut)

console.log(
  `Wrote material-icons-codepoints.txt (${classicLines.length}) and material-symbols-codepoints.txt (${symbolsLines.length})`
)

for (const stale of [
  path.join(assetsFonts, 'material-codepoints.txt'),
  path.join(iosResources, 'material-codepoints.txt'),
]) {
  try {
    fs.unlinkSync(stale)
  } catch {
    /* ignore */
  }
}
