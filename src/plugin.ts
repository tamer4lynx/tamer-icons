import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'
import type { RsbuildPlugin } from '@rsbuild/core'
import { MATERIAL_ICONS_URL, FONTAWESOME_SOLID_URL } from './fonts'

const __dirname = path.dirname(fileURLToPath(import.meta.url))

async function fetchToBuffer(url: string): Promise<Buffer> {
  const res = await fetch(url)
  if (!res.ok) throw new Error(`Failed to fetch ${url}: ${res.status}`)
  const arr = await res.arrayBuffer()
  return Buffer.from(arr)
}

async function ensureFonts(pkgDir: string): Promise<void> {
  const fontsDir = path.join(pkgDir, 'fonts')
  fs.mkdirSync(fontsDir, { recursive: true })

  const materialPath = path.join(fontsDir, 'MaterialSymbolsOutlined.ttf')
  const faPath = path.join(fontsDir, 'fa-solid-900.ttf')

  const cacheDir = path.join(pkgDir, '.cache', 'tamer-icons')
  fs.mkdirSync(cacheDir, { recursive: true })
  const materialCache = path.join(cacheDir, 'MaterialSymbolsOutlined.ttf')
  const faCache = path.join(cacheDir, 'fa-solid-900.ttf')

  if (!fs.existsSync(materialCache)) {
    const buf = await fetchToBuffer(MATERIAL_ICONS_URL)
    fs.writeFileSync(materialCache, buf)
  }
  fs.copyFileSync(materialCache, materialPath)

  if (!fs.existsSync(faCache)) {
    const buf = await fetchToBuffer(FONTAWESOME_SOLID_URL)
    fs.writeFileSync(faCache, buf)
  }
  fs.copyFileSync(faCache, faPath)
}

export function pluginTamerIcons(): RsbuildPlugin {
  return {
    name: 'tamer-icons',
    async setup(api) {
      const pkgDir = path.resolve(__dirname, '..')
      if (fs.existsSync(path.join(pkgDir, 'package.json'))) {
        await ensureFonts(pkgDir)
      }
    },
  }
}
