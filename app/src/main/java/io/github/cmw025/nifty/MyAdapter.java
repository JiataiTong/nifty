package io.github.cmw025.nifty;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by app on 17/11/9.
 */

public class MyAdapter extends FragmentStatePagerAdapter {

    public MyAdapter(FragmentManager fm) {
        super(fm);
    }
    

    @Override
    public ProjectContributionFragment getItem(int i){
        return new ProjectContributionFragment();
    }

    public int getCount(){
        return 3;
    }
}
