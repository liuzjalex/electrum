//
//  NSDictionary+OKKeyPath.h
//  OneKey
//
//  Created by liuzj on 2021/1/11.
//  Copyright © 2021 Onekey. All rights reserved.
//

#import <Foundation/Foundation.h>


extern const NSString *OKKeyPathQueryPattern;

@interface NSDictionary (OKKeyPath)
- (id)ok_objectForKeyPath:(NSString *)keyPath;

@end

