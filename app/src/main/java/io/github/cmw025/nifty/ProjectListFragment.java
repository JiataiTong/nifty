package io.github.cmw025.nifty;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.cards.topcolored.TopColoredCard;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardViewNative;

public class ProjectListFragment extends Fragment {


    private CardListView  mListView;
    private CardArrayAdapter mCardArrayAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initCard();
    }

    /**
     * This method builds a simple list of cards
     */
    private void initCard() {

        //Init an array of Cards
        ArrayList<Card> cards = new ArrayList<Card>();
        for (int i = 0; i < 7; i++) {


            Card card = new Card(this.getActivity(), R.layout.example);
            //CardHeader header = new CardHeader(getContext());
            //header.setTitle("Damn");
            //card.addCardHeader(header);
            //card.setTitle("" + i);
            // card.setSecondaryTitle("Simple text..." + i);
            // card.setCount(i);

            //Card must have a stable Id.
            card.setId("a"+i);
            card.setOnClickListener(new Card.OnCardClickListener() {
                @Override
                public void onClick(Card card, View view) {
                    Intent intent = new Intent(getActivity(), ProjectActivity.class);
                    getActivity().startActivity(intent);
                }
            });

            cards.add(card);
        }


        //Set the adapter
        mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);

        mListView = (CardListView) getActivity().findViewById(R.id.myList);
        if (mListView != null) {
            mListView.setAdapter(mCardArrayAdapter);
        }
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
