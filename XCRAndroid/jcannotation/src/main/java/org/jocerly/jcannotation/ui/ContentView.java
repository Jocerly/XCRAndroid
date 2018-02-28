package org.jocerly.jcannotation.ui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)//表示该注解可以用于什么地方
@Retention(RetentionPolicy.RUNTIME)//表示需要在什么级别保存该注解信息；我们这里设置为运行时
public @interface ContentView {
	int value();
}
