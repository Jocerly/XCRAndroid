//
//  CDVNetwork.m
//  XCRSupChain
//
//  Created by LiXiang on 2017/7/28.
//
//

#import "CDVNetwork.h"
#import "Reachability.h"

@interface CDVNetwork ()

@property(nonatomic) Reachability *reach;

@end

@implementation CDVNetwork

- (void)pluginInitialize {
    _reach = [Reachability reachabilityForInternetConnection];
}

- (void)checkStatus:(CDVInvokedUrlCommand *)command {
    NetworkStatus status = [self.reach currentReachabilityStatus];
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsNSInteger:status];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

@end