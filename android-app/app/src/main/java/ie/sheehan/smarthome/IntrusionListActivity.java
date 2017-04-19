package ie.sheehan.smarthome;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import ie.sheehan.smarthome.adapter.IntrusionListViewAdapter;
import ie.sheehan.smarthome.model.IntrusionReading;
import ie.sheehan.smarthome.utility.HttpRequestHandler;

public class IntrusionListActivity extends AppCompatActivity {

    ProgressBar progressBar;

    ListView listView;
    IntrusionListViewAdapter intrusionListViewAdapter;
    List<IntrusionReading> intrusionReadings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intrusion_list);

        progressBar = (ProgressBar) findViewById(R.id.progress);

        intrusionReadings = new ArrayList<>();
        listView = (ListView) findViewById(R.id.list_intrusions);
        intrusionListViewAdapter = new IntrusionListViewAdapter(intrusionReadings);
        listView.setAdapter(intrusionListViewAdapter);

        new GetAllIntrusionReadings().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(IntrusionListActivity.this, IntrusionViewActivity.class);
                Bundle arguments = new Bundle();

                IntrusionReading intrusionReading = intrusionReadings.get(position);
                arguments.putSerializable("intrusion", intrusionReading);
                arguments.putInt("source", IntrusionViewActivity.INTENT_SOURCE_ACTIVITY);
                intent.putExtras(arguments);

                new MarkIntrusionAsViewed().execute(intrusionReading);

                IntrusionListActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        intrusionListViewAdapter.notifyDataSetChanged();
    }

    public void markAllAsViewed(View view) {
        new MarkAllIntrusionsAsViewed().execute();
    }

    public void removeAll(View view) {
        new RemoveAllIntrusions().execute();
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
            intrusionListViewAdapter.setData(intrusionReadings);
            progressBar.setVisibility(View.GONE);

            if (intrusionReadings.isEmpty()) {
                IntrusionListActivity.this.findViewById(R.id.text_label_no_intrusions).setVisibility(View.VISIBLE);
            }
        }
    }

    private class MarkIntrusionAsViewed extends AsyncTask<IntrusionReading, Void, Void> {

        IntrusionReading intrusionReading;

        @Override
        protected Void doInBackground(IntrusionReading... params) {
            intrusionReading = params[0];
            HttpRequestHandler.getInstance().markIntrusionAsViewed(intrusionReading);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            intrusionReading.viewed = true;
            intrusionListViewAdapter.notifyDataSetChanged();
        }
    }

    private class MarkAllIntrusionsAsViewed extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            HttpRequestHandler.getInstance().markAllIntrusionsAsViewed();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            for (IntrusionReading intrusionReading : IntrusionListActivity.this.intrusionReadings) {
                intrusionReading.viewed = true;
            }

            IntrusionListActivity.this.intrusionListViewAdapter.notifyDataSetChanged();
        }
    }

    private class RemoveAllIntrusions extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            HttpRequestHandler.getInstance().removeAllIntrusions();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            IntrusionListActivity.this.intrusionReadings.clear();
            IntrusionListActivity.this.intrusionListViewAdapter.notifyDataSetChanged();
        }
    }

}
