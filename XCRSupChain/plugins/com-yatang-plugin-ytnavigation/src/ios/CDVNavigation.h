//
//  NavigationPlugin.h
//  HelloWorld
//
//  Created by Jeavil_Tang on 2017/6/16.
//
//

#import <Cordova/CDVPlugin.h>

@interface CDVNavigation : CDVPlugin

@property(nonatomic, class) NSString *currentDirectory;

- (void)goBack:(CDVInvokedUrlCommand *)command;

- (void)redirect:(CDVInvokedUrlCommand *)command;

- (void)popPage:(CDVInvokedUrlCommand *)command;

- (void)popAllPage:(CDVInvokedUrlCommand *)command;

- (void)deepLinkRedirect:(CDVInvokedUrlCommand *)command;

@end