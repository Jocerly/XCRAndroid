package com.yatang.xc.xcr.db;

/**
 * Created by dengjiang on 2017/9/5.
 */

public class ADEntityBase {
    public String AdId;//位置Id
    public String AdPic;//图片链接
    public String AdJump;//跳转链接

    public String getAdId() {
        return AdId;
    }

    public void setAdId(String adId) {
        AdId = adId;
    }

    public String getAdPic() {
        return AdPic;
    }

    public void setAdPic(String adPic) {
        AdPic = adPic;
    }

    public String getAdJump() {
        return AdJump;
    }

    public void setAdJump(String adJump) {
        AdJump = adJump;
    }

    @Override
    public String toString() {
        return "ADEntityBase:" +
                "AdId = " + AdId+
                "AdPic=  " + AdPic+
                "AdJump = " + AdJump;
    }
}
