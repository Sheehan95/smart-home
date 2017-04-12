package ie.sheehan.smarthome.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ie.sheehan.smarthome.CameraFeedActivity;
import ie.sheehan.smarthome.R;
import ie.sheehan.smarthome.utility.HttpRequestHandler;

/**
 * A simple {@link Fragment} subclass.
 */
public class SecurityFragment extends Fragment {


    public SecurityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_security, container, false);
    }

    public void openCamera(){
        new OpenCameraFeed().execute();
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
