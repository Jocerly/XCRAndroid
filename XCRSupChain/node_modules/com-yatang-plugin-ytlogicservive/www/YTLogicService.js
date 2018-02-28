var exec = require('cordova/exec');

var YTLogicService = {
	fetchCommonParams: function(onSuccess, onFail) {
	    exec(onSuccess, null, "YTLogicService", "fetchCommonParams", []);
	},
	handleError: function(errCode, onSuccess, onFail) {
	    exec(onSuccess, null, "YTLogicService", "handleError", [errCode]);
	}
};
module.exports = YTLogicService;
