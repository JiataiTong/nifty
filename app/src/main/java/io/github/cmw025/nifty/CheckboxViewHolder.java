package io.github.cmw025.nifty;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.CheckBox;

import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

public class CheckboxViewHolder extends AbstractDraggableItemViewHolder implements View.OnClickListener {

    private RecyclerViewClickListener mListener;
    TextView textView;
    CheckBox checkBox;

    CheckboxViewHolder(View v, RecyclerViewClickListener listener) {
        super(v);
        mListener = listener;
        v.setOnClickListener(this);
        textView = itemView.findViewById(android.R.id.text1);
        checkBox = itemView.findViewById(android.R.id.checkbox);
    }

    @Override
    public void onClick(View view) {
        mListener.onClick(view, getAdapterPosition());
    }
}