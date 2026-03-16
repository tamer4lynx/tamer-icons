#import "TamerIconElement.h"

#import <CoreText/CoreText.h>
#import <Lynx/LynxPropsProcessor.h>

static UIColor *TamerIconParseColor(id value);
static NSString *gMaterialFontName = nil;
static NSString *gFontAwesomeFontName = nil;
static NSDictionary<NSString *, NSString *> *gMaterialCodepoints = nil;

@interface TamerIconHostView : UIView
@property(nonatomic, strong) UILabel *label;
@end

@implementation TamerIconHostView

- (instancetype)init {
  self = [super initWithFrame:CGRectZero];
  if (self) {
    self.backgroundColor = UIColor.clearColor;
    self.clipsToBounds = NO;

    _label = [[UILabel alloc] initWithFrame:CGRectZero];
    _label.backgroundColor = UIColor.clearColor;
    _label.textAlignment = NSTextAlignmentCenter;
    _label.numberOfLines = 1;
    _label.adjustsFontSizeToFitWidth = YES;
    _label.minimumScaleFactor = 0.1;
    [self addSubview:_label];
  }
  return self;
}

- (void)layoutSubviews {
  [super layoutSubviews];
  self.label.frame = self.bounds;
}

@end

@interface TamerIconElement ()
@property(nonatomic, copy) NSString *iconSet;
@property(nonatomic, copy) NSString *iconName;
@property(nonatomic, strong) UIColor *iconColor;
@property(nonatomic, assign) CGFloat iconSize;
@property(nonatomic, strong) NSDictionary<NSString *, NSString *> *fontAwesomeCodepoints;
@property(nonatomic, copy) NSString *materialFontName;
@property(nonatomic, copy) NSString *fontAwesomeFontName;
@end

@implementation TamerIconElement

- (instancetype)init {
  self = [super init];
  if (self) {
    _iconSet = @"material";
    _iconName = @"";
    _iconColor = UIColor.blackColor;
    _iconSize = 24.0;
    _fontAwesomeCodepoints = @{
      @"search": @"\uf002",
      @"home": @"\uf015",
      @"bars": @"\uf0c9",
      @"arrow-left": @"\uf060",
      @"xmark": @"\uf00d",
      @"close": @"\uf00d",
      @"plus": @"\uf067",
      @"minus": @"\uf068",
      @"cog": @"\uf013",
      @"user": @"\uf007",
      @"heart": @"\uf004",
      @"share": @"\uf064",
      @"trash": @"\uf1f8",
      @"pen": @"\uf304",
      @"check": @"\uf00c",
      @"info": @"\uf129",
      @"exclamation-triangle": @"\uf071",
      @"circle-xmark": @"\uf057"
    };
    _materialFontName = @"Material Symbols Outlined";
    _fontAwesomeFontName = @"Font Awesome 6 Free-Solid";
  }
  return self;
}

- (UIView *)createView {
  TamerIconHostView *view = [[TamerIconHostView alloc] init];
  [self registerFontsIfNeeded];
  [TamerIconElement ensureCodepointsLoaded];
  [self applyIcon];
  return view;
}

+ (void)ensureCodepointsLoaded {
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{
    gMaterialCodepoints = [self loadMaterialCodepointsFromBundle];
    NSLog(@"[TamerIcon] loaded %lu material codepoints", (unsigned long)gMaterialCodepoints.count);
  });
}

+ (NSBundle *)tamerBundle {
  NSBundle *classBundle = [NSBundle bundleForClass:self];
  NSURL *url = [classBundle URLForResource:@"tamericons" withExtension:@"bundle"];
  return url ? [NSBundle bundleWithURL:url] : classBundle;
}

+ (NSDictionary<NSString *, NSString *> *)loadMaterialCodepointsFromBundle {
  NSBundle *bundle = [self tamerBundle];
  NSURL *url = [bundle URLForResource:@"material-codepoints" withExtension:@"txt"];
  if (url == nil) {
    NSLog(@"[TamerIcon] material-codepoints.txt not found in bundle");
    return @{};
  }
  NSString *content = [NSString stringWithContentsOfURL:url encoding:NSUTF8StringEncoding error:nil];
  if (content.length == 0) return @{};
  NSMutableDictionary<NSString *, NSString *> *map = [NSMutableDictionary dictionary];
  [content enumerateLinesUsingBlock:^(NSString * _Nonnull line, BOOL * _Nonnull stop) {
    NSRange spaceRange = [line rangeOfString:@" "];
    if (spaceRange.location == NSNotFound || spaceRange.location == 0) return;
    NSString *name = [line substringToIndex:spaceRange.location];
    NSString *hex = [[line substringFromIndex:spaceRange.location + 1] stringByTrimmingCharactersInSet:NSCharacterSet.whitespaceCharacterSet];
    unsigned int codepoint = 0;
    if ([[NSScanner scannerWithString:hex] scanHexInt:&codepoint]) {
      UTF32Char scalar = (UTF32Char)codepoint;
      NSString *glyph = [[NSString alloc] initWithBytes:&scalar length:sizeof(scalar) encoding:NSUTF32LittleEndianStringEncoding];
      if (glyph.length > 0) {
        map[name] = glyph;
      }
    }
  }];
  return [map copy];
}

- (void)layoutDidFinished {
  [super layoutDidFinished];
  [self applyIcon];
}

LYNX_PROP_SETTER("icon", setIconProp, NSString *) {
  self.iconName = value ?: @"";
  [self applyIcon];
}

LYNX_PROP_SETTER("set", setSetProp, NSString *) {
  self.iconSet = value.length > 0 ? value.lowercaseString : @"material";
  [self applyIcon];
}

LYNX_PROP_SETTER("iconColor", setIconColorProp, id) {
  UIColor *color = TamerIconParseColor(value);
  if (color != nil) {
    self.iconColor = color;
    [self applyIcon];
  }
}

LYNX_PROP_SETTER("size", setSizeProp, NSNumber *) {
  CGFloat next = value != nil ? value.doubleValue : 24.0;
  self.iconSize = next > 0 ? next : 24.0;
  [self applyIcon];
}

- (void)applyIcon {
  if (![self.view isKindOfClass:[TamerIconHostView class]]) return;

  TamerIconHostView *hostView = (TamerIconHostView *)self.view;
  NSString *glyph = [self resolveGlyph];
  hostView.label.text = glyph;
  hostView.label.textColor = self.iconColor;

  BOOL isFA = [self.iconSet isEqualToString:@"fontawesome"] || [self.iconSet isEqualToString:@"fa"];
  NSString *fontName = isFA ? self.fontAwesomeFontName : self.materialFontName;
  UIFont *font = [UIFont fontWithName:fontName size:self.iconSize];

  if (font == nil && !isFA) {
    font = [UIFont fontWithName:@"MaterialSymbolsOutlined-Regular" size:self.iconSize];
  }
  if (font == nil && !isFA) {
    font = [UIFont fontWithName:@"Material Symbols Outlined" size:self.iconSize];
  }
  if (font == nil && isFA) {
    font = [UIFont fontWithName:@"FontAwesome6Free-Solid" size:self.iconSize];
  }

  if (font == nil) {
    [self registerFontsIfNeeded];
    font = [UIFont fontWithName:self.materialFontName size:self.iconSize];
  }

  if (font == nil) {
    NSLog(@"[TamerIcon] ERR no font for icon='%@' set='%@'", self.iconName, self.iconSet);
    font = [UIFont systemFontOfSize:self.iconSize weight:UIFontWeightRegular];
  }

  hostView.label.font = font;
  hostView.label.adjustsFontSizeToFitWidth = isFA;
  [hostView setNeedsLayout];
}

- (NSString *)resolveGlyph {
  if (self.iconName.length == 0) return @"";

  if ([self.iconSet isEqualToString:@"fontawesome"] || [self.iconSet isEqualToString:@"fa"]) {
    NSString *normalized = [[self.iconName stringByReplacingOccurrencesOfString:@"_" withString:@"-"] lowercaseString];
    NSString *trimmed = [normalized hasPrefix:@"fa-"] ? [normalized substringFromIndex:3] : normalized;
    return self.fontAwesomeCodepoints[trimmed] ?: self.fontAwesomeCodepoints[normalized] ?: @"";
  }

  NSString *name = self.iconName;
  NSDictionary *codepoints = gMaterialCodepoints ?: @{};
  NSString *result = codepoints[name];
  if (result == nil) {
    result = codepoints[[name stringByReplacingOccurrencesOfString:@"_" withString:@"-"]];
  }
  if (result == nil) {
    result = codepoints[[name stringByReplacingOccurrencesOfString:@"-" withString:@"_"]];
  }
  return result ?: @"";
}

+ (void)registerFonts {
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{
    NSBundle *bundle = [self tamerBundle];
    NSArray<NSString *> *resources = @[@"MaterialSymbolsOutlined", @"fa-solid-900"];
    for (NSString *resource in resources) {
      NSURL *url = [bundle URLForResource:resource withExtension:@"ttf"];
      if (url == nil) {
        NSLog(@"[TamerIcon] font resource NOT FOUND: %@.ttf", resource);
        continue;
      }
      CFErrorRef cfError = NULL;
      CTFontManagerRegisterFontsForURL((__bridge CFURLRef)url, kCTFontManagerScopeProcess, &cfError);
      if (cfError) CFRelease(cfError);
      CGDataProviderRef provider = CGDataProviderCreateWithURL((__bridge CFURLRef)url);
      if (provider == nil) continue;
      CGFontRef fontRef = CGFontCreateWithDataProvider(provider);
      CGDataProviderRelease(provider);
      if (fontRef == nil) continue;
      NSString *postScriptName = (__bridge_transfer NSString *)CGFontCopyPostScriptName(fontRef);
      CGFontRelease(fontRef);
      if (postScriptName.length > 0 && [UIFont fontWithName:postScriptName size:24] != nil) {
        if ([resource isEqualToString:@"MaterialSymbolsOutlined"]) {
          gMaterialFontName = postScriptName;
        } else if ([resource isEqualToString:@"fa-solid-900"]) {
          gFontAwesomeFontName = postScriptName;
        }
      }
    }
  });
}

- (void)registerFontsIfNeeded {
  [TamerIconElement registerFonts];
  [TamerIconElement ensureCodepointsLoaded];
  if (gMaterialFontName.length > 0) self.materialFontName = gMaterialFontName;
  if (gFontAwesomeFontName.length > 0) self.fontAwesomeFontName = gFontAwesomeFontName;
}

@end

static UIColor *TamerIconParseColor(id value) {
  if ([value isKindOfClass:[NSNumber class]]) {
    uint32_t raw = ((NSNumber *)value).unsignedIntValue;
    CGFloat alpha = ((raw >> 24) & 0xFF) / 255.0;
    CGFloat red = ((raw >> 16) & 0xFF) / 255.0;
    CGFloat green = ((raw >> 8) & 0xFF) / 255.0;
    CGFloat blue = (raw & 0xFF) / 255.0;
    return [UIColor colorWithRed:red green:green blue:blue alpha:alpha > 0 ? alpha : 1];
  }

  if (![value isKindOfClass:[NSString class]]) return nil;
  NSString *string = [(NSString *)value stringByTrimmingCharactersInSet:NSCharacterSet.whitespaceAndNewlineCharacterSet];
  if (string.length == 0) return nil;

  if ([string hasPrefix:@"#"]) {
    NSString *hex = [string substringFromIndex:1];
    if (hex.length == 3) {
      hex = [NSString stringWithFormat:@"%C%C%C%C%C%C",
              [hex characterAtIndex:0], [hex characterAtIndex:0],
              [hex characterAtIndex:1], [hex characterAtIndex:1],
              [hex characterAtIndex:2], [hex characterAtIndex:2]];
    }
    unsigned int valueInt = 0;
    if ([[NSScanner scannerWithString:hex] scanHexInt:&valueInt]) {
      return [UIColor colorWithRed:((valueInt >> 16) & 0xFF) / 255.0
                             green:((valueInt >> 8) & 0xFF) / 255.0
                              blue:(valueInt & 0xFF) / 255.0
                             alpha:1];
    }
  }

  if ([string hasPrefix:@"rgb"]) {
    NSString *numbers = [[string componentsSeparatedByCharactersInSet:[[NSCharacterSet characterSetWithCharactersInString:@"0123456789.,"] invertedSet]] componentsJoinedByString:@""];
    NSArray<NSString *> *parts = [numbers componentsSeparatedByString:@","];
    if (parts.count == 3 || parts.count == 4) {
      CGFloat red = parts[0].doubleValue / 255.0;
      CGFloat green = parts[1].doubleValue / 255.0;
      CGFloat blue = parts[2].doubleValue / 255.0;
      CGFloat alpha = parts.count == 4 ? parts[3].doubleValue : 1.0;
      return [UIColor colorWithRed:red green:green blue:blue alpha:alpha];
    }
  }

  return nil;
}
