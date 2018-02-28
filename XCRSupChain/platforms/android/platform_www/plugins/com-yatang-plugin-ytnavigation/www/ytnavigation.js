cordova.define("com-yatang-plugin-ytnavigation.YTNavigation", function(require, exports, module) {
var exec = require('cordova/exec');
var YTNavigation = {
    iCallbackId: 0,
    iCallbacks: [],
    goBack: function(data) {
        exec(null, null, "YTNavigation", "goBack", [data]);
    },
    redirect: function(url, options) {
        options = options || {
            transition: "right",
            closeSelf: false,
            navigationBarHidden: true
        };
        if (options.paramsCallBack) {
            var callbackId = -1;
            callbackId = YTNavigation.iCallbackId++ % 65536;
            YTNavigation.iCallbacks[callbackId.toString()] = options.paramsCallBack;
            options.paramsCallBackId = callbackId;
            exec(function(callbackId, data) {
                var callbackImpl = YTNavigation.iCallbacks[callbackId];
                callbackImpl && callbackImpl(data);
            }, null, "YTNavigation", "redirect", [url, options]);
        } else {
            exec(null, null, "YTNavigation", "redirect", [url, options]);
        }
    },
    deepLinkRedirect: function(url) {
        exec(null, null, "YTNavigation", "deepLinkRedirect", [url]);
    },
    popPage: function(popSelf) {
        exec(null, null, "YTNavigation", "popPage", [popSelf]);
    },
    popAllPage: function() {
        exec(null, null, "YTNavigation", "popAllPage", []);
    }
};

module.exports = YTNavigation;
});
