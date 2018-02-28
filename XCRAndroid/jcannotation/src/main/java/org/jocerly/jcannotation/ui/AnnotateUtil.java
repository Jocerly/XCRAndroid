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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 注解工具类<br>
 * <p>
 * <b>创建时间</b> 2014-6-5
 *
 * @author Jocerly
 * @version 1.1
 */
@SuppressLint("NewApi")
public class AnnotateUtil {
    private static final String METHOD_SET_CONTENTVIEW = "setContentView";

    /**
     * @param currentClass 当前类，一般为Activity或Fragment
     * @param sourceView   待绑定控件的直接或间接父控件
     */
    public static void initBindView(Object currentClass, View sourceView) {
        // 通过反射获取包括私有的成员变量，反射的字段可能是一个类（静态）字段或实例字段
        Field[] fields = currentClass.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                // 返回BindView类型的注解内容
                BindView bindView = field.getAnnotation(BindView.class);
                if (bindView != null) {
                    int viewId = bindView.id();
                    boolean clickLis = bindView.click();
                    View view = sourceView.findViewById(viewId);
                    try {
                        field.setAccessible(true);
                        if (view != null && clickLis) {
                            view.setOnClickListener((OnClickListener) currentClass);
                        }
                        // 将currentClass的field赋值为sourceView.findViewById(viewId)
                        field.set(currentClass, sourceView.findViewById(viewId));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 注入主布局文件
     *
     * @param activity
     */
    public static void injectContentView(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        ContentView contentView = clazz.getAnnotation(ContentView.class);
        //有注解
        if (contentView != null) {
            int resId = contentView.value();
            try {
                Method method = clazz.getMethod(METHOD_SET_CONTENTVIEW, int.class);
                method.setAccessible(true);
                method.invoke(activity, resId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 必须在setContentView之后调用
     *
     * @param aty Activity对象
     */
    public static void initBindView(Activity aty) {
        injectContentView(aty);
        initBindView(aty, aty.getWindow().getDecorView());
    }

    /**
     * 必须在setContentView之后调用
     *
     * @param view 侵入式的view，例如使用inflater载入的view
     */
    public static void initBindView(View view) {
        Context cxt = view.getContext();
        if (cxt instanceof Activity) {
            initBindView((Activity) cxt);
        } else {
            throw new RuntimeException("view must into Activity");
        }
    }

    /**
     * 必须在setContentView之后调用
     *
     * @param frag 要初始化的Fragment
     */
    public static void initBindView(Fragment frag) {
        initBindView(frag, frag.getActivity().getWindow().getDecorView());
    }

}
