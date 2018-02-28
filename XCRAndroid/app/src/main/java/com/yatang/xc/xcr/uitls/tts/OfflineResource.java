package com.yatang.xc.xcr.uitls.tts;

import android.content.Context;
import android.content.res.AssetManager;

import org.jocerly.jcannotation.utils.JCLoger;

import java.io.IOException;


/**
 * Created by fujiayi on 2017/5/19.
 */

public class OfflineResource {
    private static final String SAMPLE_DIR = "baiduTTS";

    private AssetManager assets;
    private String destPath;

    private String textFilename;
    private String modelFilename;

    public OfflineResource(Context context) throws IOException {
        context = context.getApplicationContext();
        this.assets = context.getApplicationContext().getAssets();
        this.destPath = FileUtil.createTmpDir(context);
        setOfflineVoiceType();
    }

    public String getModelFilename() {
        return modelFilename;
    }

    public String getTextFilename() {
        return textFilename;
    }

    public void setOfflineVoiceType() throws IOException {
        String text = "bd_etts_text.dat";
        String fmodel = "bd_etts_speech_female.dat";
        String model = "bd_etts_speech_male.dat";
        textFilename = copyAssetsFile(text);
        modelFilename = copyAssetsFile(fmodel);
    }


    private String copyAssetsFile(String sourceFilename) throws IOException {
        String destFilename = destPath + "/" + sourceFilename;
        FileUtil.copyFromAssets(assets, sourceFilename, destFilename, false);
        JCLoger.debug("文件复制成功：" + destFilename);
        return destFilename;
    }
}
