/// <reference types="@lynx-js/react" />
import type { ViewProps } from '@lynx-js/types'

export { MATERIAL_ICONS_URL, FONTAWESOME_SOLID_URL } from './fonts'
export { MATERIAL_CODEPOINTS } from './material-codepoints'

export interface TfontProps {
  src: string
  family: string
  weight?: number
  style?: ViewProps['style']
}

export type IconSet = 'material' | 'fontawesome' | 'fa'

export interface IconProps extends ViewProps {
  name: string
  set?: IconSet
  size?: number
  color?: string
}

const px = (value: number) => `${Math.round(value)}px`

export function Tfont(props: TfontProps) {
  void props
  return null
}

export function Icon(props: IconProps) {
  const { name, set = 'material', size = 24, color, style, ...rest } = props
  return (
    <icon
      icon={name}
      set={set}
      size={size}
      iconColor={color}
      style={{
        width: px(size),
        height: px(size),
        ...(style as object),
      }}
      {...rest}
    />
  )
}
