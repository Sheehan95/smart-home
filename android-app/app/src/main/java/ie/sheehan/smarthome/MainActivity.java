package ie.sheehan.smarthome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ie.sheehan.smarthome.activities.TemperatureActivity;
import ie.sheehan.smarthome.activities.TemperatureChartActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void launchTemperatureActivity(View view){
        Intent intent = new Intent(this, TemperatureActivity.class);
        startActivity(intent);
    }

    public void launchTemperatureChartActivity(View view){
        Intent intent = new Intent(this, TemperatureChartActivity.class);
        startActivity(intent);
    }

}
