package io.github.cmw025.nifty;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RowViewHolder> implements DraggableItemAdapter<RecyclerViewAdapter.RowViewHolder> {
    List<TaskModel> mItems;
    DatabaseReference fb;
    String uid;

    public RecyclerViewAdapter() {
        setHasStableIds(true); // this is required for D&D feature.
        mItems = new ArrayList<>();
        fb = FirebaseDatabase.getInstance().getReference();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
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

        if (item.getProjectKey() != null) {
            fb.child("usrs").child(uid).child("projects").child(item.getProjectKey()).child("color").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot data) {
                    if (data.getValue() != null) {
                        long l = (long) data.getValue();
                        int i = (int) l;
                        holder.colorTag.setBackgroundResource(i);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
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

    public void updateItems(List<TaskModel> newList) {
        mItems = newList;
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    class RowViewHolder extends AbstractDraggableItemViewHolder implements View.OnClickListener {

        TextView textView;
        TextView colorTag;

        RowViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            textView = itemView.findViewById(android.R.id.text1);
            colorTag = itemView.findViewById(R.id.color_tag);
        }

        @Override
        public void onClick(View view) {
            TaskModel clickedTask = mItems.get(getAdapterPosition());
            Activity activity = (Activity) view.getContext();
            // Log.v("fb", "The key of the item clicked: " + key);

            Intent intent = new Intent(activity, TaskDetailActivity.class);
            intent.putExtra("projectFireBaseKey", clickedTask.getProjectKey());
            intent.putExtra("taskFireBaseKey", clickedTask.getKey());
            intent.putExtra("taskListID", clickedTask.getId());
            activity.startActivity(intent);
            activity.overridePendingTransition(R.animator.slide_in_right_to_left, R.animator.slide_out_right_to_left);
        }
    }
}
