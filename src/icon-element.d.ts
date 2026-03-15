declare module '@lynx-js/react/jsx-runtime' {
  namespace JSX {
    interface IntrinsicElements {
      view: Record<string, unknown>
      icon: {
        icon: string
        set?: 'material' | 'fontawesome' | 'fa'
        iconColor?: string
        size?: number
        style?: unknown
      }
    }
  }
}
