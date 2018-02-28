package org.jocerly.jcannotation.holder;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jocerly.jcannotation.R;
/**
 * 底部加载更多ViewHolder
 * @author asus
 *
 */
public class FooterHolder extends BaseViewHolder{
    private RelativeLayout rlFooter;
	public TextView mFooterTextView;
    public ProgressBar mProgressBar;
    
    public FooterHolder(View itemView){
        super(itemView);
        rlFooter = (RelativeLayout) itemView.findViewById(R.id.rlFooter);
        mFooterTextView = (TextView) itemView.findViewById(R.id.tv_footer);
        mProgressBar = (ProgressBar) itemView.findViewById(R.id.footer_progressbar);
    }
    
    public void setEnabled(boolean enabled) {
    	mFooterTextView.setEnabled(enabled);
	}

    public void setVisibility(int visibility) {
        rlFooter.setVisibility(visibility);
    }

    public void setMessage(String msg){
        mFooterTextView.setText(msg);
    }

    public void setTextColor(int color){
        mFooterTextView.setTextColor(color);
    }

    public void setProgressBarVisible(int visible){
        mProgressBar.setVisibility(visible);
    }
}
