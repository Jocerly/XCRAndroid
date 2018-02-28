cordova.define("com-yatang-plugin-ytnetwork.YTNetwork", function(require, exports, module) {
var exec = require('cordova/exec');

var YTNetwork = {
	checkStatus: function(onSuccess) {
		exec(onSuccess, null, "YTNetwork", "checkStatus");
	}
};
module.exports = YTNetwork;

});
