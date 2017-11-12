package io.github.cmw025.nifty;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import io.github.cmw025.nifty.RecyclerViewCheckboxAdapter.DataModel;

import java.util.ArrayList;

public class AddMemberActivity extends AppCompatActivity {

    ArrayList<DataModel> dataModels;
    ListView listView;
    private RecyclerViewCheckboxAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);


        listView = (ListView) findViewById(R.id.listView);

        dataModels = new ArrayList();

        dataModels.add(new DataModel("Jimmy Wei", false));
        dataModels.add(new DataModel("Sonia", false));
        dataModels.add(new DataModel("GWW", false));
        dataModels.add(new DataModel("Troy Tong", true));

        adapter = new RecyclerViewCheckboxAdapter(dataModels, getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                DataModel dataModel= dataModels.get(position);
                dataModel.checked = !dataModel.checked;
                adapter.notifyDataSetChanged();


            }
        });
    }
    public void goBack(View view) {
        finish();
        overridePendingTransition(R.animator.slide_in_left_to_right, R.animator.slide_out_left_to_right);
    }
}
