package ie.sheehan.smarthome.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import ie.sheehan.smarthome.R;
import ie.sheehan.smarthome.model.IntrusionReading;
import ie.sheehan.smarthome.utility.DateUtility;


public class ListViewAdapter extends BaseAdapter {

    private List<IntrusionReading> data;

    public ListViewAdapter(List<IntrusionReading> data) {
        this.data = data;
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

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.list_row_intrusion, null);
        }
        else {
            itemView = convertView;
        }

        final IntrusionReading entry = data.get(position);

        ImageView image = (ImageView) itemView.findViewById(R.id.image_intrusion_preview);
        TextView date = (TextView) itemView.findViewById(R.id.label_intrusion_date);
        TextView time = (TextView) itemView.findViewById(R.id.label_intrusion_time);
        TextView seen = (TextView) itemView.findViewById(R.id.label_intrusion_seen);
        ImageButton viewButton = (ImageButton) itemView.findViewById(R.id.img_button_view_intrusion);
        ImageButton removeButton = (ImageButton) itemView.findViewById(R.id.img_button_remove_intrusion);

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

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.get(position).viewed = true;
                ListViewAdapter.this.notifyDataSetChanged();
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.remove(position);
                ListViewAdapter.this.notifyDataSetChanged();
            }
        });

        viewButton.setFocusable(false);
        removeButton.setFocusable(false);

        return itemView;
    }

    public void setData(List<IntrusionReading> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}
