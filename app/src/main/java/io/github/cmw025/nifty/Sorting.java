package io.github.cmw025.nifty;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by troytong on 2017/11/27.
 */

public class Sorting {

    @Nullable
    public static ArrayList<TaskModel> task_sorting(ArrayList<TaskModel> tms, boolean sortingByDue) {
        if (sortingByDue == true) {
            Collections.sort(tms);
            return tms;
        }
        else {
            ArrayList<TaskModel> ntms = new ArrayList<TaskModel>();
            //int[] index = new int[tms.size()];
            int i = 0, j = 1, k = 0;

            for (TaskModel task : tms) {
                if (task.isFinished()) {
                    ntms.add(task);
                }
            }
            if (ntms.size() == 0) {
                return null;
            }
            if (ntms.size() == 1) {
                return ntms;
            }
//            while (k < ntms.size()) {
//                while (j < ntms.size()) {
//                    assert (ntms.get(j).getFinishDate() != null);
//                    if (ntms.get(i).getFinishDate().after(ntms.get(j).getFinishDate())) {
//                        TaskModel item = ntms.get(i);
//                        ntms.remove(i);
//                        ntms.add(i, ntms.get(j));
//                        ntms.remove(j);
//                        ntms.add(j, item);
//                    }
//                    i++;
//                    j++;
//                }
//                k++;
//            }
            return ntms;
        }


    }

}
