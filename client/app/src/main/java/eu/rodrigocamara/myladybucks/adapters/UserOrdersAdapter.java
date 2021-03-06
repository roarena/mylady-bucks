package eu.rodrigocamara.myladybucks.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.rodrigocamara.myladybucks.R;
import eu.rodrigocamara.myladybucks.listeners.ClickListeners;
import eu.rodrigocamara.myladybucks.pojos.Coffee;
import eu.rodrigocamara.myladybucks.pojos.Order;
import eu.rodrigocamara.myladybucks.utils.C;
import eu.rodrigocamara.myladybucks.utils.Utils;

/**
 * Created by Rodrigo Câmara on 16/01/2018.
 */

public class UserOrdersAdapter extends RecyclerView.Adapter<UserOrdersAdapter.MyViewHolder> {
    private List<Order> orderList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private Context context;
    private int mTotalValue;
    private int widgetId = 0;
    private Activity widgetConfigureActivity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_user_order_coffees)
        TextView mTvCoffees;
        @BindView(R.id.tv_user_order_date)
        TextView mTvDate;
        @BindView(R.id.tv_user_order_price)
        TextView mTvTotalPrice;
        @BindView(R.id.tv_user_order_status)
        TextView mTvOrderStatus;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

    public UserOrdersAdapter(List<Order> orderList, Context context) {
        this.context = context;
        this.orderList = orderList;
    }

    public UserOrdersAdapter(List<Order> orderList, Context context, int widgetId, Activity widgetConfigureActivity) {
        this.context = context;
        this.orderList = orderList;
        this.widgetId = widgetId;
        this.widgetConfigureActivity = widgetConfigureActivity;
    }

    @Override
    public UserOrdersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_order_item, parent, false);
        return new UserOrdersAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final UserOrdersAdapter.MyViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.mTvDate.setText(dateFormat.format(order.getmDate()));
        holder.mTvCoffees.setText(getCoffees(order.getmCoffeeList()));
        holder.mTvTotalPrice.setText(Currency.getInstance(Locale.getDefault()).getSymbol() + String.valueOf(Utils.getFinalOrderValue(order.getmCoffeeList())));
        holder.mTvOrderStatus.setText(order.getmStatus());
        holder.mTvOrderStatus.setTextColor(setStatusColor(order.getmStatus()));
        if (widgetId == 0) {
            holder.itemView.setOnClickListener(ClickListeners.goToOrderDetail(context, order.getmCoffeeList()));
        } else {
            holder.itemView.setOnClickListener(ClickListeners.setAsFavoriteCoffee(context, order.getmCoffeeList(), widgetId, widgetConfigureActivity));
        }

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private String getCoffees(List<Coffee> coffeeList) {
        StringBuilder order = new StringBuilder();
        for (Coffee coffee : coffeeList) {
            order.append(coffee.getQuantity()).append(" ").append(coffee.getName()).append("\n");
            mTotalValue = mTotalValue + coffee.getPrice();
        }
        return order.toString();
    }

    private int setStatusColor(String statusColor) {
        switch (statusColor) {
            case C.STATUS_NEW:
                return context.getResources().getColor(R.color.colorPrimary);
            case C.STATUS_IN_PROGRESS:
                return context.getResources().getColor(R.color.status_progress);
            case C.STATUS_READY:
                return context.getResources().getColor(R.color.status_ready);
            case C.STATUS_FINISHED:
                return context.getResources().getColor(R.color.status_finished);
            default:
                return context.getResources().getColor(R.color.colorPrimary);
        }
    }
}
