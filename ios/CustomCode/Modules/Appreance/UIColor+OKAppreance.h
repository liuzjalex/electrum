//
//  UIColor+OKAppreance.h
//  OneKey
//
//  Created by zj on 2021/2/24.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIColor (OKAppreance)
// Background
// 背景色为无透明度的实色，用于页面（Page）或视图（View）的底色填充。

+ (UIColor *)BG_W00; // FFFFFF
+ (UIColor *)BG_W01; // F9F9FB
+ (UIColor *)BG_W02; // F2F2F7

// Foreground
// 前景色有时含透明度，用于背景或视图中的文本、图标的颜色填充。

+ (UIColor *)FG_B00; // 1A1A1A
+ (UIColor *)FG_B01; // 3C3C43 .6
+ (UIColor *)FG_B02; // 3C3C43 .3
+ (UIColor *)FG_B03; // 3C3C43 .2
+ (UIColor *)FG_W01; // FFFFFF
+ (UIColor *)FG_W02; // FFFFFF .6
+ (UIColor *)FG_W03; // FFFFFF .3

// Tint
// 页面中的强调色，宁缺毋滥。

+ (UIColor *)TintBrand; // 00B812
+ (UIColor *)TintGreen; // 34C759
+ (UIColor *)TintRed;   // FF3B30
+ (UIColor *)TintYellow;    // FFCC00
+ (UIColor *)TintBlue;  // 007AFF

// Special
// 特殊色用于协助类组件（如分隔线、遮罩、把手）的颜色填充。

+ (UIColor *)SP_Separator;  // 3C3C43 .12
+ (UIColor *)SP_NavBarSeparator;  // CECED3


//Chain
//App 中主链或代币的主题色。
+(UIColor *)CG_BTC;
+(UIColor *)CG_ETH;
+(UIColor *)CG_BSC;
+(UIColor *)CG_HECO;

@end

NS_ASSUME_NONNULL_END
