//
//  CDVSubViewController.h
//  XCRSupChain
//
//  Created by LiXiang on 2017/7/28.
//
//

#import <Cordova/CDVViewController.h>

/**
 * 需要打开的URL
 */
static NSString * const PARAM_URL = @"url";
/**
 * 新页面打开的动画
 */
static NSString * const PARAM_TRANSITION = @"transition";
/**
 * 唯一标识符
 */
static NSString * const PARAM_IDENTIFIER = @"identifier";
/**
 * 是否关闭当前页
 */
static NSString * const PARAM_CLOSESELF = @"closeSelf";
/**
 * 是否隐藏导航栏
 */
static NSString * const PARAM_NAVIGATIONBARHIDDEN = @"navigationBarHidden";
/**
 * CallBackID
 */
static NSString * const PARAM_CALLBACKID = @"paramsCallBackId";

typedef void(^CallBack)(NSString *jsonString);

@interface CDVSubViewController : CDVViewController

@property(nonatomic, copy) CallBack callBack;
@property(nonatomic) NSDictionary *params;

@end
