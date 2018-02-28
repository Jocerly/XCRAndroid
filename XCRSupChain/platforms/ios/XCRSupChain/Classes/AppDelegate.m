/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */

//
//  AppDelegate.m
//  XCRSupChain
//
//  Created by ___FULLUSERNAME___ on ___DATE___.
//  Copyright ___ORGANIZATIONNAME___ ___YEAR___. All rights reserved.
//

#import "AppDelegate.h"
#import "MainViewController.h"
#import <UIKit/UIKit.h>
#import "CDVNavigationManager.h"

#define Test 1

@implementation AppDelegate

- (BOOL)application:(UIApplication*)application didFinishLaunchingWithOptions:(NSDictionary*)launchOptions
{
    CGRect screenBounds = [[UIScreen mainScreen] bounds];
    
    self.window = [[UIWindow alloc] initWithFrame:screenBounds];
    self.window.autoresizesSubviews = YES;
    
    
#ifdef Test
    [CDVNavigationManager sharedInstance].currentDirectory = @"www";
#else
    [CDVNavigationManager sharedInstance].currentDirectory = [NSString stringWithFormat:@"%@/www", [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0]];
#endif

    MainViewController *viewController = [[MainViewController alloc] init];
    NSString *dir=[NSString stringWithFormat:@"file://%@/www",[NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0]];
#ifdef Test
    viewController.wwwFolderName = @"www";
    viewController.startPage = @"index.html";
#else
    viewController.wwwFolderName = dir;
    viewController.startPage = [NSString stringWithFormat:@"%@/index.html", dir];
#endif
    NSLog(@"dir: %@", dir);
    UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:viewController];
    [nav setNavigationBarHidden:YES];
    self.window.rootViewController = nav;
    [self.window makeKeyAndVisible];
    return YES;
}
@end
