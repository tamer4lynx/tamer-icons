import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const pkgDir = path.resolve(__dirname, '..')
const fontsDir = path.join(pkgDir, 'fonts')

const MATERIAL_URL = 'https://github.com/google/material-design-icons/raw/refs/heads/master/font/MaterialIcons-Regular.ttf'
const FA_URL = 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/webfonts/fa-solid-900.ttf'

async function fetchToBuffer(url) {
  const res = await fetch(url)
  if (!res.ok) throw new Error(`Failed to fetch ${url}: ${res.status}`)
  return Buffer.from(await res.arrayBuffer())
}

fs.mkdirSync(fontsDir, { recursive: true })
await fetchToBuffer(MATERIAL_URL).then((b) => fs.writeFileSync(path.join(fontsDir, 'MaterialIcons-Regular.ttf'), b))
await fetchToBuffer(FA_URL).then((b) => fs.writeFileSync(path.join(fontsDir, 'fa-solid-900.ttf'), b))
console.log('Fonts fetched to', fontsDir)
