//
//  NavigationPlugin.h
//  HelloWorld
//
//  Created by Jeavil_Tang on 2017/6/16.
//
//

//#import "CDV.h"
#import <Cordova/CDVPlugin.h>

@interface CDVNavigation : CDVPlugin

- (void)goBack:(CDVInvokedUrlCommand *)command;

- (void)redirect:(CDVInvokedUrlCommand *)command;

- (void)popPage:(CDVInvokedUrlCommand *)command;

- (void)deepLinkRedirect:(CDVInvokedUrlCommand *)command;

@end
