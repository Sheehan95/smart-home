package ie.sheehan.smarthome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import ie.sheehan.smarthome.R;
import ie.sheehan.smarthome.model.Task;

import static ie.sheehan.smarthome.utility.DateUtility.*;

public class TaskListViewAdapter extends BaseAdapter {

    private List<Task> data;

    public TaskListViewAdapter(List<Task> data) { this.data = data; }


    public void setData(List<Task> data) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.list_row_task, null);
        }
        else {
            itemView = convertView;
        }

        Task task = data.get(position);

        TextView taskNumber = (TextView) itemView.findViewById(R.id.task_number);
        TextView taskType = (TextView) itemView.findViewById(R.id.task_type);
        TextView taskDate = (TextView) itemView.findViewById(R.id.task_date);

        taskNumber.setText(String.format(Locale.getDefault(), "#%d", position));
        taskType.setText(task.getDisplayName());

        String date = getDateFormat().format(task.getDate());
        String time = getTimeFormat().format(task.getDate());

        taskDate.setText(String.format(Locale.getDefault(), "%s @ %s", date, time));

        return itemView;
    }
}
