package com.project.ken.botec;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.ken.botec.models.SalesLog;

import java.util.List;


public class SalesLogRecyclerAdapter extends RecyclerView.Adapter<SalesLogRecyclerAdapter.ViewHolder> {
    private final List<SalesLog.SaleLog> mValues;
    private final Context mContext;

    public SalesLogRecyclerAdapter(List<SalesLog.SaleLog> items, Context c) {
        mValues = items;
        mContext = c;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.log_list_content, parent, false);
        return new SalesLogRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SalesLogRecyclerAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mItemName.setText(mValues.get(position).item_name);
        holder.mQuantity.setText(mValues.get(position).quantity);
        holder.mDate.setText(mValues.get(position).date);
        holder.mSalesPrice.setText("Shs "+(Integer.parseInt(mValues.get(position).unit_price) * Integer.parseInt(mValues.get(position).quantity)) +
                " @ " + mValues.get(position).unit_price);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mItemName;
        public final TextView mQuantity;
        public final TextView mDate;
        public final TextView mSalesPrice;
        public SalesLog.SaleLog mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mItemName = view.findViewById(R.id.item_name);
            mQuantity = view.findViewById(R.id.quantity);
            mDate = view.findViewById(R.id.date);
            mSalesPrice = view.findViewById(R.id.total_price);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItemName.getText() + "'";
        }
    }
}
