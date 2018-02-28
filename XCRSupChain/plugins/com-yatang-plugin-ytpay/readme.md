###调用方法
```
/// 支付宝支付
/// payInfo: 签名后的订单信息
/// success: 成功回调
/// fail: 失败回调
YTPay.alipay(payInfo, success, fail);
 
/// 微信支付
/// payInfo: 字典
    /// partnerId: 商家Id
    /// prepayId: 预支付订单Id
    /// noncestr: 随机串，防重发
    /// timestamp: 时间戳，防重发
    /// package: 商家根据财付通文档填写的数据和签名
    /// sign: 商家根据微信开放平台文档对数据做的签名
/// success: 成功回调
/// fail: 失败回调
YTPay.wxpay(payInfo, success, fail)
```
