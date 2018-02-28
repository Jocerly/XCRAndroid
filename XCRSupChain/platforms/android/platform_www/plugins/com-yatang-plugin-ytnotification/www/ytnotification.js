cordova.define("com-yatang-plugin-ytnotification.YTNotification", function(require, exports, module) {
var exec = require('cordova/exec');
var YTNotification = {
	iCallbackId: 0,
	iCallbacks: [],
	listen: function(key, paramsCallBack){
		if (paramsCallBack) {
			var callbackId = -1;
        	callbackId = YTNotification.iCallbackId++ % 65536;
        	YTNotification.iCallbacks[callbackId.toString()] = paramsCallBack;
        	exec(function(callbackId, data){
        		var callbackImpl = YTNotification.iCallbacks[callbackId];
        		callbackImpl && callbackImpl(data);
        	}, null, "YTNotification", "listen", [key, callbackId]);
		} else {
			exec(null, null, "YTNotification", "listen", [key]);
		}
	},
	emit: function(key, data){
		exec(null, null, "YTNotification", "emit", [key, data]);
	}
};
module.exports = YTNotification;
});
