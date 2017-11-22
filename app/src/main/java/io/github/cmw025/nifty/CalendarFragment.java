package io.github.cmw025.nifty;


import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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


public class CalendarFragment extends Fragment implements OnDateSelectedListener {

    // private DayPickerView calendarView;
    private ArrayList<ProjectModel> projectList;
    private MaterialCalendarView calendarView;
    private TodayDecorator todayDecorator;
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
        todayDecorator = new TodayDecorator();
        calendarView.addDecorator(todayDecorator);

        MaterialSpinner spinner = (MaterialSpinner) activity.findViewById(R.id.spinner);
        projectList = new ArrayList<>();
        // Initializing an ArrayAdapter
        ArrayAdapter<ProjectModel> spinnerArrayAdapter = new ArrayAdapter<ProjectModel>(
                getActivity(),R.layout.spinner_textview_align,projectList
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_textview_align);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                ProjectModel project = (ProjectModel) item;

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -2);
                ArrayList<CalendarDay> dates = new ArrayList<>();
                for (TaskModel task: project.giveMeTasks()) {
                    Log.v("tasks", "this is task:" + task);
                    CalendarDay day = CalendarDay.from(calendar);
                    dates.add(day);
                    calendar.add(Calendar.DATE, 5);
                }

                // Remove old decorators
                calendarView.removeDecorators();

                // Add new decorators
                calendarView.addDecorator(todayDecorator);
                calendarView.addDecorator(new EventDecorator(Color.RED, dates));
            }
//
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
//            {
//                String selectedItem = parent.getItemAtPosition(position).toString();
//                if(selectedItem.equals("Add new category"))
//                {
//                    // do your stuff
//                }
//            } // to close the onItemSelected
//            public void onNothingSelected(AdapterView<?> parent)
//            {
//
//            }
        });

        // Set up FireBase for project list
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        fb.child("usrs").child(uid).child("projects").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                projectList = new ArrayList<>();
                for (DataSnapshot child : data.getChildren()) {
                    ProjectModel project = child.getValue(ProjectModel.class);
                    String projectKey = project.getKey();
                    projectList.add(project);
                    fb.child("usrs").child(uid).child("projects").child(projectKey).child("tasks").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot tasks) {
                            List<TaskModel> newTaskList = new ArrayList<>();
                            for (DataSnapshot task : tasks.getChildren()) {
                                TaskModel mTask = task.getValue(TaskModel.class);
                                newTaskList.add(mTask);
                            }
                            project.replaceTasks(newTaskList);
                            projectList.add(project);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                spinnerArrayAdapter.clear();
                spinnerArrayAdapter.addAll(projectList);
                if (!projectList.isEmpty()) {
                    spinner.setText(projectList.get(0).getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());
//
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
