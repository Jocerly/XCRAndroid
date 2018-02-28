package cordova.plugin.ytpay;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yatang.cordova.navigation.Navigator;

import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class echoes a string called from JavaScript.
 */
public class YTPay extends CordovaPlugin {

    public static final String RSA2_PRIVATE = "";
    public static final String RSA_PRIVATE = "";
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    public static final String APP_ID = "wx9c1bcd9ed6a316c4";

    private static CallbackContext WXPayCallback;

    private static IWXAPI iwxapi;

    public static void setIwxapi(IWXAPI iwxapi) {
        YTPay.iwxapi = iwxapi;
    }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        return super.execute(action, args, callbackContext);
    }

   @Override
    public boolean execute(String action, String rawArgs, CallbackContext callbackContext) throws JSONException {
        return super.execute(action,rawArgs,callbackContext);
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.d("corodova_pay",action+":"+args.toString());
        if(action.equals("alipay")){
            final String orderInfo = args.optString(0,"");
            Log.d("corodova_pay", "orderinfo="+orderInfo);
            Runnable payRunnable = new Runnable() {

                @Override
                public void run() {
                    PayTask alipay = new PayTask(cordova.getActivity());
                    Map<String, String> result = alipay.payV2(orderInfo, true);
                    if(result != null) {
                        Log.d("lqs", result.toString());
                    }else{
                        Log.d("lqs","noresult");
                    }
                    PayResult payResult = new PayResult(result);
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    if (TextUtils.equals(resultStatus, "9000")) {
                        callbackContext.success(resultInfo);
                    } else {
                        callbackContext.error(resultInfo);
                    }
                }
            };

            Thread payThread = new Thread(payRunnable);
            payThread.start();
        }else if(action.equals("wxpay")){
            JSONObject res = args.optJSONObject(0);
            if(res != null){
                WXPayCallback = callbackContext;
                IWXAPI msgApi = WXAPIFactory.createWXAPI(cordova.getActivity(), APP_ID);
                PayReq request = new PayReq();
                request.appId = APP_ID;
                request.partnerId = res.optString("partnerId");
                request.prepayId = res.optString("prepayId");
                request.nonceStr = res.optString("noncestr");
                request.timeStamp = res.optString("timestamp");
                request.packageValue = res.optString("package");
                request.sign = res.optString("sign");
                iwxapi.sendReq(request);
            }
        }
        return true;
    }

    public static void setWXPayResult(BaseResp baseResp){
        if(WXPayCallback != null && baseResp != null){
            if(baseResp.errCode == BaseResp.ErrCode.ERR_OK){
                WXPayCallback.success(baseResp.errStr);
            }else{
                WXPayCallback.error(baseResp.errStr);
            }
        }
        WXPayCallback = null;
    }

}
