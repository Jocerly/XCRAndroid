package org.jocerly.jcannotation.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import org.jocerly.jcannotation.R;
import org.jocerly.jcannotation.style.Sprite;
import org.jocerly.jcannotation.style.SpriteFactory;
import org.jocerly.jcannotation.style.Style;
import org.jocerly.jcannotation.ui.SpinKitView;

/**
 * 提示框
 * @author Jocerly
 */
public class UIHelper {
	private static boolean isClosed = false;
	public static Dialog mLoadDialog;

    public static void showLoadDialog(Context context) {
        showLoadDialog(context, null, true, false);
    }

	public static void showLoadDialog(Context context, boolean isBottom) {
		showLoadDialog(context, null, true, isBottom);
	}

	public static void showLoadDialog(Context context, boolean isCancle, boolean isBottom) {
		showLoadDialog(context, null, isCancle, isBottom);
	}

	public static void showLoadDialog(Context context, String msg, boolean isCancle, boolean isBottom) {
		if (context == null) {
			return;
		}
		if (mLoadDialog != null && mLoadDialog.isShowing()) {
			return;
		}
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View login_doag = null;
        if (isBottom) {
            login_doag = inflater.inflate(R.layout.dialog_loading_bottom, null);
        } else {
            login_doag = inflater.inflate(R.layout.dialog_loading, null);
        }
		SpinKitView spinKitView = (SpinKitView) login_doag.findViewById(R.id.spin_kit);
		Style style = Style.values()[0];
		Sprite drawable = SpriteFactory.create(style);
		spinKitView.setIndeterminateDrawable(drawable);
		
		mLoadDialog = new Dialog(context, R.style.loading_large);
		mLoadDialog.setCanceledOnTouchOutside(isCancle);
		mLoadDialog.setCancelable(isCancle);
		mLoadDialog.setContentView(login_doag);
		try {
			mLoadDialog.show();
			isClosed = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void cloesLoadDialog() {
		if (isClosed)
			return;
		if (mLoadDialog != null && mLoadDialog.isShowing()) {
			mLoadDialog.dismiss();
			mLoadDialog = null;
			isClosed = true;
		}
	}
}
