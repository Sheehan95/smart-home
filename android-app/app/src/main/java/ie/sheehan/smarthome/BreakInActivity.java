package ie.sheehan.smarthome;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import ie.sheehan.smarthome.model.IntrusionReading;
import ie.sheehan.smarthome.utility.HttpRequestHandler;

public class BreakInActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break_in);

        imageView = (ImageView) findViewById(R.id.break_in_image);

        new GetLatestBreakIn().execute();
    }


    private class GetLatestBreakIn extends AsyncTask<Void, Void, IntrusionReading> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected IntrusionReading doInBackground(Void... params) {
            return HttpRequestHandler.getInstance().getBreakIn();
        }

        @Override
        protected void onPostExecute(IntrusionReading intrusionReading) {
            super.onPostExecute(intrusionReading);

            Log.e("STAR", "TING");

            byte[] data = Base64.decode(intrusionReading.image, Base64.DEFAULT);
            Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);

            imageView.setImageBitmap(image);
            imageView.refreshDrawableState();

            Log.e("FIN", "ISHED");
        }
    }

}
