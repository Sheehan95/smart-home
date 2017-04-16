package ie.sheehan.smarthome.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import ie.sheehan.smarthome.CameraFeedActivity;
import ie.sheehan.smarthome.IntrusionViewActivity;
import ie.sheehan.smarthome.R;
import ie.sheehan.smarthome.model.AlarmStatus;
import ie.sheehan.smarthome.utility.HttpRequestHandler;

/**
 * A simple {@link Fragment} subclass.
 */
public class SecurityFragment extends Fragment {

    Switch alarmSwitch;
    TextView labelAlarmScheduled;
    TextView labelAlarmLastArmed;


    public SecurityFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_security, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        alarmSwitch = (Switch) getActivity().findViewById(R.id.switch_alarm);
        labelAlarmScheduled = (TextView) getActivity().findViewById(R.id.label_alarm_scheduled);
        labelAlarmLastArmed = (TextView) getActivity().findViewById(R.id.label_alarm_last_armed);

        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                new ArmAlarm().execute(isChecked);
            }
        });

        new GetAlarmStatus().execute();
    }

    public void openCamera(){
        new OpenCameraFeed().execute();
    }

    public void openIntrusionView() {
        getActivity().startActivity(new Intent(getActivity(), IntrusionViewActivity.class));
    }


    private class ArmAlarm extends AsyncTask<Boolean, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            boolean arm = params[0];
            return HttpRequestHandler.getInstance().armAlarm(arm);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                new GetAlarmStatus().execute();
            }
            else {
                Toast.makeText(getActivity(), "Unable to arm the alarm", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class GetAlarmStatus extends AsyncTask<Void, Void, AlarmStatus> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected AlarmStatus doInBackground(Void... params) {
            return HttpRequestHandler.getInstance().getAlarmStatus();
        }

        @Override
        protected void onPostExecute(AlarmStatus alarmStatus) {
            super.onPostExecute(alarmStatus);

            if (alarmStatus == null) {
                Toast.makeText(getActivity(), "Unable to read alarm status", Toast.LENGTH_SHORT).show();
                return;
            }

            alarmSwitch.setChecked(alarmStatus.armed);
            labelAlarmLastArmed.setText(alarmStatus.lastArmed.toString());
        }
    }

    private class OpenCameraFeed extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return HttpRequestHandler.getInstance().toggleCameraFeed(true);
        }

        @Override
        protected void onPostExecute(Boolean stream) {
            super.onPostExecute(stream);

            if (stream) {
                startActivity(new Intent(getActivity(), CameraFeedActivity.class));
            }
            else {
                Toast.makeText(getActivity(), "Unable to open camera feed", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
