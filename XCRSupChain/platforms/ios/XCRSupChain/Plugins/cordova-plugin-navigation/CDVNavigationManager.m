//
//  CDVNavigationManager.m
//  XCRSupChain
//
//  Created by LiXiang on 2017/7/18.
//
//

#import "CDVNavigationManager.h"

@implementation CDVNavigationManager

+ (instancetype)sharedInstance {
    static id _shareInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _shareInstance = [[self alloc] init];
    });
    return _shareInstance;
}

- (instancetype)init {
    self = [super init];
    if (self) {
        _currentDirectory = @"www";
    }
    return self;
}

@end
