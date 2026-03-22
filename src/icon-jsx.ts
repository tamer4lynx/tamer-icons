/// <reference types="@lynx-js/types" />
import type { ViewProps } from '@lynx-js/types'

export type IconElementProps = {
  icon: string
  set?: 'material' | 'fontawesome' | 'fa'
  iconColor?: string
  size?: number
} & ViewProps

declare module '@lynx-js/types' {
  interface IntrinsicElements {
    icon: IconElementProps
  }
}
