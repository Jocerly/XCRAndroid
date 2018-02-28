package org.jocerly.jcannotation.adapter;

import java.util.ArrayList;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;

/**
 * 实现显示头部和尾部item的adapter,把头部尾部的事情交给这个adapter来做,其他的交给子adapter
 * @author asus
 *
 */
@SuppressWarnings("rawtypes")
public class WrapAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
	public ArrayList<View> headerViews = new ArrayList<View>();
    public ArrayList<View> footViews = new ArrayList<View>();
    public RecyclerView.Adapter adapter;

	public WrapAdapter(RecyclerView.Adapter adapter, ArrayList<View> headerViews, ArrayList<View> footViews) {
    	this.adapter = adapter;
        this.headerViews = headerViews;
        this.footViews = footViews;
	}
    
	@Override
	public int getItemCount() {
		int count = headerViews.size()+footViews.size();
        if(adapter!=null){
            count += adapter.getItemCount();
        }
		return count;
	}
	
	@Override
	public int getItemViewType(int position) {
		//如果是头部则返回一个不可用的标识,表示这是头部item
		if(position>=0 && position<headerViews.size()){
            return RecyclerView.INVALID_TYPE;
        }

        if(adapter != null){
            int p = position-headerViews.size();
            if(p < adapter.getItemCount()){
                return adapter.getItemViewType(p);
            }
        }
        //默认返回表示是尾部的item
		return RecyclerView.INVALID_TYPE - 1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		if (position >= 0 && position < headerViews.size()) {
			return;
		}
		
		if(adapter != null){
            int p = position-headerViews.size();
            if(p < adapter.getItemCount()){
                adapter.onBindViewHolder(holder,p);
            }
        }
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == RecyclerView.INVALID_TYPE) {//头部item
			return new RecyclerView.ViewHolder(headerViews.get(0)) {};
		} else if (viewType == RecyclerView.INVALID_TYPE-1) {//尾部item
			return new RecyclerView.ViewHolder(footViews.get(0)){};
		}
		return adapter.onCreateViewHolder(parent, viewType);
	}
}
