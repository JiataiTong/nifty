package io.github.cmw025.nifty;


import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Html;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;

import me.nlmartian.silkcal.DatePickerController;
import me.nlmartian.silkcal.DayPickerView;
import me.nlmartian.silkcal.SimpleMonthAdapter;


public class CalendarFragment extends Fragment implements OnDateSelectedListener, MaterialSpinner.OnItemSelectedListener{

    private Activity activity;

    private DatabaseReference fb;
    private String userDisplayName;
    private String projectFireBaseID;
    private String uid;

    private ArrayList<CalendarDay> dates;
    private ArrayList<TaskModel> tasks;
    private ArrayList<ProjectModel> projectList;

    private MaterialCalendarView calendarView;
    private TodayDecorator todayDecorator;
    private EventDecorator eventDecorator;

    private MaterialSpinner spinner;
    private ArrayAdapter<ProjectModel> spinnerArrayAdapter;
    private RecyclerViewAdapter recyclerViewAdapter;

    private ProjectModel firstProject;

    // Required empty public constructor
    public CalendarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for list fragment
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Setup D&D feature and RecyclerView
        RecyclerViewDragDropManager dragMgr = new RecyclerViewDragDropManager();

        dragMgr.setInitiateOnMove(false);
        dragMgr.setInitiateOnLongPress(true);

        RecyclerView recyclerView =  (RecyclerView) v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mgr = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mgr);

        // Divider decoration
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), mgr.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Set Adapter
        recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(dragMgr.createWrappedAdapter(recyclerViewAdapter));

        // NOTE: need to disable change animations to ripple effect work properly
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        dragMgr.attachRecyclerView(recyclerView);

        return v;
    }


    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        activity = getActivity();

        projectList = new ArrayList<>();
        tasks = new ArrayList<>();
        dates = new ArrayList<>();

        initFireBase();
        initSpinner();
        initEditText();
    }

    public void initFireBase() {
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userDisplayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        fb = FirebaseDatabase.getInstance().getReference();
    }

    public void initSpinner() {
        spinner = activity.findViewById(R.id.spinner);
        spinnerArrayAdapter = new ArrayAdapter<ProjectModel>(
                activity, R.layout.spinner_textview_align, projectList
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_textview_align);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(this);
        fillSpinnerWithProjectList();
    }

    // Helper method to fill spinner with project list
    public void fillSpinnerWithProjectList() {
        fb.child("usrs").child(uid).child("projects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                projectList = new ArrayList<>();
                for (DataSnapshot child : data.getChildren()) {
                    ProjectModel project = child.getValue(ProjectModel.class);
                    projectList.add(project);
                    Log.v("project", project.toString());
                }
//                // Initialize event decorator for first project in the list
//                if (!projectList.isEmpty() && getActivity()!= null) {
//                    fb.child("usrs").child(uid).child("projects").child(projectList.get(0).getKey()).child("tasks").addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot tasks) {
//                            ArrayList<CalendarDay> dates = new ArrayList<>();
//                            for (DataSnapshot task : tasks.getChildren()) {
//                                TaskModel mTask = task.getValue(TaskModel.class);
//                                Date dueDate = mTask.getDueDate();
//                                CalendarDay day = CalendarDay.from(dueDate);
//                                dates.add(day);
//                            }
//
//                            int projectColor = projectList.get(0).getColor();
//                            int realColor = 0;
//                            if (getContext()!= null) {
//                                realColor = ContextCompat.getColor(getContext(), projectColor);
//                            }
//
//                            EventDecorator eventDecorator = new EventDecorator(realColor, dates);
//                            calendarView.addDecorator(eventDecorator);
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }


                // Update spinner list
                spinnerArrayAdapter.clear();
                spinnerArrayAdapter.addAll(projectList);
                if (getActivity()!= null) {
                    initCalendar();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void initCalendar() {
        calendarView = (MaterialCalendarView) activity.findViewById(R.id.calendarView);
        calendarView.setOnDateChangedListener(this);

        Date today = new Date();
        // Default to placeholder if project list is empty
        firstProject = new ProjectModel("Project List", "", today, today, 0, uid, R.color.light_red);

        if (!projectList.isEmpty()) {
            firstProject = projectList.get(0);
        }

        projectFireBaseID = firstProject.getKey();

        int realColor = ContextCompat.getColor(getContext(), firstProject.getColor());
        calendarView.setSelectionColor(realColor);

        fb.child("projects").child(firstProject.getKey()).child("tasks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                dates = new ArrayList<>();
                for (DataSnapshot child : data.getChildren()) {
                    TaskModel task = child.getValue(TaskModel.class);
                    dates.add(CalendarDay.from(task.getDueDate()));
                }
                todayDecorator = new TodayDecorator(realColor);
                calendarView.addDecorator(todayDecorator);

                if (!dates.isEmpty()) {
                    eventDecorator = new EventDecorator(realColor, dates);
                    calendarView.addDecorator(eventDecorator);
                }
                // Update UI to display project name
                String colorString = Integer.toString(realColor, 16);
                String text = "<font color=#ffffff>"+ firstProject.getName() + "</font> <font color=#" + colorString + ">   ⬤</font>";
                spinner.setText(Html.fromHtml(text));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void initEditText() {
        // Set up add new task
        EditText toDo = activity.findViewById(R.id.add_todo);
        toDo.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, @NonNull KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event == null ||
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            String title = toDo.getText().toString();

                            if (!TextUtils.isEmpty(title)) {
                                // Get reference to "fb/tasks/new_task"
                                DatabaseReference ref = fb.child("tasks").push();
                                String taskKey = ref.getKey();

                                // Create TaskModel
                                long id = longHash(taskKey);
                                TaskModel task = new TaskModel(title, "", id, taskKey, projectFireBaseID);
                                task.setDueDate(calendarView.getSelectedDate().getDate());

                                // Add task to FireBase
                                ref.setValue(task);
                                fb.child("usrs").child(uid).child("tasks").child(taskKey).setValue(task);
                                fb.child("usrs").child(uid).child("projects").child(projectFireBaseID).child("tasks").child(taskKey).setValue(task);

                                // Get project ref
                                DatabaseReference projectRef = fb.child("projects").child(projectFireBaseID);

                                // Retrieve current project, add task to project, and update FireBase
                                projectRef.child("tasks").child(taskKey).setValue(task);

                                // Add user to the task member list
                                RecyclerViewCheckboxAdapter.MemberModel user = new RecyclerViewCheckboxAdapter.MemberModel(userDisplayName, true, uid);
                                projectRef.child("tasks").child(taskKey).child("members").child(uid).setValue(user);
                                fb.child("tasks").child(taskKey).child("members").child(uid).setValue(user);
                                fb.child("usrs").child(uid).child("tasks").child(taskKey).setValue(task);
                                fb.child("usrs").child(uid).child("tasks").child(taskKey).child("members").child(uid).setValue(user);

                                toDo.setText("");
                                dates.add(calendarView.getSelectedDate());
                                tasks.add(task);
                                // Trigger calendarView's update function to update list
                                onDateSelected(calendarView, calendarView.getSelectedDate(), true);
                            }
                            return false; // consume.
                        }
                        return false; // pass on to other listeners.
                    }
                });
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        // Update list view
        if (dates.contains(date)) {
            ArrayList<TaskModel> tasksOnSelectedDay = new ArrayList<>();
            for (TaskModel task: tasks) {
                CalendarDay day = CalendarDay.from(task.getDueDate());
                if (day.equals(date)) {
                    tasksOnSelectedDay.add(task);
                }
            }
            recyclerViewAdapter.updateItems(tasksOnSelectedDay);
        }

        else {
            recyclerViewAdapter.clear();
        }
    }

    @Override
    public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
        ProjectModel project = (ProjectModel) item;
        String projectKey = project.getKey();
        projectFireBaseID = projectKey;
        calendarView.removeDecorators();

        // Query FireBase and update UI (calendar decorators and spinner project name)
        fb.child("projects").child(projectKey).child("tasks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                dates = new ArrayList<>();
                tasks = new ArrayList<>();
                for (DataSnapshot child : data.getChildren()) {
                    // Add to task list
                    TaskModel task = child.getValue(TaskModel.class);
                    tasks.add(task);
                    // Add task due date to calendar
                    dates.add(CalendarDay.from(task.getDueDate()));
                }

                // Update decorators
                int realColor = ContextCompat.getColor(getContext(), project.getColor());
                calendarView.setSelectionColor(realColor);

                todayDecorator = new TodayDecorator(realColor);
                eventDecorator = new EventDecorator(realColor, dates);

                calendarView.addDecorator(todayDecorator);
                calendarView.addDecorator(eventDecorator);

                // Set spinner project name
                String colorString = Integer.toString(realColor, 16);
                String text = "<font color=#ffffff>"+ project.getName() + "</font> <font color=#" + colorString + ">   ⬤</font>";
                spinner.setText(Html.fromHtml(text));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class TodayDecorator implements DayViewDecorator {
        private final CalendarDay today;
        private final Drawable backgroundDrawable;

        public TodayDecorator(int color) {

            today = CalendarDay.today();
            Shape circle = new Shape() {
                @Override
                public void draw(Canvas canvas, Paint paint) {
                    canvas.drawCircle(getWidth()/2,getHeight()/2,getHeight()/2,paint);
                }
            };
            ShapeDrawable shapeDrawable = new ShapeDrawable();
            shapeDrawable.setShape(circle);
            shapeDrawable.getPaint().setColor(color);

            backgroundDrawable = shapeDrawable;
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
        private final ShapeDrawable backgroundDrawable;

        public EventDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);

            Shape circle = new Shape() {
                @Override
                public void draw(Canvas canvas, Paint paint) {
                    canvas.drawCircle(getWidth()/2,getHeight()/2,getHeight()/2,paint);
                }
            };
            ShapeDrawable shapeDrawable = new ShapeDrawable();
            shapeDrawable.setShape(circle);
            shapeDrawable.getPaint().setColor(color);

            backgroundDrawable = shapeDrawable;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(backgroundDrawable);
            //view.addSpan(new DotSpan(20, color));
            //view.setBackgroundDrawable(color);
        }
    }

    // Helper function to create RecyclerView ID from task FireBase ID
    private static long longHash(String string) {
        long h = 98764321261L;
        int l = string.length();
        char[] chars = string.toCharArray();

        for (int i = 0; i < l; i++) {
            h = 31 * h + chars[i];
        }
        return h;
    }
}
