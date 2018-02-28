//
//  CDVNotification.h
//  XCRSupChain
//
//  Created by LiXiang on 2017/8/12.
//
//

#import <Cordova/CDVPlugin.h>

@interface CDVNotification : CDVPlugin

//监听通知
- (void)listen:(CDVInvokedUrlCommand *)command;

//发送通知
- (void)emit:(CDVInvokedUrlCommand *)command;

@end
