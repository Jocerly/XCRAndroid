package org.jocerly.jcannotation.widget.recyclevew;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.utils.DividerItemDecoration;
/**
 * 自定义RecyclerView
 */
@SuppressWarnings("rawtypes")
public class LoadMoreRecyclerView extends RecyclerView{
    public static final int spanCount = 2;
    private PTLoadMoreListener mLoadListener;
    private BaseRecyclerViewAdapter mAdapter;
    public static int type = 0;// 0:列表，1:网格
    private int firstPosition = 0;

    public LoadMoreRecyclerView(Context context){
        super(context);
        initView();
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs){
        super(context, attrs);
        initView();
    }

    public LoadMoreRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    /**
     * 加载更多
     * @author asus
     *
     */
    public interface PTLoadMoreListener{
        void loadMore();
    }

    public void setLoadMoreListener(PTLoadMoreListener listener){
        mLoadListener = listener;
    }

    public void startLoadMore(){
        mLoadListener.loadMore();
    }

    public void setRecyclerAdapter(BaseRecyclerViewAdapter adapter){
        mAdapter = adapter;
        setAdapter(adapter);
    }

    /**
     * 设置分割线
     */
    public void initDecoration(Context context, int mDividerHeight) {
        addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST, mDividerHeight));
    }

    /**
     * 设置分割线
     */
    public void initDecoration(Context context, int mDividerHeight, int color) {
        addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST, mDividerHeight, color));
    }

    /**
     * 设置分割线
     */
    public void initDecoration(Context context) {
        addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
    }

    /**
     * 设置布局
     * @param context
     * @param type 0:列表，1:网格
     */
    public void setLayoutManager(Context context, int type){
        this.type = type;
        LayoutManager manager = null;
        if (type == 0) {
            manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
//    		manager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        } else {
            manager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        }
        setLayoutManager(manager);
        getLayoutManager().scrollToPosition(firstPosition);
    }

    private void initView(){
        setOnScrollListener(new OnScrollListener(){
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState){
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && getLastVisiblePosition() + 1 == getLayoutManager().getItemCount()){
                    if(mLoadListener != null &&
                            mAdapter.getLoadState() == BaseRecyclerViewAdapter.STATE_DEFAULT &&
                            mAdapter.getCanLoadMore()){
                        mLoadListener.loadMore();
                        mAdapter.setLoadState(BaseRecyclerViewAdapter.STATE_LOADING);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                super.onScrolled(recyclerView, dx, dy);
                firstPosition = getFirstVisiblePosition();
            }
        });
    }

    /**
     *
     * @return
     */
    private int getLastVisiblePosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

    private int getFirstVisiblePosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findFirstVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMinPositions(lastPositions);
        } else {
            position = 0;
        }
        return position;
    }

    private int getMinPositions(int[] positions) {
        int size = positions.length;
        int minPosition = Integer.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            minPosition = Math.min(minPosition, positions[i]);
        }
        return minPosition;
    }
}
