# tamer-icons

Icon fonts for Lynx: Material Icons, Font Awesome. Provides TypeScript typings for the native `<icon>` element, font URLs, and codepoint data.

## Installation

```bash
npm install @tamer4lynx/tamer-icons
```

Add to your app's dependencies and run `t4l link`.

## Usage

Import the package once so JSX knows the `<icon>` intrinsic (or rely on `.tamer/tamer-components.d.ts` from `t4l init` / `t4l link`):

```tsx
import '@tamer4lynx/tamer-icons'

<icon
  icon="home"
  set="material"
  size={24}
  iconColor="#333"
  style={{ width: '24px', height: '24px' }}
/>

<icon icon="fa-home" set="fontawesome" size={24} iconColor="#333" style={{ width: '24px', height: '24px' }} />
```

| Attribute | Description |
|-----------|-------------|
| `icon` | Icon name / codepoint key |
| `set` | `'material'` \| `'fontawesome'` \| `'fa'` |
| `size` | Number (optional; pair with `style` width/height as needed) |
| `iconColor` | Color string |
| `style` | Lynx `ViewProps` style (e.g. width/height) |

## Exports

| Export | Description |
|--------|-------------|
| `IconElementProps` | Props type for `<icon>` |
| `IconSet` | `'material'` \| `'fontawesome'` \| `'fa'` |
| `MATERIAL_ICONS_URL` | Material Icons font URL |
| `FONTAWESOME_SOLID_URL` | Font Awesome solid URL |
| `MATERIAL_CODEPOINTS` | Material icon codepoint map |

## Platform

Uses **lynx.ext.json**. Run `t4l link` after adding to your app. Requires native `<icon>` element support.
