package com.novoda.androidskeleton;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class RowView extends LinearLayout {

    private TextView titleTextView;
    private RecyclerView recyclerView;

    public RowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        super.setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View.inflate(getContext(), R.layout.merge_row, this);

        titleTextView = (TextView) findViewById(R.id.row_title);
        recyclerView = (RecyclerView) findViewById(R.id.row_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    void update(Row row) {
        titleTextView.setText(row.heading);
        recyclerView.setAdapter(new ItemsAdapter(row.items));
    }

    private static class ItemsAdapter extends RecyclerView.Adapter {

        private final List<Item> items;

        ItemsAdapter(List<Item> items) {
            this.items = items;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item, parent, false);
            return new PlainViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Item item = items.get(position);
            ((TextView) holder.itemView).setText(item.title);
            holder.itemView.setBackgroundColor(item.color);
        }

        @Override
        public int getItemCount() {
            if (items == null) {
                return 0;
            }
            return items.size();
        }
    }
}
