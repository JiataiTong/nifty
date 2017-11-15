package io.github.cmw025.nifty;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;

import java.util.ArrayList;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RowViewHolder> implements DraggableItemAdapter<RowViewHolder> {
    List<MyItem> mItems;
    private RecyclerViewClickListener mListener;

    public RecyclerViewAdapter(RecyclerViewClickListener listener) {
        setHasStableIds(true); // this is required for D&D feature.

        mItems = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            mItems.add(new MyItem(i, "Item " + i));
//        }
        mListener = listener;
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).id; // need to return stable (= not change even after reordered) value
    }

    @Override
    public RowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_minimal, parent, false);
        return new RowViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(RowViewHolder holder, int position) {
        MyItem item = mItems.get(position);
        holder.textView.setText(item.text);
//
//        if (holder instanceof RowViewHolder) {
//            RowViewHolder rowHolder = (RowViewHolder) holder;
//            //set values of data here
//        }


    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        MyItem movedItem = mItems.remove(fromPosition);
        mItems.add(toPosition, movedItem);
    }

    @Override
    public boolean onCheckCanStartDrag(RowViewHolder holder, int position, int x, int y) {
        return true;
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(RowViewHolder holder, int position) {
        return null;
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }

    @Override
    public void onItemDragStarted(int position) {
    }

    @Override
    public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
    }

    public void addItem( MyItem item) {
        mItems.add(item);
        notifyItemInserted(mItems.size());
    }

    static class MyItem {
        public final long id;
        public final String text;

        public MyItem(long id, String text) {
            this.id = id;
            this.text = text;
        }
    }
}
