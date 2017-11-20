package io.github.cmw025.nifty;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;


public class RecyclerViewCheckboxAdapter extends ArrayAdapter {
    private ArrayList<DataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        CheckBox checkBox;
    }

    public RecyclerViewCheckboxAdapter(ArrayList data, Context context) {
        super(context, R.layout.list_item_checkbox, data);
        this.dataSet = data;
        this.mContext = context;

    }

    public void updateItems(ArrayList<DataModel> newData) {
        dataSet = newData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public DataModel getItem(int position) {
        return dataSet.get(position);
    }


    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_checkbox, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

            result=convertView;
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        DataModel item = getItem(position);


        viewHolder.txtName.setText(item.name);
        viewHolder.checkBox.setChecked(item.checked);


        return result;
    }



    static class DataModel {

        public String name;
        boolean checked;

        DataModel(String name, boolean checked) {
            this.name = name;
            this.checked = checked;

        }
    }
}
