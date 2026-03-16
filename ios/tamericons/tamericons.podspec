package = JSON.parse(File.read(File.join(__dir__, "..", "..", "package.json")))

Pod::Spec.new do |s|
  s.name             = 'tamericons'
  s.version          = package["version"]
  s.module_name      = 'tamericons'
  s.summary          = 'Native icon font element for Lynx on iOS.'
  s.description      = 'Registers the icon custom element and bundles font resources for iOS.'
  s.homepage         = 'https://github.com/nanofuxion/tamer4lynx'
  s.license          = package["license"]
  s.authors          = package["author"]
  s.source           = { :path => '.' }
  s.ios.deployment_target = '13.0'
  s.source_files     = 'tamericons/Classes/**/*.{h,m}'
  s.public_header_files = 'tamericons/Classes/**/*.h'
  s.resource_bundles = {
    'tamericons' => [
      'tamericons/Resources/MaterialSymbolsOutlined.ttf',
      'tamericons/Resources/fa-solid-900.ttf',
      'tamericons/Resources/material-codepoints.txt'
    ]
  }
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES' }
  s.dependency 'Lynx'
  s.requires_arc     = true
end
