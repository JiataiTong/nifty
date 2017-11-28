package io.github.cmw025.nifty;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.content.Intent;

public class ProjectInfoFragment extends Fragment {
    public int test;
    private ImageButton startChatting;
    public ProjectInfoFragment(){

    }

    public void setTest(int test) {
        this.test = test;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_project_info,container,false);
        Intent intent=new Intent(getActivity(),ChattingActivity.class);
        startChatting = (ImageButton) getActivity().findViewById(R.id.startChatting);
        startChatting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View a){
                startActivity(intent);
            }

        });



        return v;
    }


}
