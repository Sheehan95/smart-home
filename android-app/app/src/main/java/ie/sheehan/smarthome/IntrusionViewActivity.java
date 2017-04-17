package ie.sheehan.smarthome;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import ie.sheehan.smarthome.model.IntrusionReading;
import ie.sheehan.smarthome.utility.HttpRequestHandler;

import static android.R.attr.data;

public class IntrusionViewActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intrusion_view);

        imageView = (ImageView) findViewById(R.id.break_in_image);

        Bundle arguments = getIntent().getExtras();

        IntrusionReading intrusionReading = (IntrusionReading) arguments.getSerializable("intrusion");

        if (intrusionReading == null) {
            Toast.makeText(this, "No intrusion to view", Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] data = Base64.decode(intrusionReading.image, Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);

        imageView.setImageBitmap(image);
        imageView.refreshDrawableState();
    }

}
