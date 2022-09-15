package com.example.weatherappnew.ui;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherappnew.R;

import org.json.JSONObject;

public class SettingsActivity extends AppCompatActivity {

    RadioButton radioButtonC;
    RadioButton radioButtonF;
    Button saveButton;
    EditText radiusEditText;
    boolean isCheckedC;
    boolean isCheckedF;

    //variables for shared prefs
    public static final String SHARED_PREFS = "SharedPrefs";
    public static final String cTEXT = "tempC";
    public static final String fTEXT = "tempF";
    public static final String rTEXT = "radius";

    private boolean loadedTempC;
    private boolean loadedTempF;
    private int defaultUnit;
    private double radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        loadTemperatureUnitData();

        radioButtonC = findViewById(R.id.radioButtonC);
        radioButtonF = findViewById(R.id.radioButtonF);
        saveButton = findViewById(R.id.saveButtonSettingsScreen);
        radiusEditText = findViewById(R.id.radiusEditText);

        if(defaultUnit==1)
        {
            radioButtonC.setChecked(true);
        }
        else if(defaultUnit==2)
        {
            radioButtonF.setChecked(true);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fetch and set radio button data
                isCheckedC = radioButtonC.isChecked();
                isCheckedF = radioButtonF.isChecked();

                //apply validation and set radius value
                if(!radiusEditText.getText().toString().equals(""))
                {
                    if(Double.parseDouble(radiusEditText.getText().toString())<200 && Double.parseDouble(radiusEditText.getText().toString())>0)
                    {
                        Toast.makeText(getApplicationContext(), "Radius should be at least 200 meters", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(Double.parseDouble(radiusEditText.getText().toString())<1)
                    {
                        Toast.makeText(getApplicationContext(), "Radius must pe positive", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else
                    {
                        radius = Double.parseDouble(radiusEditText.getText().toString());
                    }
                }
                else
                {
                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                    radius = (double) sharedPreferences.getFloat(rTEXT, 2000);
                }

                //save data to shared prefs
                saveTemperatureUnitData();

                //go to previous activity
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void saveTemperatureUnitData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(cTEXT, isCheckedC);
        editor.putBoolean(fTEXT, isCheckedF);
        editor.putFloat(rTEXT, (float) radius);
        editor.apply();
    }

    public void loadTemperatureUnitData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        loadedTempC = sharedPreferences.getBoolean(cTEXT, true);
        loadedTempF = sharedPreferences.getBoolean(fTEXT, false);
        if(loadedTempC)
        {
            defaultUnit = 1;
        }
        else
        {
            defaultUnit = 2;
        }
    }
}