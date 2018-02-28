package org.jocerly.jcannotation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jocerly.jcannotation.R;
import org.jocerly.jcannotation.holder.BaseViewHolder;
import org.jocerly.jcannotation.holder.FooterHolder;

import java.util.List;


/**
 * 基类BaseAdapter for RecyclerView
 */
public abstract class BaseRecyclerViewAdapter<T, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {
    protected List<T> listData;
    protected Context mContext;
    protected FooterHolder mFooterHolder;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    public static final int STATE_DEFAULT = 0;   //默认情况
    public static final int STATE_LOADING = 1;     //正在加载
    public static final int STATE_FAILURE = 2;     //加载失败
    public static final int STATE_SUCCESS = 3;     //加载失败
    public static final int STATE_FINISH = 4;      //没有更多数据了

    private int mLoadState = STATE_DEFAULT;

    /**
     * 得到加载的状态
     *
     * @return
     */
    public int getLoadState() {
        return mLoadState;
    }

    public void setLoadState(int state) {
        mLoadState = state;
    }

    public void finishLoad(int state) {
        mFooterHolder.setVisibility(View.VISIBLE);
        mFooterHolder.setEnabled(true);
        switch (state) {
            case BaseRecyclerViewAdapter.STATE_SUCCESS:  //加载成功
                mLoadState = STATE_DEFAULT;
                break;
            case BaseRecyclerViewAdapter.STATE_FAILURE:  //加载失败
                mLoadState = STATE_FAILURE;
                setLoadFailView();
                break;
            case BaseRecyclerViewAdapter.STATE_FINISH:   //没有更多数据了
                mLoadState = STATE_FINISH;
                setLoadFinishView();
                break;
        }
    }

    private void setLoadFinishView() {
        mFooterHolder.setEnabled(false);
        mFooterHolder.setProgressBarVisible(View.GONE);
        mFooterHolder.setTextColor(mContext.getResources().getColor(R.color.text_light));
        mFooterHolder.setMessage("没有更多数据了o(╯□╰)o");
    }

    private void setLoadFailView() {
        mFooterHolder.setProgressBarVisible(View.GONE);
        mFooterHolder.setTextColor(mContext.getResources().getColor(R.color.red));
        mFooterHolder.setMessage("加载失败，请点击重试");
    }

    /**
     * 设置为默认状态
     */
    public void setLoadingDefualt() {
        mLoadState = STATE_DEFAULT;
        if (mFooterHolder != null) {
            mFooterHolder.setProgressBarVisible(View.VISIBLE);
//            mFooterHolder.setTextColor(mContext.getResources().getColor(R.color.text_light));
//            mFooterHolder.setMessage("加载中···");
            mFooterHolder.setMessage("");
        }
    }

    /**
     * @param list the datas to attach the adapter
     */
    public BaseRecyclerViewAdapter(Context context, List<T> list) {
        listData = list;
        mContext = context;
    }

    /**
     * get a item by index
     *
     * @param position
     * @return
     */
    protected T getItem(int position) {
        if (position == listData.size() && mCanLoadMore)
            return null;
        else
            return listData.get(position);
    }

    private boolean mCanLoadMore = false;   //说明是否可以上拉加载

    public void setCanLoadMore(boolean loadMore) {
        mCanLoadMore = loadMore;
    }

    public boolean getCanLoadMore() {
        return mCanLoadMore;
    }

    @Override
    public int getItemCount() {
        if (mCanLoadMore)
            return listData.size() + 1;
        else
            return listData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount() && mCanLoadMore) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public abstract void bindDataToItemView(VH vh, int position);

    public abstract VH createViewHolder(ViewGroup parent);

    @Override
    public final void onBindViewHolder(VH vh, int position) {
        bindDataToItemView(vh, position);
    }

    @SuppressWarnings("unchecked")
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (mCanLoadMore && viewType == TYPE_FOOTER) {
            if (mFooterHolder == null) {
                view = LayoutInflater.from(mContext).inflate(R.layout.footer_loadmore, parent, false);
                FooterHolder holder = new FooterHolder(view);
                mFooterHolder = holder;
            }
            return (VH) mFooterHolder;
        } else {
            return createViewHolder(parent);
        }
    }
}
