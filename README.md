# tamer-icons

Icon fonts for Lynx: Material Icons, Font Awesome. Tfont, Icon component.

## Installation

```bash
npm install @tamer4lynx/tamer-icons
```

Add to your app's dependencies and run `t4l link`.

## Usage

```tsx
import { Icon, Tfont, type IconSet } from '@tamer4lynx/tamer-icons'

// Material Icons (default)
<Icon name="home" size={24} color="#333" />

// Font Awesome
<Icon name="fa-home" set="fontawesome" size={24} />

// Custom icon font (register via Tfont)
<Tfont src="https://..." family="MyIcons" />
<Icon name="custom-icon" set="material" size={32} />
```

## API

| Component | Props | Description |
|-----------|-------|-------------|
| `Icon` | `name`, `set?`, `size?`, `color?`, `style?` | Renders icon. `set`: `'material'` \| `'fontawesome'` \| `'fa'` |
| `Tfont` | `src`, `family`, `weight?`, `style?` | Registers custom icon font |

| Export | Description |
|--------|-------------|
| `MATERIAL_ICONS_URL` | Material Icons font URL |
| `FONTAWESOME_SOLID_URL` | Font Awesome solid URL |
| `MATERIAL_CODEPOINTS` | Material icon codepoint map |

## Platform

Uses **lynx.ext.json**. Run `t4l link` after adding to your app. Requires native `<icon>` element support.
