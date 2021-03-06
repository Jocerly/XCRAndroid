//
//  CDVLogicService.h
//  HelloCordova
//
//  Created by LiXiang on 2017/7/18.
//
//

#import <Cordova/CDVPlugin.h>

static NSString * kCDVLogicServiceErrorNotification = @"kCDVLogicServiceErrorNotification";

@interface CDVLogicService : CDVPlugin

@property(nonatomic, class) NSString *userId;
@property(nonatomic, class) NSString *userName;
@property(nonatomic, class) NSString *cityName;
@property(nonatomic, class) NSString *avatarUrl;
@property(nonatomic, class) NSString *ipAddress;

/* 获取公共参数 */
- (void)fetchCommonParams:(CDVInvokedUrlCommand *)command;
/* 处理错误 */
- (void)handleError:(CDVInvokedUrlCommand *)command;

@end