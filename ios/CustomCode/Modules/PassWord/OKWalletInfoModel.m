//
//  OKWalletInfoModel.m
//  OneKey
//
//  Created by xiaoliang on 2020/12/7.
//  Copyright © 2020 OneKey. All rights reserved.
//

#import "OKWalletInfoModel.h"

@implementation OKWalletInfoModel
+ (OKWalletType)walletTypeWithStr:(NSString *)type {

    OKWalletType walletType = OKWalletTypeHD;
    #define OK_WALLET_TYPE_CASE(argTypeStr,argType) \
        else if([type ignoreCaseCointain:(argTypeStr)]) {walletType = (argType);}
    if ([type ignoreCaseCointain:@"hd-standard"]) {
        walletType = OKWalletTypeHD;
    }
    OK_WALLET_TYPE_CASE(@"derived-standard",    OKWalletTypeHD)
    OK_WALLET_TYPE_CASE(@"private-standard",    OKWalletTypeIndependent)
    OK_WALLET_TYPE_CASE(@"watch-standard",      OKWalletTypeObserve)
    OK_WALLET_TYPE_CASE(@"hw-derived-",         OKWalletTypeHardware)
    OK_WALLET_TYPE_CASE(@"hd-hw-",              OKWalletTypeHardware)
    OK_WALLET_TYPE_CASE(@"hw-",                 OKWalletTypeMultipleSignature)
    OK_WALLET_TYPE_CASE(@"standard",            OKWalletTypeIndependent)
    return walletType;
}

- (void)setType:(NSString *)type {
    if (type == _type || [type isEqualToString:_type]) {
        return;
    }
    _type = type;
    if ([type hasPrefix:@"eth"] ||
        [type hasPrefix:@"bsc"] ||
        [type hasPrefix:@"heco"]
        ) {
        self.chainType = OKWalletChainTypeETHLike;
    } else {
        self.chainType = OKWalletChainTypeBTC;
    }
    self.walletType = [OKWalletInfoModel walletTypeWithStr:type];
}

- (NSString *)walletTypeDesc {
    switch (self.walletType) {
        case OKWalletTypeHD:        return @"HD".localized; break;
        case OKWalletTypeHardware:  return @"hardware".localized; break;
        case OKWalletTypeObserve:   return @"Observation".localized; break;
        default: return @""; break;
    }
}

- (OKWalletCoinType)walletCoinType {
    NSString *coinType = self.type.lowercaseString;
    OKWalletCoinType type = OKWalletCoinTypeUnknown;
    if ([coinType hasPrefix:@"btc"]) {
        type = OKWalletCoinTypeBTC;
    } else if ([coinType hasPrefix:@"eth"]) {
        type = OKWalletCoinTypeETH;
    } else if ([coinType hasPrefix:@"bsc"]) {
        type = OKWalletCoinTypeBSC;
    } else if ([coinType hasPrefix:@"heco"]) {
        type = OKWalletCoinTypeHECO;
    }
    return type;
}

@end
