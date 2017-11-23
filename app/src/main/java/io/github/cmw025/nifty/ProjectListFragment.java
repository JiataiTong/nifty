package io.github.cmw025.nifty;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.OnItemMovedListener;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import it.gmariotti.cardslib.library.cards.topcolored.TopColoredCard;
import it.gmariotti.cardslib.library.extra.dragdroplist.internal.CardDragDropArrayAdapter;
import it.gmariotti.cardslib.library.extra.dragdroplist.view.CardListDragDropView;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardViewNative;

import io.github.cmw025.nifty.RecyclerViewCheckboxAdapter.MemberModel;

public class ProjectListFragment extends Fragment {


    private CardListView  mListView;
    private CardArrayAdapter mCardArrayAdapter;
    private DatabaseReference fb;
    private String uid;
    private int color;
    private String userDisplayName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View)inflater.inflate(R.layout.fragment_project_list, container, false);
        return inflater.inflate(R.layout.fragment_project_list, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = (CardListView) getActivity().findViewById(R.id.myList);

        initFirebase();
        initAddButton();
    }

//    /**
//     * This method builds a simple list of cards
//     */
//    private void initCard() {
//        //Init an array of Cards
//        ArrayList<Card> cards = new ArrayList<>();
//        for (int i = 0; i < 7; i++) {
//
//
//            Card card = new Card(this.getActivity(), R.layout.example);
//            //CardHeader header = new CardHeader(getContext());
//            //header.setTitle("Damn");
//            //card.addCardHeader(header);
//            //card.setTitle("" + i);
//            // card.setSecondaryTitle("Simple text..." + i);
//            // card.setCount(i);
//
//            //Card must have a stable Id.
//            card.setId("a"+i);
//            card.setOnClickListener(new Card.OnCardClickListener() {
//                @Override
//                public void onClick(Card card, View view) {
//                    Intent intent = new Intent(getActivity(), ProjectActivity.class);
//                    getActivity().startActivity(intent);
//                }
//            });
//
//            cards.add(card);
//        }
//
//
//        //Set the adapter
//        mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);
//
//        mListView = (CardListView) getActivity().findViewById(R.id.myList);
//        if (mListView != null) {
//            mListView.setAdapter(mCardArrayAdapter);
//        }
//    }

    public void initFirebase() {
        // Firebase
        fb = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        uid = user.getUid();
        userDisplayName = user.getDisplayName();

        DatabaseReference projects = fb.child("usrs").child(uid).child("projects");


        projects.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                ArrayList<Card> cards = new ArrayList<>();
                for (DataSnapshot child : data.getChildren()) {
                    ProjectModel project = child.getValue(ProjectModel.class);

                    Card card = new Card(getActivity(), R.layout.example);
                    card.setId(project.getKey());
                    card.setTitle(project.getName());
                    card.setBackgroundResourceId(project.getColor());
                    card.setSwipeable(true);
                    card.setOnSwipeListener(new Card.OnSwipeListener() {
                        @Override
                        public void onSwipe(Card card) {

                            String projectKey = child.getKey();
                            // Remove user from the project
                            fb.child("projects").child(projectKey).child("members").child(uid).removeValue();

                            // Remove project reference under user
                            fb.child("usrs").child(uid).child("projects").child(projectKey).removeValue();

                            // Remove associated tasks under user
                            fb.child("projects").child(projectKey).child("tasks").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot data) {
                                    for (DataSnapshot child : data.getChildren()) {
                                        String taskKey = child.getKey();
                                        fb.child("usrs").child(uid).child("tasks").child(taskKey).removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            Snackbar undo = Snackbar.make(getActivity().findViewById(R.id.myList), "Removed " + project.getName(), Snackbar.LENGTH_LONG);
                            undo.setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Add user back to the project
                                    String userName = user.getDisplayName();
                                    MemberModel user = new MemberModel(userName, true, uid);
                                    // Add user back to project
                                    fb.child("projects").child(projectKey).child("members").child(uid).setValue(user);

                                    // Update project info under user
                                    fb.child("usrs").child(uid).child("projects").child(projectKey).setValue(project);

                                    // Add back member list
                                    fb.child("projects").child(projectKey).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot data) {
                                            for (DataSnapshot child : data.getChildren()) {
                                                MemberModel member = child.getValue(MemberModel.class);
                                                fb.child("usrs").child(uid).child("projects").child(projectKey).child("members").child(member.getUid()).setValue(member);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    // Add back task list
                                    fb.child("projects").child(projectKey).child("tasks").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot data) {
                                            for (DataSnapshot child : data.getChildren()) {
                                                TaskModel task = child.getValue(TaskModel.class);
                                                fb.child("usrs").child(uid).child("projects").child(projectKey).child("tasks").child(task.getKey()).setValue(task);
                                                fb.child("usrs").child(uid).child("tasks").child(task.getKey()).setValue(task);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                            });
                            undo.show();

                            projects.child("members").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot data) {
                                    ArrayList<MemberModel> members = new ArrayList<>();
                                    for (DataSnapshot child : data.getChildren()) {
                                        MemberModel member = child.getValue(MemberModel.class);
                                        members.add(member);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
//
//                    //Create thumbnail
//                    CardThumbnail thumb = new CardThumbnail(getActivity());
//                    //Set resource
//                    thumb.setDrawableResource(R.drawable.today_rect_background);
//                    //Add thumbnail to a card
//                    card.addCardThumbnail(thumb);

//                    CardHeader header = new CardHeader(getContext());
//                    header.setTitle(project.getName());
//                    header.setOtherButtonVisible(true);
//                    header.setOtherButtonClickListener(new CardHeader.OnClickCardHeaderOtherButtonListener() {
//                        @Override
//                        public void onButtonItemClick(Card card, View view) {
//                            String projectKey = child.getKey();
//                            // Remove user from the project
//                            fb.child("projects").child(projectKey).child("members").child(uid).removeValue();
//
//                            // Remove project reference under user
//                            fb.child("usrs").child(uid).child("projects").child(projectKey).removeValue();
//
//                            // Remove associated tasks under user
//                            fb.child("projects").child(projectKey).child("tasks").addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot data) {
//                                    for (DataSnapshot child : data.getChildren()) {
//                                        String taskKey = child.getKey();
//                                        fb.child("usrs").child(uid).child("tasks").child(taskKey).removeValue();
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//
//                            Snackbar undo = Snackbar.make(getActivity().findViewById(R.id.myList), "Removed " + project.getName(), Snackbar.LENGTH_LONG);
//                            undo.setAction("UNDO", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    // Add user back to the project
//                                    String userName = user.getDisplayName();
//                                    MemberModel user = new MemberModel(userName, true, uid);
//                                    fb.child("projects").child(projectKey).child("members").child(uid).setValue(user);
//
//                                    // Update project info under user
//                                    fb.child("usrs").child(uid).child("projects").child(projectKey).setValue(project);
//
//                                    // Add back member list
//                                    fb.child("projects").child(projectKey).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot data) {
//                                            for (DataSnapshot child : data.getChildren()) {
//                                                MemberModel member = child.getValue(MemberModel.class);
//                                                fb.child("usrs").child(uid).child("projects").child(projectKey).child("members").child(member.getUid()).setValue(member);
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//
//                                    // Add back task list
//                                    fb.child("projects").child(projectKey).child("tasks").addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot data) {
//                                            for (DataSnapshot child : data.getChildren()) {
//                                                TaskModel task = child.getValue(TaskModel.class);
//                                                fb.child("usrs").child(uid).child("projects").child(projectKey).child("tasks").child(task.getKey()).setValue(task);
//                                                fb.child("usrs").child(uid).child("tasks").child(task.getKey()).setValue(task);
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//
//                                }
//                            });
//                            undo.show();
//
//                            projects.child("members").addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot data) {
//                                    ArrayList<MemberModel> members = new ArrayList<>();
//                                    for (DataSnapshot child : data.getChildren()) {
//                                        MemberModel member = child.getValue(MemberModel.class);
//                                        members.add(member);
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
//                        }
//                    });
//                    card.addCardHeader(header);
                    card.setOnClickListener(new Card.OnCardClickListener() {
                        @Override
                        public void onClick(Card card, View view) {
                            String key = child.getKey();
                            Intent intent = new Intent(getActivity(), ProjectActivity.class);
                            intent.putExtra("projectFireBaseID", key);
                            intent.putExtra("projectName", project.getName());
                            intent.putExtra("projectColor", project.getColor());
                            getActivity().startActivity(intent);
                        }
                    });
                    cards.add(card);
                }
                Collections.reverse(cards);



                if (mListView != null && getActivity() != null) {
                    //Set the adapter
                    mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);
                    mListView.setAdapter(mCardArrayAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    public void initAddButton() {
//        ImageView button = getActivity().findViewById(R.id.add_project);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Date date = new Date();
//                ProjectModel project = new ProjectModel("COOL PROJECT", "EMPTY CONTENT",
//                        date, date, 0);
//
//                DatabaseReference ref = fb.child("projects").push();
//                ref.setValue(project);
//
//
//                // For now we pretend we are in every new project we create
//                MemberModel member = new MemberModel("Jimmy", true, uid);
//                MemberModel member2 = new MemberModel("sonia", false, uid + "1");
//                MemberModel member3 = new MemberModel("Troy", false, uid + "2");
//                MemberModel member4 = new MemberModel("Weiwei", false, uid + "3");
//                ref.child("members").push().setValue(member);
//                ref.child("members").push().setValue(member2);
//                ref.child("members").push().setValue(member3);
//                ref.child("members").push().setValue(member4);
//                String key = ref.getKey();
//                fb.child("usrs").child(uid).child("projects").child(key).setValue(project);
//                fb.child("usrs").child(uid).child("projects").child(key).child("members").push().setValue(member);
//                fb.child("usrs").child(uid).child("projects").child(key).child("members").push().setValue(member2);
//                fb.child("usrs").child(uid).child("projects").child(key).child("members").push().setValue(member3);
//                fb.child("usrs").child(uid).child("projects").child(key).child("members").push().setValue(member4);
//            }
//        });
//
//    }

    public void initAddButton() {
        ImageView button = getActivity().findViewById(R.id.add_project);
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(View view) {

                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_around_center);
                button.startAnimation(animation);

                LayoutInflater li=LayoutInflater.from(getActivity());
                View promptsView=li.inflate(R.layout.addprojectlayout,null);
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setIcon(R.drawable.ic_note_add_black_24dp);
                builder.setTitle("New Project");
                builder.setView(promptsView);


                // Default to red
                color = R.color.light_red;
                final RadioGroup radioGroup = (RadioGroup)promptsView.findViewById(R.id.color_radio_group);
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId)
                    {
                        switch (checkedId) {
                            case R.id.red:
                                //color = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.light_red));
                                color = R.color.light_red;
                                break;
                            case R.id.orange:
//                                color = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.light_orange));
                                color = R.color.light_orange;
                                break;
                            case R.id.yellow:
//                                color = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.light_yellow));
                                color = R.color.light_yellow;
                                break;
                            case R.id.green:
//                                color = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.light_green));
                                color = R.color.light_green;
                                break;
                            case R.id.cyan:
//                                color = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.light_cyan));
                                color = R.color.light_cyan;
                                break;
                            case R.id.aqua:
//                                color = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.light_aqua));
                                color = R.color.light_aqua;
                                break;
                            case R.id.blue:
//                                color = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.light_blue));
                                color = R.color.light_blue;
                                break;
                            case R.id.purple:
//                                color = "#" + Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.light_purple));
                                color = R.color.light_purple;
                                break;
                        }
                    }
                });


                final EditText inputName= (EditText)promptsView.findViewById(R.id.Name);
                inputName.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Date date = new Date();
                        String name = inputName.getText().toString();

                        DatabaseReference ref = fb.child("projects").push();
                        String key = ref.getKey();
                        ProjectModel project = new ProjectModel(name, "",
                                date, date, 0, key, color);
                        ref.setValue(project);

                        // For now we pretend we are in every new project we create
                        MemberModel member = new MemberModel(userDisplayName, true, uid);
                        MemberModel member2 = new MemberModel("sonia", false, "SONIA'S UNIQUE ID");
                        MemberModel member3 = new MemberModel("Troy", false, "TROY'S UNIQUE ID");
                        MemberModel member4 = new MemberModel("Weiwei", false, "WEIWEI VERY SPECIAL");

                        ref.child("members").child(member.getUid()).setValue(member);
                        ref.child("members").child(member2.getUid()).setValue(member2);
                        ref.child("members").child(member3.getUid()).setValue(member3);
                        ref.child("members").child(member4.getUid()).setValue(member4);

                        // Save user project history
                        fb.child("usrs").child(uid).child("projects").child(key).setValue(project);

                        // Save project
                        fb.child("usrs").child(uid).child("projects").child(key).setValue(project);
                        fb.child("usrs").child(uid).child("projects").child(key).child("members").child(member.getUid()).setValue(member);
                        fb.child("usrs").child(uid).child("projects").child(key).child("members").child(member2.getUid()).setValue(member2);
                        fb.child("usrs").child(uid).child("projects").child(key).child("members").child(member3.getUid()).setValue(member3);
                        fb.child("usrs").child(uid).child("projects").child(key).child("members").child(member4.getUid()).setValue(member4);
                    }
                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
                builder.show();
            }
        });

    }


//    //-------------------------------------------------------------------------------------------------------------
//    // Animations. (these method aren't used in this demo, but they can be called to enable the animations)
//    //-------------------------------------------------------------------------------------------------------------
//
//    /**
//     * Alpha animation
//     */
//    private void setAlphaAdapter() {
//        AnimationAdapter animCardArrayAdapter = new AlphaInAnimationAdapter(mCardArrayAdapter);
//        animCardArrayAdapter.setAbsListView(mListView);
//        if (mListView != null) {
//            mListView.setExternalAdapter(animCardArrayAdapter,mCardArrayAdapter);
//        }
//    }
//
//    /**
//     * Left animation
//     */
//    private void setLeftAdapter() {
//        AnimationAdapter animCardArrayAdapter = new SwingLeftInAnimationAdapter(mCardArrayAdapter);
//        animCardArrayAdapter.setAbsListView(mListView);
//        if (mListView != null) {
//            mListView.setExternalAdapter(animCardArrayAdapter,mCardArrayAdapter);
//        }
//    }
//
//    /**
//     * Right animation
//     */
//    private void setRightAdapter() {
//        AnimationAdapter animCardArrayAdapter = new SwingRightInAnimationAdapter(mCardArrayAdapter);
//        animCardArrayAdapter.setAbsListView(mListView);
//        if (mListView != null) {
//            mListView.setExternalAdapter(animCardArrayAdapter,mCardArrayAdapter);
//        }
//    }
//
//    /**
//     * Bottom animation
//     */
//    private void setBottomAdapter() {
//        AnimationAdapter animCardArrayAdapter = new SwingBottomInAnimationAdapter(mCardArrayAdapter);
//        animCardArrayAdapter.setAbsListView(mListView);
//        mListView.setExternalAdapter(animCardArrayAdapter,mCardArrayAdapter);
//    }
//
//    /**
//     * Bottom-right animation
//     */
//    private void setBottomRightAdapter() {
//        AnimationAdapter animCardArrayAdapter = new SwingBottomInAnimationAdapter(new SwingRightInAnimationAdapter(mCardArrayAdapter));
//        animCardArrayAdapter.setAbsListView(mListView);
//        if (mListView != null) {
//            mListView.setExternalAdapter(animCardArrayAdapter,mCardArrayAdapter);
//        }
//    }
//
//    /**
//     * Scale animation
//     */
//    private void setScaleAdapter() {
//        AnimationAdapter animCardArrayAdapter = new ScaleInAnimationAdapter(mCardArrayAdapter);
//        animCardArrayAdapter.setAbsListView(mListView);
//        if (mListView != null) {
//            mListView.setExternalAdapter(animCardArrayAdapter,mCardArrayAdapter);
//        }
//    }
//
//    }

}
