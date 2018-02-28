package org.jocerly.jcannotation.widget.listView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
/**
 * 自定义ListView---消除ScrollView与ListView的冲突
 * @author Administrator
 *
 */
public class CustomerListView extends ListView {
	public CustomerListView(Context context) {
		super(context, null);
	}

	public CustomerListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomerListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
