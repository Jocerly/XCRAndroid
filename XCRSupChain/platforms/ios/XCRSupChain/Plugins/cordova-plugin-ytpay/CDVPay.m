//
//  CDVPay.m
//  HelloCordova
//
//  Created by LiXiang on 2017/7/18.
//
//

#import "CDVPay.h"
#import <AlipaySDK/AlipaySDK.h>
#import "WXApi.h"

#define kAlipayHost @"safepay"

@interface CDVPay () <WXApiDelegate>

@property(nonatomic) NSString *callbackId;

@end

@implementation CDVPay
- (void)alipay:(CDVInvokedUrlCommand *)command {
    ///签名后的订单信息
    NSString *payInfo = [command argumentAtIndex:0];
    if (payInfo) {
        [[AlipaySDK defaultService] payOrder:payInfo
                                  fromScheme:@"xcralipay"
                                    callback:^(NSDictionary *resultDic) {
                                        NSString *resultStatus=[NSString stringWithFormat:@"%@",resultDic[@"resultStatus"]];
                                        if ([resultStatus isEqualToString:@"9000"]) {
                                            CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultDic];
                                            [self.commandDelegate sendPluginResult:result callbackId:self.callbackId];
                                        } else {
                                            CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:resultStatus];
                                            [self.commandDelegate sendPluginResult:result callbackId:self.callbackId];
                                        }
        }];
        self.callbackId = command.callbackId;
    } else {
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    }
}

- (void)wxpay:(CDVInvokedUrlCommand *)command {
    self.callbackId = command.callbackId;
    NSDictionary *wxPayDic = [command argumentAtIndex:0];
    PayReq *request = [[PayReq alloc] init];
    /** 商家向财付通申请的商家id */
    request.partnerId = wxPayDic[@"partnerId"];
    /** 预支付订单 */
    request.prepayId= wxPayDic[@"prepayId"];
    /** 商家根据财付通文档填写的数据和签名 */
    request.package = wxPayDic[@"package"];
    /** 随机串，防重发 */
    request.nonceStr = wxPayDic[@"noncestr"];
    /** 时间戳，防重发 */
    request.timeStamp = [wxPayDic[@"timestamp"] intValue];
    /** 商家根据微信开放平台文档对数据做的签名 */
    request.sign = wxPayDic[@"sign"];
    if (request.partnerId == nil ||
        request.prepayId == nil ||
        request.package == nil ||
        request.nonceStr == nil ||
        request.sign == nil) {
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"参数有误!"];
        [self.commandDelegate sendPluginResult:result callbackId:self.callbackId];
    } else {
        [WXApi sendReq: request];
    }
}

- (void)handleOpenURL:(NSNotification *)notification {
    NSURL *url = notification.object;
    if ([url.host isEqualToString:kAlipayHost]) {
        [[AlipaySDK defaultService] processOrderWithPaymentResult:url standbyCallback:^(NSDictionary *resultDic) {
            NSString *resultStatus=[NSString stringWithFormat:@"%@",resultDic[@"resultStatus"]];
            if ([resultStatus isEqualToString:@"9000"]) {
                CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultDic];
                [self.commandDelegate sendPluginResult:result callbackId:self.callbackId];
            } else {
                CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:resultStatus];
                [self.commandDelegate sendPluginResult:result callbackId:self.callbackId];
            }
        }];
    } else {
        [WXApi handleOpenURL:url delegate:self];
    }
}

- (void)onResp:(BaseResp*)resp {
    if (resp.errCode == 0) {
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:nil];
        [self.commandDelegate sendPluginResult:result callbackId:self.callbackId];
    } else {
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:resp.errStr];
        [self.commandDelegate sendPluginResult:result callbackId:self.callbackId];
    }
}

@end