package io.github.cmw025.nifty;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.gmariotti.cardslib.library.cards.topcolored.TopColoredCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardViewNative;

public class ProjectListFragment extends Fragment {


    private CardListView  mListView;
    private CardArrayAdapter mCardArrayAdapter;
    private DatabaseReference fb;
    private String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initCard();
        initFirebase();
        initAddButton();
    }

    /**
     * This method builds a simple list of cards
     */
    private void initCard() {

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
    }

    public void initFirebase() {
        // Firebase
        fb = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        uid = user.getUid();

        DatabaseReference projects = fb.child("projects").child(uid);
        projects.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                ArrayList<Card> cards = new ArrayList<>();
                for (DataSnapshot child : data.getChildren()) {
                    ProjectModel project = child.getValue(ProjectModel.class);
                    Log.v("fb", child.getKey() + ": " + project.getName());

                    Card card = new Card(getActivity(), R.layout.example);
                    card.setId("a");
                    card.setOnClickListener(new Card.OnCardClickListener() {
                        @Override
                        public void onClick(Card card, View view) {
                            String key = child.getKey();

                            Intent intent = new Intent(getActivity(), ProjectActivity.class);
                            intent.putExtra("projectFireBaseID", key);
                            getActivity().startActivity(intent);
                        }
                    });
                    cards.add(card);
                }
                CardListView listview = (CardListView) getActivity().findViewById(R.id.myList);
                listview.setAdapter(new CardArrayAdapter(getActivity(), cards));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void initAddButton() {
        ImageView button = getActivity().findViewById(R.id.add_project);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = new Date();
                ProjectModel project = new ProjectModel("COOL", "SUCH CONTENT",
                        date, date, 0);

                DatabaseReference ref = fb.child("projects").child(uid).push();
                ref.setValue(project);
                String key = ref.getKey();
                fb.child("usrs").child(uid).child("projects").child(key).setValue(true);
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
