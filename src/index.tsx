/// <reference types="@lynx-js/react" />
import './fonts.css'
import type { ViewProps } from '@lynx-js/types'

export { MATERIAL_ICONS_URL, FONTAWESOME_SOLID_URL } from './fonts'

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

export function Tfont(props: TfontProps) {
  const { src, family, weight, style } = props
  return null
}

export function Icon(props: IconProps) {
  const { name, set = 'material', size = 24, color, style, ...rest } = props
  const px = typeof size === 'number' ? size : 24
  const iconStyle = {
    width: px,
    height: px,
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    overflow: 'visible' as const,
    ...(style as object),
  }
  return (
    <view style={iconStyle} {...rest}>
      <icon
        icon={name}
        set={set}
        {...(color && { iconColor: color })}
        size={px}
        style={{ width: px, height: px }}
      />
    </view>
  )
}
