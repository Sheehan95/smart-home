package ie.sheehan.smarthome.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import ie.sheehan.smarthome.R;
import ie.sheehan.smarthome.model.IntrusionReading;
import ie.sheehan.smarthome.utility.DateUtility;
import ie.sheehan.smarthome.utility.HttpRequestHandler;


/**
 * An implementation of the {@link BaseAdapter} for displaying {@link IntrusionReading} objects
 * in a {@link android.widget.ListView} view.
 */
public class IntrusionListViewAdapter extends BaseAdapter {

    private List<IntrusionReading> data;

    /**
     * Default constructor.
     *
     * @param data to populate the {@link android.widget.ListView} with
     */
    public IntrusionListViewAdapter(List<IntrusionReading> data) {
        this.data = data;
    }

    /**
     * Sets the {@link android.widget.ListView} data to a new data set
     *
     * @param data
     */
    public void setData(List<IntrusionReading> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View itemView;

        // inflating the view
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.list_row_intrusion, null);
        }
        else {
            itemView = convertView;
        }

        final IntrusionReading entry = data.get(position);

        // creating references to GUI components
        ImageView image = (ImageView) itemView.findViewById(R.id.image_intrusion_preview);
        TextView date = (TextView) itemView.findViewById(R.id.label_intrusion_date);
        TextView time = (TextView) itemView.findViewById(R.id.label_intrusion_time);
        TextView seen = (TextView) itemView.findViewById(R.id.label_intrusion_seen);
        ImageButton viewButton = (ImageButton) itemView.findViewById(R.id.img_button_view_intrusion);
        ImageButton removeButton = (ImageButton) itemView.findViewById(R.id.img_button_remove_intrusion);

        // populating the GUI components
        byte[] imageData = Base64.decode(entry.image, Base64.DEFAULT);
        image.setImageBitmap(BitmapFactory.decodeByteArray(imageData, 0, imageData.length));
        image.refreshDrawableState();

        date.setText(DateUtility.getDateFormat().format(new Date(entry.timestamp * 1000L)));
        time.setText(DateUtility.getTimeFormat().format(new Date(entry.timestamp * 1000L)));

        if (entry.viewed) {
            seen.setText(R.string.text_label_intrusion_viewed_seen);
        }
        else {
            seen.setText(R.string.text_label_intrusion_viewed_unseen);
        }

        // applying listeners to buttons
        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MarkIntrusionAsViewed().execute(entry);
                data.get(position).viewed = true;
                IntrusionListViewAdapter.this.notifyDataSetChanged();
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RemoveIntrusion().execute(entry);
                data.remove(position);
                IntrusionListViewAdapter.this.notifyDataSetChanged();
            }
        });

        viewButton.setFocusable(false);
        removeButton.setFocusable(false);

        return itemView;
    }


    private class MarkIntrusionAsViewed extends AsyncTask<IntrusionReading, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(IntrusionReading... params) {
            IntrusionReading intrusionReading = params[0];
            return HttpRequestHandler.getInstance().markIntrusionAsViewed(intrusionReading);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                Log.e("MARK", "SUCCESS");
            }
            else {
                Log.e("MARK", "FAILURE");
            }
        }
    }

    private class RemoveIntrusion extends AsyncTask<IntrusionReading, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(IntrusionReading... params) {
            IntrusionReading intrusionReading = params[0];
            return HttpRequestHandler.getInstance().removeIntrusion(intrusionReading);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                Log.e("REMOVE", "SUCCESS");
            }
            else {
                Log.e("REMOVE", "FAILURE");
            }
        }
    }

}
