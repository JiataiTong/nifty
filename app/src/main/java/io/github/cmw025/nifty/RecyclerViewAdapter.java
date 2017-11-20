package io.github.cmw025.nifty;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import java.util.ArrayList;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RowViewHolder> implements DraggableItemAdapter<RecyclerViewAdapter.RowViewHolder> {
    List<TaskModel> mItems;

    public RecyclerViewAdapter() {
        setHasStableIds(true); // this is required for D&D feature.
        mItems = new ArrayList<>();
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getId(); // need to return stable (= not change even after reordered) value
    }

    @Override
    public RowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_minimal, parent, false);
        return new RowViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RowViewHolder holder, int position) {
        TaskModel item = mItems.get(position);
        holder.textView.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        TaskModel movedItem = mItems.remove(fromPosition);
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

    public void addItem( TaskModel item) {
        mItems.add(item);
        notifyItemInserted(mItems.size());
    }

    public void updateItems(List<TaskModel> newList) {
        mItems = newList;
        notifyDataSetChanged();
    }
//
//    static class MyItem {
//        public final long id;
//        public final String text;
//
//        public MyItem(long id, String text) {
//            this.id = id;
//            this.text = text;
//        }
//    }

    class RowViewHolder extends AbstractDraggableItemViewHolder implements View.OnClickListener {

        TextView textView;

        RowViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            textView = itemView.findViewById(android.R.id.text1);
        }

        @Override
        public void onClick(View view) {
            TaskModel clickedTask = mItems.get(getAdapterPosition());
            Activity activity = (Activity) view.getContext();
            String key = clickedTask.getKey();
            // Log.v("fb", "The key of the item clicked: " + key);

            Intent intent = new Intent(activity, TaskDetailActivity.class);
            intent.putExtra("taskID", key);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.animator.slide_in_right_to_left, R.animator.slide_out_right_to_left);
        }
    }
}
