//
//  NavigationPlugin.m
//  HelloWorld
//
//  Created by Jeavil_Tang on 2017/6/16.
//
//

#import "CDVNavigation.h"
#import "CDVViewController+category.h"
#import "CDVNavigationManager.h"

static NSString * const PARAM_IDENTIFIER = @"PARAM_IDENTIFIER";
static NSString * const PARAM_NAVIGATIONBARHIDDEN = @"PARAM_NAVIGATIONBARHIDDEN";

@interface CDVNavigation ()
@property (nonatomic, strong) CDVInvokedUrlCommand *command;
@property (nonatomic, strong) CDVViewController *mainVC;
@end

@implementation CDVNavigation

- (void)goBack:(CDVInvokedUrlCommand *)command {
    NSString *returnParams = [command argumentAtIndex:0];
    if (returnParams) {
        if ([self.viewController isKindOfClass:[CDVViewController class]]) {
            CDVViewController *cdvVC = (CDVViewController *)self.viewController;
            if (cdvVC.callBack) {
                cdvVC.callBack(returnParams);
            }
        }
    }
    [self.viewController.navigationController popViewControllerAnimated:YES];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)redirect:(CDVInvokedUrlCommand *)command {
    NSString *url = [command argumentAtIndex:0 withDefault:nil];
    NSDictionary *option = [command argumentAtIndex:1 withDefault:nil];
    NSString *transition = option[@"transition"];
    NSString *paramsCallBackId = option[@"paramsCallBackId"];
    BOOL navigationBarHidden = [option[@"navigationBarHidden"] boolValue];
    CDVViewController *vc = [[CDVViewController alloc] init];
    NSLog(@"路径为:%@", [CDVNavigationManager sharedInstance].currentDirectory);
    vc.wwwFolderName = [CDVNavigationManager sharedInstance].currentDirectory;
    NSDictionary *params = @{PARAM_IDENTIFIER:url};
    vc.params = params;
    if ([vc.wwwFolderName isEqualToString:@"www"]) {
        vc.startPage = url;
    } else {
        vc.startPage = [NSString stringWithFormat:@"file://%@/%@", vc.wwwFolderName, url];
    }
    if (paramsCallBackId) {
        __weak __typeof__(self) weakSelf = self;
        vc.callBack = ^(NSString *jsonString) {
            if ([weakSelf.viewController isKindOfClass:[CDVViewController class]]) {
                CDVViewController *cdvVC = (CDVViewController *)weakSelf.viewController;
                CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsMultipart:@[paramsCallBackId, jsonString]];
                [cdvVC.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }
        };
    }
    if ([transition isEqualToString:@"bottom"]) {
        CATransition* transition = [CATransition animation];
        transition.duration = 0.35;
        transition.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseInEaseOut];
        transition.type = kCATransitionPush; //kCATransitionMoveIn; //, kCATransitionPush, kCATransitionReveal, kCATransitionFade
        transition.subtype = kCATransitionFromTop; //kCATransitionFromLeft, kCATransitionFromRight, kCATransitionFromTop, kCATransitionFromBottom
        [self.viewController.navigationController.view.layer addAnimation:transition forKey:nil];
        [self.viewController.navigationController pushViewController:vc animated:NO];
    } else if ([transition isEqualToString:@"none"]) {
        [self.viewController.navigationController pushViewController:vc animated:NO];
    } else {
        [self.viewController.navigationController pushViewController:vc animated:YES];
    }
    self.viewController.navigationController.navigationBarHidden = navigationBarHidden;
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(notificationHandle:) name:@"H5Notification"  object:nil];
}

- (void)popPage:(CDVInvokedUrlCommand *)command {
    NSString *url = [command argumentAtIndex:0 withDefault:nil];
    for (UIViewController *vc in self.viewController.navigationController.viewControllers) {
        if ([vc isKindOfClass:[CDVViewController class]]) {
            NSString *originUrl = ((CDVViewController *)vc).params[PARAM_IDENTIFIER];
            if ([originUrl isEqualToString:url]) {
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


- (void)deepLinkRedirect:(CDVInvokedUrlCommand *)command {
    NSDictionary *option = [command argumentAtIndex:0 withDefault:nil];
    NSString *url = option[@"url"];
    //    NSString *title = option[@"title"];
    //    NSDictionary *parmas = option[@"params"];
    CDVViewController *vc = [[CDVViewController alloc] init];
    NSString *curFilePath=[NSString stringWithFormat:@"file://%@/www",[NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0]];
    NSLog(@"路径为:%@",curFilePath);
    if ([[NSFileManager defaultManager] fileExistsAtPath:curFilePath]) {
        vc.wwwFolderName = curFilePath;
    }
    vc.startPage = url;
    
    [self.viewController.navigationController popToViewController:vc animated:YES];
}

- (void)notificationHandle:(NSNotification *)noti {
    id obj = noti.object;
    NSLog(@"接收到的广播内容 = %@",obj);
}

- (UIViewController *)getCurrentVCFrom:(UIViewController *)rootVC {
    
    UIViewController *currentVC;
    
    if ([rootVC presentedViewController]) {
        // 视图是被presented出来的
        
        rootVC = [rootVC presentedViewController];
    }
    
    if ([rootVC isKindOfClass:[UITabBarController class]]) {
        // 根视图为UITabBarController
        
        currentVC = [self getCurrentVCFrom:[(UITabBarController *)rootVC selectedViewController]];
        
    } else if ([rootVC isKindOfClass:[UINavigationController class]]){
        // 根视图为UINavigationController
        
        currentVC = [self getCurrentVCFrom:[(UINavigationController *)rootVC visibleViewController]];
        
    } else {
        // 根视图为非导航类
        
        currentVC = rootVC;
    }
    
    return currentVC;
}


@end