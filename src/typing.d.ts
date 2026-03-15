import type { TextProps, ViewProps } from '@lynx-js/types'

declare module '@lynx-js/react/jsx-runtime' {
  namespace JSX {
    interface IntrinsicElements {
      text: TextProps
      view: ViewProps
    }
  }
}
