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

import android.view.View;

/**
 * JCFrameActivity接口协议，实现此接口可使用JCActivityManager堆栈<br>
 * <b>创建时间</b> 2014-3-1 <br>
 * <b>最后修改时间</b> 2014-5-30
 *
 * @author Jocerly
 * @version 2.25
 */
public interface I_JCActivity {

    int DESTROY = 0;
    int STOP = 2;
    int PAUSE = 1;
    int RESUME = 3;

    /**
     * 初始化控件
     */
    void initWidget();

    /**
     * 初始化数据
     */
    void initData();


    /**
     * 点击事件回调方法
     */
    void onClick(View v);
}
