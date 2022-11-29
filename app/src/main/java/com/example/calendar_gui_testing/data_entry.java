package com.example.calendar_gui_testing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;

public class data_entry extends AppCompatActivity {

    public static String user_data = "";
    public String[] month_list = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState){
        compare_date();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_entry);
        initialize_dropdown();
        set_data_entry_date();

        display_previous_user_data();
    }

    public void compare_date(){
        LocalDateTime current_date_time = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            current_date_time = LocalDateTime.now();
        }

        System.out.println("Current time-> " + current_date_time);
    }

    public void display_previous_user_data(){
        ArrayList<String[]> previous_data = MainActivity.data_for_day_clicked;
        //This should reset the data_for_day_clicked variable, so we don't get duplicate stuff
        MainActivity.data_for_day_clicked = new ArrayList<>();

        int day_clicked = MainActivity.day_clicked;

        TextView user_data_text = (TextView) findViewById(R.id.previous_data);
        String data_display = "";
        if(previous_data.size() != 0){
            for(int x = 0; x < previous_data.size(); x++){
                String time = previous_data.get(x)[0];
                String workout_type = previous_data.get(x)[4];

                if(workout_type.contains("Running") || workout_type.contains("Walking")){
                    String distance = previous_data.get(x)[6];
                    data_display += time + " " + workout_type + " " + distance + " miles";
                }
                else if(workout_type.contains("Bench Press") || workout_type.contains("Bicep Curl")){
                    String weight_used = previous_data.get(x)[5];
                    String sets = previous_data.get(x)[7];
                    String reps = previous_data.get(x)[8];
                    data_display += time + " " + workout_type + " " + weight_used + "lbs " + sets + " sets " + reps + " reps";
                }
                else{
                    //This is for situps
                    String sets = previous_data.get(x)[7];
                    String reps = previous_data.get(x)[8];
                    data_display = time + " " + workout_type + " " + sets + " sets " + reps + " reps";
                }
                data_display += "\n";
            }
            user_data_text.setText("workout data  ->" + data_display);
            System.out.println("Data displayed successfully!");
        }

    }

    public void set_data_entry_date(){
        //This takes the date of the cell that was selected by the user, and puts it at the header of the activity
        int[] day_month_year = MainActivity.day_month_year_id;


        String day = String.valueOf(day_month_year[0]);
        String month = month_list[day_month_year[1]];
        String year = String.valueOf(day_month_year[2]);
        String header_string = day + " " + month + ", " + year;

        TextView header = (TextView) findViewById(R.id.header_text);
        header.setText(header_string);
    }

    public void go_to_calendar_view(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void initialize_dropdown(){
        Spinner dropdown = findViewById(R.id.workout_type_dropdown);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.workout_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        dropdown.setAdapter(adapter);
    }

    public String get_dropdown_content(){
        Spinner dropdown = findViewById(R.id.workout_type_dropdown);
        String selected_item = dropdown.getSelectedItem().toString();
        return(selected_item);
    }

    public String get_entry_data(){

        TextView weight_used_widget = (TextView) findViewById(R.id.weight_used);
        TextView distance_widget = (TextView) findViewById(R.id.distance);
        TextView sets_widget = (TextView) findViewById(R.id.sets);
        TextView reps_widget = (TextView) findViewById(R.id.reps);

        String workout_type = get_dropdown_content();
        String weight_used = weight_used_widget.getText().toString();
        String distance = distance_widget.getText().toString();
        String sets = sets_widget.getText().toString();
        String reps = reps_widget.getText().toString();

        switch(workout_type){
            case "Select Workout Type-":
                Toast warning_toast = Toast.makeText(this, "Please select a workout type!", Toast.LENGTH_SHORT);
                warning_toast.show();
                break;
            case "Running":
                sets_widget.setText("n/a");
                reps_widget.setText("n/a");
                weight_used_widget.setText("n/a");
                sets = "n/a";
                reps = "n/a";
                weight_used = "n/a";
                break;
            case "Walking":
                sets_widget.setText("n/a");
                reps_widget.setText("n/a");
                weight_used_widget.setText("n/a");
                sets = "n/a";
                reps = "n/a";
                weight_used = "n/a";
                break;
            case "Bench Press":
                distance_widget.setText("n/a");
                distance = "n/a";
                break;
            case "Bicep Curl":
                distance_widget.setText("n/a");
                distance = "n/a";
                break;
            case "Sit Up":
                distance_widget.setText("n/a");
                distance = "n/a";
                break;
        }

        Formatter format = new Formatter();
        Calendar gfg_calender = Calendar.getInstance();
        String current_time = String.valueOf(format.format("%tl:%tM", gfg_calender, gfg_calender));
        int[] day_month_year_id = MainActivity.day_month_year_id;
        String day = String.valueOf(day_month_year_id[0]);
        //We need to change this to the int of the month;
        String month = String.valueOf(day_month_year_id[1]);
        String year = String.valueOf(day_month_year_id[2]);
        String id_num = "0";

        //Look into the notes, that explains how we don't actually need the id_num any more!
        //Once we finish this, we then need to save this string to the file
        String data_line = current_time + ", " + day + ", " + month + ", " + year  + ", " + workout_type  + ", " + weight_used  + ", " + distance  + ", " + sets  + ", " + reps + ", " + id_num;
        System.out.println(data_line);
        return(data_line);
    }

    public void enter_data(View view){
        String entry_data = get_entry_data();
        user_data = entry_data;
        go_to_calendar_view(view);
    }

}
