//
//  CDVViewController+category.m
//  XCRSupChain
//
//  Created by LiXiang on 2017/7/15.
//
//

#import "CDVViewController+category.h"
#import <objc/runtime.h>

@implementation CDVViewController (category)

- (CallBack)callBack {
    return objc_getAssociatedObject(self, @selector(callBack));
}

- (void)setCallBack:(CallBack)callBack {
    objc_setAssociatedObject(self, @selector(callBack), callBack, OBJC_ASSOCIATION_COPY_NONATOMIC);
}

- (NSDictionary *)params {
    return objc_getAssociatedObject(self, @selector(params));
}

- (void)setParams:(NSDictionary *)params {
    objc_setAssociatedObject(self, @selector(params), params, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

@end