package ie.sheehan.smarthome.fragment;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ie.sheehan.smarthome.R;
import ie.sheehan.smarthome.adapter.TaskListViewAdapter;
import ie.sheehan.smarthome.dialog.DatePickerFragment;
import ie.sheehan.smarthome.model.Task;
import ie.sheehan.smarthome.utility.HttpRequestHandler;

import static ie.sheehan.smarthome.utility.DateUtility.getDateFormat;
import static ie.sheehan.smarthome.utility.DateUtility.getShortDateFormat;
import static ie.sheehan.smarthome.utility.DateUtility.getShortTimeFormat;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {

    ListView listView;
    TaskListViewAdapter viewAdapter;
    List<Task> taskList;

    ArrayAdapter<String> spinnerAdapter;

    Spinner taskTypeSpinner;
    TextView taskDateView;
    TextView taskTimeView;

    Date taskDate;


    public ScheduleFragment() {}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        taskDate = new Date(new Date().getTime() + TimeUnit.DAYS.toMillis(1));
        taskDate.setHours(0);
        taskDate.setMinutes(0);
        taskDate.setSeconds(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        String[] taskTypes = { "Arm Alarm", "Turn On Heating" };

        taskTypeSpinner = (Spinner) getActivity().findViewById(R.id.spinner_tasks);
        spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, taskTypes);
        taskTypeSpinner.setAdapter(spinnerAdapter);

        taskDateView = (TextView) getActivity().findViewById(R.id.new_task_date);
        taskTimeView = (TextView) getActivity().findViewById(R.id.new_task_time);

        taskDateView.setText(getShortDateFormat().format(taskDate));
        taskTimeView.setText(getShortTimeFormat().format(taskDate));

        initializeButtons();

        taskList = new ArrayList<>();
        listView = (ListView) getActivity().findViewById(R.id.list_tasks);
        viewAdapter = new TaskListViewAdapter(taskList);
        listView.setAdapter(viewAdapter);

        new GetAllTasks().execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new RemoveTask().execute(taskList.get(position));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        new GetAllTasks().execute();
    }

    private void initializeButtons() {
        Button dateButton = (Button) getActivity().findViewById(R.id.button_task_date);
        Button timeButton = (Button) getActivity().findViewById(R.id.button_task_time);
        Button addButton = (Button) getActivity().findViewById(R.id.button_add_task);

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateDialog();
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimeDialog();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task task = new Task();

                String spinnerOption = (String) taskTypeSpinner.getSelectedItem();
                if (spinnerOption.equals("Arm Alarm")) {
                    task.setType("arm_alarm");
                }
                else if (spinnerOption.equals("Turn On Heating")) {
                    task.setType("turn_on_heating");
                }

                task.setDate(taskDate);

                new AddTask().execute(task);
            }
        });
    }

    public void openDateDialog() {
        DatePickerFragment fragment = new DatePickerFragment();

        Bundle arguments = new Bundle();
        arguments.putSerializable("date", taskDate);
        fragment.setArguments(arguments);

        fragment.addOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth, taskDate.getHours(), taskDate.getMinutes(), taskDate.getSeconds());
                taskDate = calendar.getTime();

                taskDateView.setText(getShortDateFormat().format(taskDate));
                taskTimeView.setText(getShortTimeFormat().format(taskDate));
            }
        });

        fragment.show(getActivity().getSupportFragmentManager(), "taskDate");
    }


    public void openTimeDialog() {
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                taskDate.setHours(hourOfDay);
                taskDate.setMinutes(minute);
                taskTimeView.setText(getShortTimeFormat().format(taskDate));
            }
        };

        int hours = taskDate.getHours();
        int minutes = taskDate.getMinutes();

        TimePickerDialog dialog = new TimePickerDialog(getContext(), listener, hours, minutes, true);
        dialog.setTitle("Time");
        dialog.show();
    }


    private class GetAllTasks extends AsyncTask<Void, Void, List<Task>> {
        @Override
        protected List<Task> doInBackground(Void... params) {
            return HttpRequestHandler.getInstance().getTaskList();
        }

        @Override
        protected void onPostExecute(List<Task> tasks) {
            super.onPostExecute(tasks);
            ScheduleFragment.this.taskList = tasks;
            ScheduleFragment.this.viewAdapter.setData(tasks);
        }
    }

    private class AddTask extends AsyncTask<Task, Void, Boolean> {
        Task task;

        @Override
        protected Boolean doInBackground(Task... params) {
            this.task = params[0];
            return HttpRequestHandler.getInstance().addTask(task);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                new GetAllTasks().execute();
            }
        }
    }

    private class RemoveTask extends AsyncTask<Task, Void, Boolean> {
        Task task;

        @Override
        protected Boolean doInBackground(Task... params) {
            this.task = params[0];
            return HttpRequestHandler.getInstance().removeTask(task);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                ScheduleFragment.this.taskList.remove(task);
                ScheduleFragment.this.viewAdapter.setData(ScheduleFragment.this.taskList);
            }
        }
    }

}
