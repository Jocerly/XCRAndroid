//
//  CDVSubViewController.m
//  XCRSupChain
//
//  Created by LiXiang on 2017/7/28.
//
//

#import "CDVSubViewController.h"

@interface CDVSubViewController ()

@property(nonatomic) BOOL statusBarVisible;

@end

@implementation CDVSubViewController

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (instancetype)init
{
    self = [super init];
    if (self) {
        _statusBarVisible = ![UIApplication sharedApplication].isStatusBarHidden;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    for (UIView *view in self.view.subviews) {
        if ([view isKindOfClass:[UIWebView class]]) {
            [(UIWebView *)view setScalesPageToFit:YES];
        }
    }
}

- (void)viewWillLayoutSubviews {
    [super viewWillLayoutSubviews];
    for (UIView *view in self.view.subviews) {
        if ([view isKindOfClass:[UIWebView class]]) {
            NSLog(@"%@ frame: %@", NSStringFromClass(view.class), NSStringFromCGRect(view.frame));
            CGRect bounds = [self.view.window bounds];
            if (CGRectEqualToRect(bounds, CGRectZero)) {
                bounds = [[UIScreen mainScreen] bounds];
            }
            self.webView.frame = bounds;
            CGRect statusBarFrame = [UIApplication sharedApplication].statusBarFrame;
            CGRect frame = self.webView.frame;
            CGFloat height = statusBarFrame.size.height;
            if (self.statusBarVisible) {
                frame.origin.y = height > 0 ? height: 20;
            } else {
                frame.origin.y = height >= 20 ? height - 20 : 0;
            }
            frame.size.height -= frame.origin.y;
            self.webView.frame = frame;
        }
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    UIView *webView = self.webView;
    NSLog(@"%@", NSStringFromCGRect(webView.frame));
    if (self.params[PARAM_NAVIGATIONBARHIDDEN]) {
        if ([self.params[PARAM_NAVIGATIONBARHIDDEN] boolValue]) {
            [self.navigationController setNavigationBarHidden:YES animated:YES];
        } else {
            [self.navigationController setNavigationBarHidden:NO animated:YES];
        }
    } else {
        [self.navigationController setNavigationBarHidden:YES animated:YES];
    }
}

@end