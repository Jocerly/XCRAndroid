var exec = require('cordova/exec');

var YTNetwork = {
	checkStatus: function(onSuccess) {
		exec(onSuccess, null, "YTNetwork", "checkStatus");
	}
};
module.exports = YTNetwork;
