package io.github.cmw025.nifty;


import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;

import me.nlmartian.silkcal.DatePickerController;
import me.nlmartian.silkcal.DayPickerView;
import me.nlmartian.silkcal.SimpleMonthAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment implements OnDateSelectedListener {

    // private DayPickerView calendarView;
    private MaterialCalendarView calendarView;
    private TodayDecorator decorator;
    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        Activity activity = getActivity();
        calendarView = (MaterialCalendarView) activity.findViewById(R.id.calendarView);
        calendarView.setOnDateChangedListener(this);
        //calendarView.setTopbarVisible(false);
        decorator = new TodayDecorator();
        calendarView.addDecorator(decorator);

        MaterialSpinner spinner = (MaterialSpinner) activity.findViewById(R.id.spinner);

        String[] plants = new String[]{
                "nifty",
                "Math 280 Final",
                "ENGR100--FUCK MY LIFE",
                "Building a Snowman"
        };

        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                getActivity(),R.layout.spinner_textview_align,plants
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_textview_align);
        spinner.setAdapter(spinnerArrayAdapter);


//        EditText editText = (EditText) activity.findViewById(R.id.add_task);
//        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//                if (actionId == EditorInfo.IME_ACTION_SEND) {
//                    EditText editText = (EditText) getActivity().findViewById(R.id.add_task);
//                    editText.setText("");
//                    // editText.clearFocus();
//                }
//                return false;
//            }
//        });

        RecyclerView recyclerView = activity.findViewById(R.id.recycler_view_calendar);

        // Setup D&D feature and RecyclerView
        RecyclerViewDragDropManager dragMgr = new RecyclerViewDragDropManager();

        dragMgr.setInitiateOnMove(false);
        dragMgr.setInitiateOnLongPress(true);

        LinearLayoutManager mgr = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mgr);
        recyclerView.setAdapter(dragMgr.createWrappedAdapter(new TaskFragment.MyAdapter()));
        // recyclerView.setAdapter(dragMgr.createWrappedAdapter(arrayAdapter);

        dragMgr.attachRecyclerView(recyclerView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mgr.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());

    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        //If you change a decorate, you need to invalidate decorators
        TextView textView = (TextView) getActivity().findViewById(R.id.date);
        textView.setText(getSelectedDatesString());
//        decorator.setDate(date.getDate());
//        widget.invalidateDecorators();
    }

    private String getSelectedDatesString() {
        CalendarDay date = calendarView.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        return String.format("Tasks on " + FORMATTER.format(date.getDate()));
    }

    private class TodayDecorator implements DayViewDecorator {

        private final CalendarDay today;
        private final Drawable backgroundDrawable;

        public TodayDecorator() {
            today = CalendarDay.today();
            backgroundDrawable = getResources().getDrawable(R.drawable.today_circle_background);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return today.equals(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(backgroundDrawable);
        }
    }

    private class EventDecorator implements DayViewDecorator {

        private int color;
        private HashSet<CalendarDay> dates;
        private final Drawable backgroundDrawable;

        public EventDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
            backgroundDrawable = getResources().getDrawable(R.drawable.today_rect_background);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(backgroundDrawable);
            //view.addSpan(new DotSpan(5, color));
        }
    }


    static class MyItem {
        public final long id;
        public final String text;

        public MyItem(long id, String text) {
            this.id = id;
            this.text = text;
        }
    }

    static class MyViewHolder extends AbstractDraggableItemViewHolder {
        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }

    static class MyAdapter extends RecyclerView.Adapter<TaskFragment.MyViewHolder> implements DraggableItemAdapter<TaskFragment.MyViewHolder> {
        List<TaskFragment.MyItem> mItems;

        public MyAdapter() {
            setHasStableIds(true); // this is required for D&D feature.

            mItems = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                mItems.add(new TaskFragment.MyItem(i, "Task " + i));
            }
        }

        @Override
        public long getItemId(int position) {
            return mItems.get(position).id; // need to return stable (= not change even after reordered) value
        }

        @Override
        public TaskFragment.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_minimal, parent, false);
            return new TaskFragment.MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(TaskFragment.MyViewHolder holder, int position) {
            TaskFragment.MyItem item = mItems.get(position);
            holder.textView.setText(item.text);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public void onMoveItem(int fromPosition, int toPosition) {
            TaskFragment.MyItem movedItem = mItems.remove(fromPosition);
            mItems.add(toPosition, movedItem);
        }

        @Override
        public boolean onCheckCanStartDrag(TaskFragment.MyViewHolder holder, int position, int x, int y) {
            return true;
        }

        @Override
        public ItemDraggableRange onGetItemDraggableRange(TaskFragment.MyViewHolder holder, int position) {
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
    }

    /**
     * Simulate an API call to show how to add decorators
     */
    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -2);
            ArrayList<CalendarDay> dates = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                CalendarDay day = CalendarDay.from(calendar);
                dates.add(day);
                calendar.add(Calendar.DATE, 5);
            }

            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (getActivity().isFinishing()) {
                return;
            }

            calendarView.addDecorator(new EventDecorator(Color.RED, calendarDays));
        }
    }
}
