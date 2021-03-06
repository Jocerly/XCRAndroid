//
//  CDVNotification.m
//  XCRSupChain
//
//  Created by LiXiang on 2017/8/12.
//
//

#import "CDVNotification.h"

@implementation CDVNotification

- (void)listen:(CDVInvokedUrlCommand *)command {
    NSString *notificationKey = [command argumentAtIndex:0];
    NSNumber *paramsCallBackId = [command argumentAtIndex:1];
    [[NSNotificationCenter defaultCenter] addObserverForName:notificationKey object:nil queue:[NSOperationQueue mainQueue]
                                                  usingBlock:^(NSNotification * _Nonnull note) {
                                                      NSString *data = note.object;
                                                      if (paramsCallBackId) {
                                                          CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsMultipart:@[paramsCallBackId, data]];
                                                          pluginResult.keepCallback = @(YES);
                                                          [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                                                      }
                                                  }];
}

- (void)emit:(CDVInvokedUrlCommand *)command {
    NSString *notificationKey = [command argumentAtIndex:0];
    NSString *data = [command argumentAtIndex:1];
    [[NSNotificationCenter defaultCenter] postNotificationName:notificationKey object:data];
}

@end