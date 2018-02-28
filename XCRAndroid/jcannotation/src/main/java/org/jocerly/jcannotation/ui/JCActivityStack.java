/*
 * Copyright (c) 2014,JCFrameForAndroid Open Source Project,Jocerly.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jocerly.jcannotation.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import java.util.Stack;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出<br>
 * <b>创建时间</b> 2014-2-28
 *
 * @author Jocerly
 * @version 1.1
 */
final public class JCActivityStack {
    private static Stack<I_JCActivity> activityStack;
    private static final JCActivityStack instance = new JCActivityStack();

    private JCActivityStack() {
    }

    public static JCActivityStack create() {
        return instance;
    }

    /**
     * 获取当前Activity栈中元素个数
     */
    public int getCount() {
        return activityStack.size();
    }

    /**
     * 添加Activity到栈
     */
    public void addActivity(I_JCActivity activity) {
        if (activityStack == null) {
            activityStack = new Stack<I_JCActivity>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（栈顶Activity）
     */
    public Activity topActivity() {
        if (activityStack == null || activityStack.isEmpty()) {
            return null;
        }
        I_JCActivity activity = activityStack.lastElement();
        return (Activity) activity;
    }

    /**
     * 获取当前Activity（栈顶Activity） 没有找到则返回null
     */
    public Activity findActivity(Class<?> cls) {
        I_JCActivity activity = null;
        for (I_JCActivity aty : activityStack) {
            if (aty.getClass().equals(cls)) {
                activity = aty;
                break;
            }
        }
        return (Activity) activity;
    }

    /**
     * 结束当前Activity（栈顶Activity）
     */
    public void finishActivity() {
        I_JCActivity activity = activityStack.lastElement();
        finishActivity((Activity) activity);
    }

    /**
     * 结束指定的Activity(重载)
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            // activity.finish();//此处不用finish
            activity = null;
        }
    }

    /**
     * 结束指定的Activity(重载)
     */
    public void finishActivity(Class<?> cls) {
        for (I_JCActivity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity((Activity) activity);
            }
        }
    }

    /**
     * 关闭除了指定activity以外的全部activity 如果cls不存在于栈中，则栈全部清空
     *
     * @param cls
     */
    public void finishOthersActivity(Class<?> cls) {
        for (I_JCActivity activity : activityStack) {
            if (!(activity.getClass().equals(cls))) {
                finishActivity((Activity) activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                ((Activity) activityStack.get(i)).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        finishAllActivity();
        try {
            int currentVersion = android.os.Build.VERSION.SDK_INT;
            if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(startMain);
                System.exit(0);
            } else {// android2.1
                ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
                am.restartPackage(context.getPackageName());
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}