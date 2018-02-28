/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.yatang.xc.xcr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.yatang.plugin.navigation.CordovaPageActivity;
import com.yatang.plugin.navigation.module.Module;
import com.yatang.xc.xcr.MyApplication;
import com.yatang.xc.xcr.R;
import com.yatang.plugin.navigation.module.ModuleEntity;
import com.yatang.plugin.navigation.module.UpdateStatus;
import com.yatang.xc.xcr.uitls.ModuleUpdaterManager;
import com.yatang.xc.xcr.uitls.VersionInfoHelper;

import org.jocerly.jcannotation.widget.UIHelper;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

public class StartActivity extends AppCompatActivity {

    private int retryNum = 0;
    private Bundle bundle;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (getIntent() != null && getIntent().getExtras() != null) {
            bundle = getIntent().getExtras();
        }
        initWidget();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ModuleUpdaterManager.checkUpdateCompleted(null);
    }

    public void initWidget() {
        UIHelper.showLoadDialog(this,false);
        ModuleUpdaterManager.checkUpdateCompleted(new ModuleUpdaterManager.UpdateResultCallback() {
            @Override
            public void onResult(boolean isSuccess) {
                if (isSuccess && VersionInfoHelper.checkWebFileExists()) {
                    UIHelper.cloesLoadDialog();
                    Intent intent = new Intent(StartActivity.this, CordovaPageActivity.class);
                    //传递上一步传递过过来的参数
                    if(bundle != null){
                        intent.putExtras(bundle);
                    }
                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    finish();
                }else{
                    if(retryNum < 3) {
                         ModuleUpdaterManager.getInstance().start(true);
                        retryNum++;
                    }else {
                        Toast.makeText(StartActivity.this, UpdateStatus.ERROR_MSG.get(UpdateStatus.UPDATE_STATUS_DOWNLOAD_FAIL), Toast.LENGTH_SHORT).show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        UIHelper.cloesLoadDialog();
                                        if (StartActivity.this != null && !StartActivity.this.isFinishing() && !StartActivity.this.isDestroyed()) {
                                            StartActivity.this.finish();
                                        }
                                    }
                                });
                            }
                        }).start();
                    }
                }
            }

            @Override
            public void onProgress(int current, int max) {

            }
        });
    }


}
