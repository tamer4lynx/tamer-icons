import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const pkgDir = path.resolve(__dirname, '..')
const fontsDir = path.join(pkgDir, 'fonts')
const MATERIAL_SYMBOLS_VERSION = '2026-03-14'

const MATERIAL_SYMBOLS_URL =
  'https://github.com/google/material-design-icons/raw/refs/heads/master/variablefont/MaterialSymbolsOutlined%5BFILL%2CGRAD%2Copsz%2Cwght%5D.ttf'
const FA_URL = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/webfonts/fa-solid-900.ttf'

async function fetchToBuffer(url) {
  const res = await fetch(url)
  if (!res.ok) throw new Error(`Failed to fetch ${url}: ${res.status}`)
  return Buffer.from(await res.arrayBuffer())
}

async function fetchWithCache(url, destPath, cachePath) {
  if (fs.existsSync(cachePath)) {
    fs.copyFileSync(cachePath, destPath)
    return
  }
  const buf = await fetchToBuffer(url)
  fs.mkdirSync(path.dirname(cachePath), { recursive: true })
  fs.writeFileSync(cachePath, buf)
  fs.copyFileSync(cachePath, destPath)
}

const cacheDir = path.join(pkgDir, '.cache', 'tamer-icons', MATERIAL_SYMBOLS_VERSION)
fs.mkdirSync(fontsDir, { recursive: true })
await fetchWithCache(
  MATERIAL_SYMBOLS_URL,
  path.join(fontsDir, 'MaterialSymbolsOutlined.ttf'),
  path.join(cacheDir, 'MaterialSymbolsOutlined.ttf')
)
await fetchWithCache(
  FA_URL,
  path.join(fontsDir, 'fa-solid-900.ttf'),
  path.join(cacheDir, 'fa-solid-900.ttf')
)
console.log('Fonts ready in', fontsDir)
