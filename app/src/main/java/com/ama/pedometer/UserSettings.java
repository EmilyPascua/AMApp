package com.ama.pedometer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.panfei.pedometer.R;

public class UserSettings extends AppCompatActivity {

    public String genderText;
    private String weightText;
    private String heightText;
    public int weight;
    public int height;
    public Button btn;

    public UserSettings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        itemSelect();

        btn = (Button) findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSettings.this, MainActivity.class);
                intent.putExtra("weightValue", weight);
                intent.putExtra("heightValue", height);
                intent.putExtra("heightText", heightText);
                startActivity(intent);
            }
        });

    }

//    public static UserSettings newInstance(String param1, String param2) {
//        UserSettings fragment = new UserSettings();
//        Bundle args = new Bundle();
//        return fragment;
//    }

    public void itemSelect(){

        Spinner gspinner =  (Spinner) findViewById(R.id.sp_gender_type);
        Spinner hspinner =  (Spinner) findViewById(R.id.sp_height_type);
        Spinner wspinner = (Spinner) findViewById(R.id.sp_weight_type);

        genderText = gspinner.getSelectedItem().toString();
        weightText = wspinner.getSelectedItem().toString();
        heightText = hspinner.getSelectedItem().toString();
        int wpos = wspinner.getSelectedItemPosition();
        int hpos = hspinner.getSelectedItemPosition();
        switch(wpos){
            case 0: weight = 105;
                break;
            case 1: weight = 115;
                break;
            case 2: weight = 125;
                break;
            case 3: weight = 135;
                break;
            case 4: weight = 145;
                break;
            case 5: weight = 155;
                break;
            case 6: weight = 165;
                break;
            case 7: weight = 175;
                break;
            case 8: weight = 185;
                break;
            case 9: weight = 195;
                break;
            default: weight = 105;
                break;
        }
        switch(hpos){
            case 0: height = 43;
                break;
            case 1: height = 44;
                break;
            case 2: height = 45;
                break;
            case 3: height = 46;
                break;
            case 4: height = 47;
                break;
            case 5: height = 48;
                break;
            case 6: height = 49;
                break;
            case 7: height = 50;
                break;
            case 8: height = 51;
                break;
            case 9: height = 52;
                break;
            case 10: height = 53;
                break;
            case 11: height = 54;
                break;
            case 12: height = 55;
                break;
            case 13: height = 56;
                break;
            case 14: height = 57;
                break;
            case 15: height = 58;
                break;
            case 16: height = 59;
                break;
            case 17: height = 60;
                break;
            default: height = 43;
                break;
        }

        Log.e("genderText",genderText);
        Log.e("height", String.format("value = %d", height));
        Log.e("heightText",weightText);
        Log.e("weight", String.format("value = %d", weight));
        Log.e("weightText",heightText);

    }
    public void onClickSubmit(View v)
    {
    }
}





