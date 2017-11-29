package io.github.cmw025.nifty;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;


public class RecyclerViewCheckboxAdapter extends ArrayAdapter {
    private ArrayList<MemberModel> dataSet;
    private Context mContext;

    // View lookup cache
    private class ViewHolder {
        TextView txtName;
        CheckBox checkBox;
    }

    public RecyclerViewCheckboxAdapter(ArrayList data, Context context) {
        super(context, R.layout.list_item_checkbox, data);
        this.dataSet = data;
        this.mContext = context;
    }

    public void updateItems(ArrayList<MemberModel> newData) {
        dataSet = newData;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public MemberModel getItem(int position) {
        return dataSet.get(position);
    }


    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_checkbox, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

            result = convertView;
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        MemberModel item = getItem(position);


        viewHolder.txtName.setText(item.name);
        viewHolder.checkBox.setChecked(item.checked);


        return result;
    }



    static class MemberModel implements Serializable {

        public String name;
        boolean checked;
        public String uid;

        MemberModel(String name, boolean checked, String uid) {
            this.name = name;
            this.checked = checked;
            this.uid = uid;
        }

        MemberModel() {}

        public String getName() {
            return name;
        }

        public boolean isChecked() {
            return checked;
        }

        public String getUid() {
            return uid;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MemberModel))
                return false;
            if (obj == this)
                return true;

            MemberModel that = (MemberModel) obj;
            return (this.uid.equals(that.uid));
        }

        @Override
        public int hashCode() {
            if (this.name == null) {
                return 0;
            }
            return this.name.length();
        }
    }
}
