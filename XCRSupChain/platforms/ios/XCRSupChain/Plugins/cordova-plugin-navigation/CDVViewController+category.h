//
//  CDVViewController+category.h
//  XCRSupChain
//
//  Created by LiXiang on 2017/7/15.
//
//

#import <Cordova/CDVViewController.h>

typedef void(^CallBack)(NSString *jsonString);
@interface CDVViewController (category)

@property(nonatomic, copy) CallBack callBack;
@property(nonatomic) NSDictionary *params;

@end