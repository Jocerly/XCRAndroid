//
//  CDVLogicService.m
//  HelloCordova
//
//  Created by LiXiang on 2017/7/18.
//
//

#import "CDVLogicService.h"

static NSString * _userId = nil;
static NSString * _userName = nil;
static NSString * _cityName = nil;
static NSString * _avatarUrl = nil;
static NSString * _ipAddress = nil;

@implementation CDVLogicService

- (void)fetchCommonParams:(CDVInvokedUrlCommand *)command {
    NSMutableDictionary *dic = [NSMutableDictionary dictionary];
    if (CDVLogicService.userId) {
        dic[@"userId"] = CDVLogicService.userId;
    }
    if (CDVLogicService.userName) {
        dic[@"userName"] = CDVLogicService.userName;
    }
    if (CDVLogicService.cityName) {
        dic[@"cityName"] = CDVLogicService.cityName;
    }
    if (CDVLogicService.avatarUrl) {
        dic[@"avatarUrl"] = CDVLogicService.avatarUrl;
    }
    if (CDVLogicService.ipAddress) {
        dic[@"ip"] = CDVLogicService.ipAddress;
    }
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:dic];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)handleError:(CDVInvokedUrlCommand *)command {
    NSString *errCode = [command argumentAtIndex:0];
    if (errCode) {
        [[NSNotificationCenter defaultCenter] postNotificationName:kCDVLogicServiceErrorNotification object:errCode];
    }
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

+ (NSString *)userId {
    return _userId;
}

+ (void)setUserId:(NSString *)userId {
    if (_userId != userId) {
        _userId = userId;
    }
}

+ (NSString *)userName {
    return _userName;
}

+ (void)setUserName:(NSString *)userName {
    if (_userName != userName) {
        _userName = userName;
    }
}

+ (NSString *)cityName {
    return _cityName;
}

+ (void)setCityName:(NSString *)cityName {
    if (_cityName != cityName) {
        _cityName = cityName;
    }
}

+ (NSString *)avatarUrl {
    return _avatarUrl;
}

+ (void)setAvatarUrl:(NSString *)avatarUrl {
    if (_avatarUrl != avatarUrl) {
        _avatarUrl = avatarUrl;
    }
}

+ (NSString *)ipAddress {
    return _ipAddress;
}

+ (void)setIpAddress:(NSString *)ipAddress {
    if (_ipAddress != ipAddress) {
        _ipAddress = ipAddress;
    }
}


@end