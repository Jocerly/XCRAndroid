//
//  NavigationPlugin.m
//  HelloWorld
//
//  Created by Jeavil_Tang on 2017/6/16.
//
//

#import "CDVNavigation.h"
#import "CDVSubViewController.h"

static NSString * _currentDirectory = nil;

@interface CDVNavigation ()
@property (nonatomic, strong) CDVInvokedUrlCommand *command;
@end

@implementation CDVNavigation

- (void)goBack:(CDVInvokedUrlCommand *)command {
    NSString *returnParams = [command argumentAtIndex:0];
    if ([self.viewController isKindOfClass:[CDVSubViewController class]]) {
        CDVSubViewController *cdvVC = (CDVSubViewController *)self.viewController;
        if (cdvVC.callBack) {
            cdvVC.callBack(returnParams);
        }
    }
    [self.viewController.navigationController popViewControllerAnimated:YES];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)redirect:(CDVInvokedUrlCommand *)command {
    NSString *url = [command argumentAtIndex:0 withDefault:nil];
    NSDictionary *option = [command argumentAtIndex:1 withDefault:nil];
    NSString *transition = option[PARAM_TRANSITION];
    NSString *paramsCallBackId = option[PARAM_CALLBACKID];
    BOOL closeSelf = [option[PARAM_CLOSESELF] boolValue];
    CDVSubViewController *vc = [[CDVSubViewController alloc] init];
    NSLog(@"路径为:%@", CDVNavigation.currentDirectory);
    vc.wwwFolderName = CDVNavigation.currentDirectory;
    NSMutableDictionary *params = [NSMutableDictionary dictionaryWithObjectsAndKeys:url, PARAM_IDENTIFIER, nil];
    if (option[PARAM_NAVIGATIONBARHIDDEN]) {
        params[PARAM_NAVIGATIONBARHIDDEN] = option[PARAM_NAVIGATIONBARHIDDEN];
    }
    vc.params = params;
    if ([url hasPrefix:@"http://"] || [url hasPrefix:@"https://"]) {
        vc.wwwFolderName = url;
        vc.startPage = @"";
    } else {
        if ([vc.wwwFolderName isEqualToString:@"www"]) {
            vc.startPage = url;
        } else {
            vc.startPage = [NSString stringWithFormat:@"file://%@/%@", vc.wwwFolderName, [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
        } 
    }
    if (paramsCallBackId) {
        __weak __typeof__(self) weakSelf = self;
        vc.callBack = ^(NSString *jsonString) {
            if ([weakSelf.viewController isKindOfClass:[CDVSubViewController class]]) {
                CDVSubViewController *cdvVC = (CDVSubViewController *)weakSelf.viewController;
                jsonString = jsonString ? jsonString : @"";
                CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsMultipart:@[paramsCallBackId, jsonString]];
                [cdvVC.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }
        };
    }
    //如果当前界面是要关闭的，需要把当前界面的callback传递到下个vc去。
    if (closeSelf) {
        CDVSubViewController *lastVC = (CDVSubViewController *)[self.viewController.navigationController.viewControllers lastObject];
        if ([lastVC isKindOfClass:[CDVViewController class]]) {
            if (lastVC.callBack) {
                vc.callBack = lastVC.callBack;
            }
        }
    }
    if ([transition isEqualToString:@"bottom"]) {
        CATransition* transition = [CATransition animation];
        transition.duration = 0.35;
        transition.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseInEaseOut];
        transition.type = kCATransitionPush; //kCATransitionMoveIn; //, kCATransitionPush, kCATransitionReveal, kCATransitionFade
        transition.subtype = kCATransitionFromTop; //kCATransitionFromLeft, kCATransitionFromRight, kCATransitionFromTop, kCATransitionFromBottom
        [self.viewController.navigationController.view.layer addAnimation:transition forKey:nil];
        if (closeSelf) {
            NSMutableArray *vcs = [self.viewController.navigationController.viewControllers mutableCopy];
            [vcs replaceObjectAtIndex:vcs.count-1 withObject:vc];
            [self.viewController.navigationController setViewControllers:vcs animated:NO];
        } else {
            [self.viewController.navigationController pushViewController:vc animated:NO];
        }
    } else if ([transition isEqualToString:@"none"]) {
        if (closeSelf) {
            NSMutableArray *vcs = [self.viewController.navigationController.viewControllers mutableCopy];
            [vcs replaceObjectAtIndex:vcs.count-1 withObject:vc];
            [self.viewController.navigationController setViewControllers:vcs animated:NO];
        } else {
            [self.viewController.navigationController pushViewController:vc animated:NO];
        }
    } else {
        if (closeSelf) {
            NSMutableArray *vcs = [self.viewController.navigationController.viewControllers mutableCopy];
            [vcs replaceObjectAtIndex:vcs.count-1 withObject:vc];
            [self.viewController.navigationController setViewControllers:vcs animated:YES];
        } else {
            [self.viewController.navigationController pushViewController:vc animated:YES];
        }
    }
}

- (void)popPage:(CDVInvokedUrlCommand *)command {
    BOOL popSelf = [[command argumentAtIndex:0 withDefault:@(NO)] boolValue];
    NSArray *vcs = self.viewController.navigationController.viewControllers;
    for (NSInteger i = vcs.count-1; i >= 0; i-- ) {
        UIViewController *vc = vcs[i];
        if (popSelf) {
            if (vc == self.viewController) {
                NSInteger lastPageIndex = i - 1;
                if (lastPageIndex < 0) {
                    lastPageIndex = 0;
                }
                UIViewController *lastPage = [vcs objectAtIndex:lastPageIndex];
                [self.viewController.navigationController popToViewController:lastPage animated:YES];
                CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                return;
            }
        } else {
            if (vc == self.viewController) {
                [self.viewController.navigationController popToViewController:vc animated:YES];
                CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                return;
            }
        }
    }
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_CLASS_NOT_FOUND_EXCEPTION];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)popAllPage:(CDVInvokedUrlCommand *)command {
    NSArray *vcs = self.viewController.navigationController.viewControllers;
    for (NSInteger i = vcs.count-1; i >= 0; i-- ) {
        UIViewController *vc = vcs[i];
        if (![vc isKindOfClass:[CDVViewController class]]) {
            [self.viewController.navigationController popToViewController:vc animated:YES];
            CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            return;
        }
    }
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_CLASS_NOT_FOUND_EXCEPTION];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)deepLinkRedirect:(CDVInvokedUrlCommand *)command {
    NSDictionary *option = [command argumentAtIndex:0 withDefault:nil];
    NSString *url = option[PARAM_URL];
    CDVSubViewController *vc = [[CDVSubViewController alloc] init];
    NSString *curFilePath=[NSString stringWithFormat:@"file://%@/www",[NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0]];
    NSLog(@"路径为:%@",curFilePath);
    if ([[NSFileManager defaultManager] fileExistsAtPath:curFilePath]) {
        vc.wwwFolderName = curFilePath;
    }
    vc.startPage = url;
    
    [self.viewController.navigationController popToViewController:vc animated:YES];
}

+ (void)setCurrentDirectory:(NSString *)currentDirectory {
    if (_currentDirectory != currentDirectory) {
        _currentDirectory = currentDirectory;
    }
}

+ (NSString *)currentDirectory {
    return _currentDirectory;
}

@end