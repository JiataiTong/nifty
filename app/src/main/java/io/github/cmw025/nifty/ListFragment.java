package io.github.cmw025.nifty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;


public class ListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        // Setup item onClick listener
        RecyclerViewClickListener listener = (view, position) -> {
            // Toast.makeText(getActivity(), "Position " + position, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
            Activity activity = getActivity();
            activity.startActivity(intent);
            activity.overridePendingTransition(R.animator.slide_in_right_to_left, R.animator.slide_out_right_to_left);
        };

        // Setup D&D feature and RecyclerView
        RecyclerViewDragDropManager dragMgr = new RecyclerViewDragDropManager();

        dragMgr.setInitiateOnMove(false);
        dragMgr.setInitiateOnLongPress(true);

        RecyclerView recyclerView =  (RecyclerView) v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mgr = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mgr);

        // Divider decoration
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), mgr.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Set Adapter
        recyclerView.setAdapter(dragMgr.createWrappedAdapter(new RecyclerViewAdapter(listener)));

        // NOTE: need to disable change animations to ripple effect work properly
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        dragMgr.attachRecyclerView(recyclerView);

        return v;
    }
}
