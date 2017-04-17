package ie.sheehan.smarthome;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import ie.sheehan.smarthome.adapter.ListViewAdapter;
import ie.sheehan.smarthome.model.IntrusionReading;
import ie.sheehan.smarthome.utility.HttpRequestHandler;

public class IntrusionListActivity extends AppCompatActivity {

    ProgressBar progressBar;

    ListView listView;
    ListViewAdapter listViewAdapter;
    List<IntrusionReading> intrusionReadings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intrusion_list);

        progressBar = (ProgressBar) findViewById(R.id.progress);

        intrusionReadings = new ArrayList<>();
        listView = (ListView) findViewById(R.id.list_intrusions);
        listViewAdapter = new ListViewAdapter(this, intrusionReadings);
        listView.setAdapter(listViewAdapter);

        new GetAllIntrusionReadings().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(IntrusionListActivity.this, IntrusionViewActivity.class);
                Bundle arguments = new Bundle();
                arguments.putSerializable("intrusion", intrusionReadings.get(position));

                intent.putExtras(arguments);
                IntrusionListActivity.this.startActivity(intent);
            }
        });
    }

    private class GetAllIntrusionReadings extends AsyncTask<Void, Void, List<IntrusionReading>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<IntrusionReading> doInBackground(Void... params) {
            return HttpRequestHandler.getInstance().getAllIntrusions();
        }

        @Override
        protected void onPostExecute(List<IntrusionReading> intrusionReadings) {
            super.onPostExecute(intrusionReadings);
            IntrusionListActivity.this.intrusionReadings = intrusionReadings;
            listViewAdapter.setData(intrusionReadings);
            progressBar.setVisibility(View.GONE);
        }

    }

}
