package cosw.eci.edu.lab9;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddLocationActivity extends AppCompatActivity {

    
    private EditText nameView;
    private EditText descriptionView;
    private EditText longituteView;
    private EditText latituteView;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        // Get the Intent that started this activity and extract the string
        //Intent intent = getIntent();
        //String message = intent.getStringExtra(MapsActivity.ADD_LOCATION_INTENT);
        
        nameView = (EditText) findViewById(R.id.name);
        descriptionView = (EditText) findViewById(R.id.description);
        longituteView = (EditText) findViewById(R.id.longitute);
        latituteView = (EditText) findViewById(R.id.latitude);
        
        

    }
    
    public void onClickAddLocation(View v){
        System.out.println(nameView.getText().toString());
        System.out.println(descriptionView.getText().toString());
        System.out.println(longituteView.getText().toString());
        System.out.println(latituteView.getText().toString());
        boolean canContinue = true;
        if(nameView.getText().toString().equals("")) { nameView.setError(getResources().getString(R.string.error_name_empty));canContinue=false;}
        if (descriptionView.getText().toString().equals("")){ descriptionView.setError(getResources().getString(R.string.error_description_empty));canContinue=false;}
        if (longituteView.getText().toString().equals("")){ longituteView.setError(getResources().getString(R.string.error_longitute_empty));canContinue=false;}
        if (latituteView.getText().toString().equals("")){ latituteView.setError(getResources().getString(R.string.error_latitute_empty));canContinue=false;}
        if(canContinue){
            Intent intent = new Intent();
            Location locationToSend = new Location("");
            locationToSend.setLatitude(Double.parseDouble(latituteView.getText().toString()));
            locationToSend.setLongitude(Double.parseDouble(longituteView.getText().toString()));
            intent.putExtra("location", locationToSend);
            intent.putExtra("name",nameView.getText().toString());
            intent.putExtra("description",descriptionView.getText().toString());
            setResult(MapsActivity.RESULT_OK,intent);
            finish();
        }

    }
}
