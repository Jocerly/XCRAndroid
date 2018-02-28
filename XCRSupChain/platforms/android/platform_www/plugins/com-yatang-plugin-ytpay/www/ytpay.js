cordova.define("com-yatang-plugin-ytpay.YTPay", function(require, exports, module) {
var exec = require('cordova/exec');

var YTPay = {
	alipay: function(payInfo, onSuccess, onFail) {
		exec(onSuccess, onFail, "YTPay", "alipay", [payInfo]);
	},
	wxpay: function(payInfo, onSuccess, onFail) {
	    exec(onSuccess, onFail, "YTPay", "wxpay", [payInfo]);
	}
};
module.exports = YTPay;

});
