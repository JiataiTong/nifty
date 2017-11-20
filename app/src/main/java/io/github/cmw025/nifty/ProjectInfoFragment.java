package io.github.cmw025.nifty;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProjectInfoFragment extends Fragment {
public int test;

    public ProjectInfoFragment(){

    }

    public void setTest(int test) {
        this.test = test;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_project_info, container, false);

        return rootView;
    }


}
