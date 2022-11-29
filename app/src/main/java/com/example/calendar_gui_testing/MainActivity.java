package com.example.calendar_gui_testing;

import static junit.framework.TestCase.assertEquals;
import static java.time.YearMonth.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Optional;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.junit.Test;


public class MainActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener{
    //Defines which file we want to store the users data to
    public static final String file_name = "data_file.txt";
    //Global variable, which stores user data from the data_entry activity
    public String user_data = "";
    public static ArrayList<String[]> data = new ArrayList<>();
    public static ArrayList<String[]> data_for_day_clicked = new ArrayList<>();
    public static int day_clicked = 0;
    public static int current_Day = 0;

    public static int[] day_month_year_id = {0,0,0,0};

    //Instead of cell_positions, this needs to store actual date values on which we worked out. Look into notes for more info
    public static ArrayList<Integer> workout_days = new ArrayList<>();

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        check_file_existance();
        check_for_new_entry();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            selectedDate = LocalDate.now();
        }

        setMonthView();
        get_data_test();
        get_workout_days_by_month_test();
        //String test_data = "12:11, 18, 10, 2022, bench_press, 115, n/a, 6, 8, 9";
        //save(test_data);
        //go_to_data_entry_activity();
    }

    @Test
    public void get_workout_days_by_month_test(){
        ArrayList<String[]> testing_data = new ArrayList<>();
        String[] line_one = {"3:06", " 3", " 10", " 2022", " Bench Press", " 100", " n/a", " 10", " 10", " 0"};
        String[] line_two = {"3:06", " 4", " 10", " 2022", " Running", " n/a", " 10", " n/a", " n/a", " 0"};
        String[] line_three = {"3:06", " 5", " 10", " 2022", " Bench Press", " 100", " n/a", " 10", " 10", " 0"};
        String[] line_four = {"3:06", " 6", " 10", " 2022", " Running", " n/a", " 10", " n/a", " n/a", " 0"};
        testing_data.add(line_one);
        testing_data.add(line_two);
        testing_data.add(line_three);
        testing_data.add(line_four);

        ArrayList<Integer> result_data = get_workout_days_by_month(testing_data);

        Integer[] expected_results = {3, 4, 5, 6};
        //Printing the dates from the data where the user worked out
        for(int x = 0; x < result_data.size(); x++){
            assertEquals(result_data.get(x), expected_results[x]);
        }
    }

    @Test
    public void get_data_test(){
        //Here, we are testing the get_data function
        String test_input = "3:06, 3, 10, 2022, Bench Press, 100, n/a, 10, 10, 0\n3:06, 3, 10, 2022, Running, n/a, 10, n/a, n/a, 0";
        ArrayList<String[]> actual_result = get_data(test_input);

        ArrayList<String[]> expected_result = new ArrayList();
        String[] line_one = {"3:06", " 3", " 10", " 2022", " Bench Press", " 100", " n/a", " 10", " 10", " 0"};
        String[] line_two = {"3:06", " 3", " 10", " 2022", " Running", " n/a", " 10", " n/a", " n/a", " 0"};
        expected_result.add(line_one);
        expected_result.add(line_two);

        assertEquals(expected_result.size(), actual_result.size());
        for(int x = 0; x < expected_result.size(); x++){
            for(int y = 0; y < expected_result.get(x).length; y++){
                assertEquals(expected_result.get(x)[y], actual_result.get(x)[y]);
            }
        }

    }

    public void check_for_new_entry(){
        //This checks the user_data variable to see if it has changed
        user_data = data_entry.user_data;
        if(user_data != ""){
            save(user_data);
            user_data = "";
        }

    }

    private void initWidgets() {
        data = get_data(load());
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);

    }

    public void check_file_existance(){
        System.out.println("Running Here!");
        FileInputStream data_file = null;
        try{
            File myObj = new File("filename.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);
        workout_days = get_workout_days_by_month(data);
        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);


        //for(int x = 0; x <= 11; x++){
            //String content = "12:11, 6, " + x + ", 2022, bench_press, 115, n/a, 6, 8, " + (x+9);
            //save(content);
        //}

        //This is where we initialize the days_worked_out list based on what month it is
    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            YearMonth yearMonth = YearMonth.from(date);
            int daysInMonth = yearMonth.lengthOfMonth();
            LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
            int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

            for(int i = 1; i <= 42; i++){
                if(i <= dayOfWeek || i > daysInMonth + dayOfWeek){
                    daysInMonthArray.add("");
                }
                else{
                    daysInMonthArray.add(String.valueOf(i - dayOfWeek));
                }
            }
        }
        return daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            return (date.format(formatter));
        }
        return("IDK, you're prolly never gonna find this ngl");
    }

    public void previousMonthAction(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            selectedDate = selectedDate = selectedDate.minusMonths(1);
        }
        setMonthView();
    }

    public void nextMonthAction(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            selectedDate = selectedDate.plusMonths(1);
        }
        setMonthView();
    }

    @Override
    public void onItemClick(int position, String dayText) {
        if(dayText.equals("")){
            String message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<String[]> get_data(String input){

        String[] lines = input.split("\r\n|\r|\n");
        ArrayList<String[]> lines_divided = new ArrayList<String[]>();
        for(int x = 0; x < lines.length; x++){
            lines_divided.add(lines[x].split(","));
        }
        return(lines_divided);
    }

    public void go_to_data_entry_activity(){
        Intent intent = new Intent(this, data_entry.class);
        startActivity(intent);
    }

    public ArrayList<Integer> get_workout_days_by_month(ArrayList<String[]> data) {
        //Instead of cell positions, this needs to store actual days that the user worked out!!!
        //Make sure the month that the calendar is on matches the month that is in the row
        //If we do this, it should work fine
        workout_days = new ArrayList<>();
        //Reinitializing workout_days to be blank
        String current_month_string = remove_year_from_month(monthYearFromDate(selectedDate));
        String current_year_string = remove_month_from_year(monthYearFromDate(selectedDate));
        int current_month = determine_month_int(current_month_string);
        int current_year = Integer.parseInt(current_year_string.replace(" ", ""));

        for(int x = 0; x < data.size(); x++){
            int row_month = Integer.parseInt((data.get(x)[2]).replace(" ", ""));
            int row_year = Integer.parseInt((data.get(x)[3]).replace(" ", ""));
            //Instead of looking at the ninth element of the array, id_num, we are now looking
            //at the first, day
            //int workout_cell = Integer.parseInt((data.get(x)[9]).replace(" ", ""));
            int workout_cell = Integer.parseInt((data.get(x)[1]).replace(" ", ""));
            if((current_month == row_month) && (current_year == row_year)){
                //Here, we need to add the current day, instead of workout cell
                workout_days.add(workout_cell);
            }

        }
        return(workout_days);
    }

    public void get_data_for_day_clicked(int day, int month, int year){
        for(int x = 0; x < data.size(); x++){
            int row_day = Integer.parseInt((data.get(x)[1]).replace(" ", ""));
            int row_month = Integer.parseInt((data.get(x)[2]).replace(" ", ""));
            int row_year = Integer.parseInt((data.get(x)[3]).replace(" ", ""));

            if((month == row_month) && (year == row_year) && (day == row_day)){
                data_for_day_clicked.add(data.get(x));
            }
        }

    }

    public void cell_clicked(View view){
        int cell_day = Integer.parseInt(((TextView) view ).getText().toString());
        day_clicked = cell_day;
        int cell_month = determine_month_int(remove_year_from_month(monthYearFromDate(selectedDate)));
        int cell_year = Integer.valueOf(remove_month_from_year(monthYearFromDate(selectedDate)));
        //We also need to get the cell position!!!
        //Were going to cheese it. We are going to put an invisible number in each cell that stores its position
            //We are going to then check what that number is here, and then put it in the day_month_year_id list
        //*** I dont know how to get the position out of the cell that is clicked!!!

        get_data_for_day_clicked(cell_day, cell_month, cell_year);

        day_month_year_id[0] = cell_day;
        day_month_year_id[1] = cell_month;
        day_month_year_id[2] = cell_year;
        //day_month_year_id[3] = cell_position;

        go_to_data_entry_activity();
    }

    private int determine_month_int(String current_month) {
        int month_int = 0;
        switch(current_month) {
            case "January":
                month_int = 0;
                break;
            case "February":
                month_int = 1;
                break;
            case "March":
                month_int = 2;
                break;
            case "April":
                month_int = 3;
                break;
            case "May":
                month_int = 4;
                break;
            case "June":
                month_int = 5;
                break;
            case "July":
                month_int = 6;
                break;
            case "August":
                month_int = 7;
                break;
            case "September":
                month_int = 8;
                break;
            case "October":
                month_int = 9;
                break;
            case "November":
                month_int = 10;
                break;
            case "December":
                month_int = 11;
                break;
        }
        return(month_int);
    }

    private String remove_month_from_year(String current_year) {
        String result = "";
        for(int x = current_year.length()-5; x < current_year.length(); x++){
            result += current_year.charAt(x);
        }
        result = result.replaceAll(" ", "");
        return(result);
    }

    public String remove_year_from_month(String current_month){
        current_month = current_month.substring(0, current_month.length()-5);
        return(current_month);
    }

    public String load(){
        FileInputStream data_file = null;
        String content = "";
        try{
            data_file = openFileInput(file_name);
            InputStreamReader reader = new InputStreamReader(data_file);
            BufferedReader buffered_reader = new BufferedReader(reader);
            StringBuilder string_builder = new StringBuilder();
            while((content = buffered_reader.readLine()) != null){
                string_builder.append(content).append("\n");
            }
            content = string_builder.toString();
            buffered_reader.close();
            reader.close();
            data_file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(data_file != null){
                try{
                    data_file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return(content);
    }

    public void save(String content){
        FileOutputStream data_file = null;
        String original_content = load();
        content = original_content + content;
        try{
            data_file = openFileOutput(file_name, MODE_PRIVATE);
            data_file.write(content.getBytes());
            data_file.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found exception!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Input Output Exception");
            e.printStackTrace();
        } finally{
            if(data_file != null){
                try{
                    data_file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}