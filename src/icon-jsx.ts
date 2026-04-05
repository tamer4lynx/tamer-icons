/// <reference types="@lynx-js/types" />
import type { ViewProps } from '@lynx-js/types'

export type IconElementProps = {
  icon: string
  set?: 'material' | 'material_symbols' | 'fontawesome' | 'fa'
  iconColor?: string
  size?: number
  /** Material Symbols only: FILL axis 0 = outline, 1 = filled. Omit for default. */
  fill?: number
} & ViewProps

declare module '@lynx-js/types' {
  interface IntrinsicElements {
    icon: IconElementProps
  }
}
