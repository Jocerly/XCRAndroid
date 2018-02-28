cordova.define('cordova/plugin_list', function(require, exports, module) {
module.exports = [
    {
        "id": "com-yatang-plugin-ytlogicservice.YTLogicService",
        "file": "plugins/com-yatang-plugin-ytlogicservice/www/ytlogicservice.js",
        "pluginId": "com-yatang-plugin-ytlogicservice",
        "clobbers": [
            "YTLogicService"
        ]
    },
    {
        "id": "com-yatang-plugin-ytnavigation.YTNavigation",
        "file": "plugins/com-yatang-plugin-ytnavigation/www/ytnavigation.js",
        "pluginId": "com-yatang-plugin-ytnavigation",
        "clobbers": [
            "YTNavigation"
        ]
    },
    {
        "id": "com-yatang-plugin-ytnetwork.YTNetwork",
        "file": "plugins/com-yatang-plugin-ytnetwork/www/ytnetwork.js",
        "pluginId": "com-yatang-plugin-ytnetwork",
        "clobbers": [
            "YTNetwork"
        ]
    },
    {
        "id": "com-yatang-plugin-ytnotification.YTNotification",
        "file": "plugins/com-yatang-plugin-ytnotification/www/ytnotification.js",
        "pluginId": "com-yatang-plugin-ytnotification",
        "clobbers": [
            "YTNotification"
        ]
    },
    {
        "id": "com-yatang-plugin-ytpay.YTPay",
        "file": "plugins/com-yatang-plugin-ytpay/www/ytpay.js",
        "pluginId": "com-yatang-plugin-ytpay",
        "clobbers": [
            "YTPay"
        ]
    }
];
module.exports.metadata = 
// TOP OF METADATA
{
    "cordova-plugin-whitelist": "1.3.2",
    "com-yatang-plugin-ytlogicservice": "1.0.0",
    "com-yatang-plugin-ytnavigation": "1.0.0",
    "com-yatang-plugin-ytnetwork": "1.0.0",
    "com-yatang-plugin-ytnotification": "1.0.0",
    "com-yatang-plugin-ytpay": "1.0.0",
    "x5webview-cordova-plugin": "1.1.0"
};
// BOTTOM OF METADATA
});