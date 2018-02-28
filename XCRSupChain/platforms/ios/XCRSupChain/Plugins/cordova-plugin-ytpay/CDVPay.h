//
//  CDVPay.h
//  HelloCordova
//
//  Created by LiXiang on 2017/7/18.
//
//

#import <Cordova/CDVPlugin.h>

@interface CDVPay : CDVPlugin

/* 支付宝支付 */
- (void)alipay:(CDVInvokedUrlCommand *)command;

/* 微信支付 */
- (void)wxpay:(CDVInvokedUrlCommand *)command;

@end
