cordova.define("cordova-plugin-navigation.YTNavigation", function(require, exports, module) {
var exec = require('cordova/exec');
var YTNavigation = {
	iCallbackId: Math.floor(Math.random() * 2000000000),
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
        	callbackId = YTNavigation.iCallbackId++;
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
        popPage: function(url) {
                exec(null, null, "YTNavigation", "popPage", [url]);
        }
};

module.exports = YTNavigation;
});
