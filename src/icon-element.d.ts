declare module '@lynx-js/react/jsx-runtime' {
  namespace JSX {
    interface IntrinsicElements {
      icon: {
        icon: string
        set?: 'material' | 'fontawesome' | 'fa'
        iconColor?: string
        size?: number
        style?: Record<string, unknown>
      }
    }
  }
}
