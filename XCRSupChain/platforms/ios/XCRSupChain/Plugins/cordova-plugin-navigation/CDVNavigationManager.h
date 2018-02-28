//
//  CDVNavigationManager.h
//  XCRSupChain
//
//  Created by LiXiang on 2017/7/18.
//
//

#import <Foundation/Foundation.h>

@interface CDVNavigationManager : NSObject

+ (instancetype)sharedInstance;

@property(nonatomic) NSString *currentDirectory;

@end
