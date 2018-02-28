package com.yatang.xc.xcr.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yatang.xc.xcr.R;
import com.yatang.xc.xcr.uitls.Common;

import org.jocerly.jcannotation.adapter.BaseRecyclerViewAdapter;
import org.jocerly.jcannotation.holder.BaseViewHolder;
import org.jocerly.jcannotation.holder.FooterHolder;
import org.jocerly.jcannotation.utils.JCLoger;
import org.jocerly.jcannotation.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dengjiang on 2017/6/8.
 */

public class OrderListAdapter extends BaseRecyclerViewAdapter<ConcurrentHashMap<String, String>, BaseViewHolder> {
    public static final String TAG = "OrderListAdapter";
    public static final String STATUS_TOACCEPT = "2";//待接单
    public static final String STATUS_TODELIVER = "3";//待发货
    public static final String STATUS_DELIVERRED = "31";//已发货
    public static final String STATUS_FINISHED = "4";//已完成
    public static final String STATUS_NOTPAY = "5";//已取消未支付
    public static final String STATUS_REFUSED = "6";//已拒绝
    public static final String STATUS_REFUSED_I = "1016";//退款中(商家拒单)
    public static final String STATUS_REFUSED_F = "1026";//退款中(商家拒单)
    public static final String STATUS_RETURNNED_REFUSED = "1036";//已退款(商家拒绝)
    public static final String STATUS_NOTACCEPT = "7";//已取消未接单
    public static final String STATUS_CUSTOMCANCELED_I = "10132";//退款中 客户取消
    public static final String STATUS_CUSTOMCANCELED_F = "10232";//退款中 客户取消
    public static final String STATUS_RETURNING_TIMEOUT_I = "1017";//退款中，超时未接单
    public static final String STATUS_RETURNING_TIMEOUT_F = "1027";//退款中，超时未接单
    public static final String STATUS_CUSTOMREFUSED_I = "10133";//退款中 客户拒收
    public static final String STATUS_CUSTOMREFUSED_F = "10233";//退款中 客户拒收
    public static final String STATUS_RETURNED_CUSTOMCANCELED = "10332";//已退款 客户取消
    public static final String STATUS_RETURNNED_TIMEOUT = "1037";//已退款 超时未接单
    public static final String STATUS_RETURNNED_CUSTOMREFUSED = "10333";//已退款 客户拒绝
    public static final String STATUS_RETURNING_GOODS = "201";//退货中 待审核
    public static final String STATUS_RETURNING_MONEY = "202";//退款中
    public static final String STATUS_RETURNNED_MONEY = "203";//已退款
    public static final String STATUS_RETURN_CANCEL = "205";//已取消
    public static final String STATUS_RETURN_REJECT = "204";//已拒绝

    public static final String DELIVER_TYPE_BUSINESS = "101";
    public static final String DELIVER_TYPE_CUSTOM = "102";
    public static final String DELIVER_TYPE_FENGNIAO = "103";
    public static ConcurrentHashMap<String, String> MAPSTATUS;

    private Context context;
    private OnItemClickListener onItemClickListener;
    private boolean needShowStatus = false;
    private boolean isReturnOrders = false;

    static {
        MAPSTATUS = new ConcurrentHashMap();
        MAPSTATUS.put(STATUS_TOACCEPT, "待接单");
        MAPSTATUS.put(STATUS_TODELIVER, "待发货");
        MAPSTATUS.put(STATUS_DELIVERRED, "已发货");
        MAPSTATUS.put(STATUS_FINISHED, "已完成");
        MAPSTATUS.put(STATUS_NOTPAY, "已经取消(未支付)");
        MAPSTATUS.put(STATUS_REFUSED, "已拒绝");
        MAPSTATUS.put(STATUS_REFUSED_I, "退款中(商家拒单)");
        MAPSTATUS.put(STATUS_REFUSED_F, "退款中(商家拒单)");
        MAPSTATUS.put(STATUS_RETURNNED_REFUSED, "已退款(商家拒单)");
        MAPSTATUS.put(STATUS_NOTACCEPT, "已取消(未接单)");
        MAPSTATUS.put(STATUS_CUSTOMCANCELED_I, "退款中(客户取消)");
        MAPSTATUS.put(STATUS_CUSTOMCANCELED_F, "退款中(客户取消)");
        MAPSTATUS.put(STATUS_RETURNING_TIMEOUT_I, "退款中(超时未接单)");
        MAPSTATUS.put(STATUS_RETURNING_TIMEOUT_F, "退款中(超时未接单)");
        MAPSTATUS.put(STATUS_CUSTOMREFUSED_I, "退款中(客户拒收)");
        MAPSTATUS.put(STATUS_CUSTOMREFUSED_F, "退款中(客户拒收)");
        MAPSTATUS.put(STATUS_RETURNED_CUSTOMCANCELED, "已退款(客户取消)");
        MAPSTATUS.put(STATUS_RETURNNED_TIMEOUT, "已退款(超时未接单)");
        MAPSTATUS.put(STATUS_RETURNNED_CUSTOMREFUSED, "已退款(客户拒收)");
        MAPSTATUS.put(STATUS_RETURNING_GOODS, "待审核");
        MAPSTATUS.put(STATUS_RETURNING_MONEY, "退款中");
        MAPSTATUS.put(STATUS_RETURNNED_MONEY, "已退款");
        MAPSTATUS.put(STATUS_RETURN_CANCEL, "已取消");
        MAPSTATUS.put(STATUS_RETURN_REJECT, "已拒绝");
    }

    public OrderListAdapter(Context context, List<ConcurrentHashMap<String, String>> listData, boolean needShowStatus) {
        super(context, listData);
        this.context = context;
        this.needShowStatus = needShowStatus;
    }

    /**
     * 设置列表数据
     *
     * @param listData 需要显示的列表数据
     */
    public void setList(List<ConcurrentHashMap<String, String>> listData, boolean isReturnOrders) {
        this.listData = listData;
        this.isReturnOrders = isReturnOrders;
    }

    @Override
    public void bindDataToItemView(BaseViewHolder baseViewHolder, int position) {
        if (baseViewHolder instanceof ViewHolder) {
            //常规viewHolder
            ViewHolder holder = (ViewHolder) baseViewHolder;
            final ConcurrentHashMap<String, String> entity = listData.get(position);
            holder.textNO.setText("#" + entity.get("SortNo"));
            String temp = entity.get("GoodsNum");
            if (!StringUtils.isEmpty(temp)) {
                String value = ("共" + temp + "件商品");
                holder.textGoodsCount.setText(getRedString(value, 0, 0 + temp.length() + 1));
            }

            String time = entity.get("CreateOrderTime");
            time = Common.stampToDate(time, "MM-dd HH:mm");
            if (isReturnOrders) {
                holder.text_Price.setText("退款:");
                holder.textTime.setText("申请时间:" + time);
                holder.textDeliveryType.setText("");
                if(DELIVER_TYPE_CUSTOM.equals(entity.get("DeliveryCode"))) {
                    holder.relayName.setVisibility(View.GONE);
                    holder.textAddress.setVisibility(View.GONE);
                } else {
                    holder.relayName.setVisibility(View.VISIBLE);
                    holder.textAddress.setVisibility(View.VISIBLE);
                }
                temp = entity.get("ReturnOrderPrice");
                if (!StringUtils.isEmpty(temp)) {
                    temp = "￥" + Common.formatFloat(temp);
                    holder.textPrice.setText(temp);
                }
            } else {
                holder.text_Price.setText("实付款:");
                holder.textTime.setText("下单时间:" + time);
                setDeliverType(holder.relayName, holder.textAddress,holder.textDeliveryType, entity.get("DeliveryCode"));
                temp = entity.get("OrderValue");
                if (!StringUtils.isEmpty(temp)) {
                    temp = "￥" + Common.formatFloat(temp);
                    holder.textPrice.setText(temp);
                }
            }

            holder.textName.setText(entity.get("AccountName"));
            holder.textPhone.setText(entity.get("Phone"));
            holder.textAddress.setText(entity.get("Address"));
            holder.setList(entity.get("PicList"), entity.get("OrderNo"), entity.get("CancelId"));
            String orderStatue = entity.get("OrderStatue");
            String deliveryType = entity.get("DeliveryCode");
            holder.textLeftTime.setText("");
            JCLoger.debug(TAG, "==orderStatue=" + orderStatue);
            if (StringUtils.isEmpty(orderStatue)) {
                return;
            }
            switch (orderStatue) {
                case STATUS_TOACCEPT:
                    //待接单
                    holder.btn1.setVisibility(View.VISIBLE);
                    holder.btn2.setVisibility(View.VISIBLE);
                    holder.btn3.setVisibility(View.VISIBLE);
                    holder.btn1.setText("拒绝");
                    holder.btn2.setText("联系客户");
                    holder.btn3.setText("接单");
                    holder.relaButton.setVisibility(View.VISIBLE);
                    temp = entity.get("ResidualOrderTime");
                    if (!StringUtils.isEmpty(temp)) {
                        int leftTime = Integer.parseInt(temp);
                        if (leftTime > 0) {
                            holder.textLeftTime.setText(getSpannableString("剩余" + leftTime + "分钟"));
                        }
                    }
                    break;
                case STATUS_TODELIVER:
                    //待发货
                    holder.btn1.setVisibility(View.GONE);
                    holder.btn2.setVisibility(View.VISIBLE);
                    holder.btn3.setVisibility(View.VISIBLE);
                    holder.btn2.setText("联系客户");
                    holder.btn3.setText("确认发货");
                    holder.relaButton.setVisibility(View.VISIBLE);
                    if (DELIVER_TYPE_FENGNIAO.equals(deliveryType)) {
                        //蜂鸟配送
                        holder.btn3.setVisibility(View.GONE);
                    } else if(DELIVER_TYPE_CUSTOM.equals(deliveryType)) {
                        //用户自提
                        holder.btn3.setText("确认完成");
//                        holder.btn2.setVisibility(View.GONE);

                    }
                    break;
                case STATUS_DELIVERRED:
                    //已发货
                    holder.btn1.setVisibility(View.VISIBLE);
                    holder.btn2.setVisibility(View.VISIBLE);
                    holder.btn3.setVisibility(View.VISIBLE);
                    holder.btn3.setText("配送完成");
                    holder.btn2.setText("联系客户");
                    holder.btn1.setText("客户拒收");
                    holder.relaButton.setVisibility(View.VISIBLE);
                    if (DELIVER_TYPE_FENGNIAO.equals(deliveryType)) {
                        //蜂鸟配送
//                        holder.btn1.setVisibility(View.GONE);
                        holder.btn3.setText("配送信息");
                    }
                    break;
                case STATUS_RETURNING_GOODS:
                    //退货中
                    holder.btn1.setVisibility(View.VISIBLE);
                    holder.btn2.setVisibility(View.VISIBLE);
                    holder.btn3.setVisibility(View.VISIBLE);
                    holder.btn3.setText("确认退货");
                    holder.btn2.setText("联系客户");
                    holder.btn1.setText("拒绝");
                    holder.relaButton.setVisibility(View.VISIBLE);
                    break;
                default:
                    holder.btn3.setVisibility(View.GONE);
                    holder.btn2.setVisibility(View.GONE);
                    holder.btn1.setVisibility(View.GONE);
                    holder.relaButton.setVisibility(View.GONE);
                    break;
            }
            if (needShowStatus) {
                holder.text_finish.setVisibility(View.VISIBLE);
                holder.text_finish.setText(MAPSTATUS.get(orderStatue));
            } else {
                holder.text_finish.setVisibility(View.GONE);
            }
            holder.btn3.setOnClickListener(new MClickListener(position, orderStatue, deliveryType));
            holder.btn2.setOnClickListener(new MClickListener(position, orderStatue, deliveryType));
            holder.btn1.setOnClickListener(new MClickListener(position, orderStatue, deliveryType));
            baseViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.itemClick(entity.get("OrderNo"), entity.get("CancelId"));
                }
            });
            holder.imgClick.setOnClickListener(new View.OnClickListener() {
                //为了item中横向图片展示区域也能点击促发 item 点击事件
                @Override
                public void onClick(View v) {
                    onItemClickListener.itemClick(entity.get("OrderNo"), entity.get("CancelId"));
                }
            });
            if (position == 0) {
                holder.linTop.setVisibility(View.GONE);
            } else {
                holder.linTop.setVisibility(View.VISIBLE);
            }
            if (position == listData.size() - 1) {
                holder.lineBottom.setVisibility(View.GONE);
            } else {
                holder.lineBottom.setVisibility(View.VISIBLE);
            }
        } else if (baseViewHolder instanceof FooterHolder) {
            final FooterHolder footerHolder = (FooterHolder) baseViewHolder;

            footerHolder.mFooterTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getLoadState() == BaseRecyclerViewAdapter.STATE_FAILURE) {
                        onItemClickListener.OnFooterClick();
                        footerHolder.mFooterTextView.setText("加载中···");
                        footerHolder.mProgressBar.setVisibility(View.VISIBLE);
                        setLoadState(BaseRecyclerViewAdapter.STATE_LOADING);
                    }
                }
            });
        }
    }

    @Override
    public BaseViewHolder createViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    private class ViewHolder extends BaseViewHolder {
        private TextView textNO;
        private TextView textTime;
        private TextView textName;
        private TextView textPhone;
        private TextView textAddress;
        private TextView textPrice;
        private TextView text_Price;
        private TextView textGoodsCount;
        private TextView text_finish;
        private TextView textDeliveryType;
        private RelativeLayout relaButton;
        private TextView btn3;
        private TextView btn2;
        private TextView btn1;
        private ImageView imgClick;
        private RecyclerView recyclerView;
        private GoodsPicAdapter mAdapter;
        private List<String> pictdata;
        private View linTop;
        private TextView textLeftTime;
        private LinearLayout lineBottom;
        private RelativeLayout relayName;

        public ViewHolder(View itemView) {
            super(itemView);
            textNO = (TextView) itemView.findViewById(R.id.textNO);
            textDeliveryType = (TextView) itemView.findViewById(R.id.textDeliveryType);
            textTime = (TextView) itemView.findViewById(R.id.textTime);
            textName = (TextView) itemView.findViewById(R.id.textName);
            textPhone = (TextView) itemView.findViewById(R.id.textPhone);
            textAddress = (TextView) itemView.findViewById(R.id.textAddress);
            textPrice = (TextView) itemView.findViewById(R.id.textPrice);
            text_Price = (TextView) itemView.findViewById(R.id.text_Price);
            textGoodsCount = (TextView) itemView.findViewById(R.id.textGoodsCount);
            text_finish = (TextView) itemView.findViewById(R.id.text_finish);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
            relaButton = (RelativeLayout) itemView.findViewById(R.id.relaButton);
            imgClick = (ImageView) itemView.findViewById(R.id.imgClick);
            btn3 = (TextView) itemView.findViewById(R.id.btn3);
            btn2 = (TextView) itemView.findViewById(R.id.btn2);
            btn1 = (TextView) itemView.findViewById(R.id.btn1);
            linTop = itemView.findViewById(R.id.linTop);
            textLeftTime = (TextView) itemView.findViewById(R.id.textLeftTime);
            lineBottom = (LinearLayout) itemView.findViewById(R.id.lineBottom);
            relayName = (RelativeLayout)itemView.findViewById(R.id.relayName);
            pictdata = new ArrayList<>();
            mAdapter = new GoodsPicAdapter(itemView.getContext(), pictdata);
            //设置布局管理器
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            //设置适配器
            recyclerView.setAdapter(mAdapter);

        }

        /**
         * 设置每一个item 的 商品图片list
         */
        private void setList(String json, final String OrderNo, final String CancelId) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                pictdata.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    pictdata.add((String) jsonArray.getJSONObject(i).get("PicUrl"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mAdapter.notifyDataSetChanged();
            mAdapter.setOnPicListclickListenner(new GoodsPicAdapter.OnPicListclickListenner() {
                @Override
                public void onPicClick() {
                    if (onItemClickListener != null) {
                        onItemClickListener.itemClick(OrderNo, CancelId);
                    }

                }
            });
        }
    }

    /**
     * 订单操作按钮点击监听器
     */
    public class MClickListener implements View.OnClickListener {
        private int position;
        private String orderStatue;
        private String deliveryType;

        public MClickListener(int position, String orderStatue, String deliveryType) {
            this.orderStatue = orderStatue;
            this.position = position;
            this.deliveryType = deliveryType;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn3:
                    if (STATUS_TOACCEPT.equals(orderStatue)) {
                        //接单
                        onItemClickListener.onAcceptClick(listData.get(position).get("OrderNo"));
                    } else if (STATUS_TODELIVER.equals(orderStatue)) {
                        if(DELIVER_TYPE_CUSTOM.equals(deliveryType)) {
                            JCLoger.debug("Custom finish");
                            //客户自提 确认完成
                            onItemClickListener.onFinishClick(listData.get(position).get("OrderNo"));
                        } else {
                            //确认发货
                            JCLoger.debug("onDeliverredClick");
                            onItemClickListener.onDeliverredClick(listData.get(position).get("OrderNo"));
                        }

                    } else if (STATUS_DELIVERRED.equals(orderStatue)) {
                        if (DELIVER_TYPE_FENGNIAO.equals(deliveryType)) {
                            //配送信息
                            onItemClickListener.onDeliverInfoClick(listData.get(position));
                        } else {
                            //完成
                            onItemClickListener.onFinishClick(listData.get(position).get("OrderNo"));
                        }
                    } else if (STATUS_RETURNING_GOODS.equals(orderStatue)) {
                        //确认退货
                        onItemClickListener.onReturnedClick(listData.get(position).get("CancelId"));
                    }
                    break;
                case R.id.btn2:
                    //联系用户
                    MobclickAgent.onEvent(context, "Order_Call");

                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + listData.get(position).get("Phone")));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    break;
                case R.id.btn1:
                    if (STATUS_DELIVERRED.equals(orderStatue)) {
                        //客户拒绝
                        onItemClickListener.onCustomRefuseClick(listData.get(position).get("OrderNo"));
                    } else if (STATUS_TOACCEPT.equals(orderStatue)) {
                        //商家拒接
                        onItemClickListener.onRefuseClick(listData.get(position).get("OrderNo"));
                    } else if (STATUS_RETURNING_GOODS.equals(orderStatue)) {
                        //拒绝退货
                        onItemClickListener.onRefuseReturnClick(listData.get(position).get("CancelId"));
                    }
                    break;
            }
        }
    }

    public interface OnItemClickListener {
        /**
         * 点击item项目，进入订单详情
         */
        void itemClick(String OrderNo, String CancelId);

        /**
         * 点击底部item，加载更多
         */
        void OnFooterClick();

        /**
         * 拒绝订单
         */
        void onRefuseClick(String OrderNo);

        /**
         * 接单
         */
        void onAcceptClick(String OrderNo);

        /**
         * 完成订单
         */
        void onFinishClick(String OrderNo);

        /**
         * 客户拒绝订单
         */
        void onCustomRefuseClick(String OrderNo);

        /**
         * 确认发货
         */
        void onDeliverredClick(String OrderNo);

        /**
         * 确认退货
         */
        void onReturnedClick(String cancelId);

        /**
         * 拒绝退货
         */
        void onRefuseReturnClick(String cancelId);

        /**
         * 配送信息
         */
        void onDeliverInfoClick(ConcurrentHashMap<String, String> mapData);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置 string 部分红色显示
     */
    private SpannableString getSpannableString(String str) {
        SpannableString s = new SpannableString(str);
        int start = str.indexOf("余");
        int end = str.indexOf("分");
        s.setSpan(new ForegroundColorSpan(Color.RED), start + 1, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }

    /**
     * 显示配送信息
     */
    private void setDeliverType(RelativeLayout relay,TextView textAddress, TextView textView, String deliverType) {
        if(StringUtils.isEmpty(deliverType)) {
            deliverType = DELIVER_TYPE_CUSTOM;
        }
        switch (deliverType) {
            case DELIVER_TYPE_BUSINESS:
                textView.setText("商家配送");
                relay.setVisibility(View.VISIBLE);
                textAddress.setVisibility(View.VISIBLE);
                break;
            case DELIVER_TYPE_CUSTOM:
                textView.setText("客户自提");
                relay.setVisibility(View.GONE);
                textAddress.setVisibility(View.GONE);
                break;
            case DELIVER_TYPE_FENGNIAO:
                textView.setText("蜂鸟配送");
                relay.setVisibility(View.VISIBLE);
                textAddress.setVisibility(View.VISIBLE);
                break;
        }
    }


    /**
     * 设置 string 部分红色显示
     *
     * @param start 开始indext
     * @param end   结束indext
     */
    private SpannableString getRedString(String str, int start, int end) {
        SpannableString s = new SpannableString(str);
        s.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red)), start + 1, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }
}
