package com.example.calendar_gui_testing;

import static java.lang.Integer.parseInt;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<String> daysOfMonth;
    private final OnItemListener onItemListener;

    public CalendarAdapter(ArrayList<String> daysOfMonth, OnItemListener onItemListener) {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int)(parent.getHeight() * 0.1666666666);
        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {

        ArrayList<Integer> workout_days = MainActivity.workout_days;
        holder.dayOfMonth.setText(daysOfMonth.get(position));
        holder.exerciseOfDay.setText("");

        for(int x = 0; x < workout_days.size(); x++){
            //I think here, we need to change this comparison. If one of the days in the workout_days list == the day that is
            //being printed to the screen, we mark that day as being worked out

            //if(position == workout_days.get(x)){
            if(daysOfMonth.get(position) != ""){
                if(Integer.parseInt((daysOfMonth.get(position)).replaceAll(" ", "")) == workout_days.get(x)){
                    holder.dayOfMonth.setText(daysOfMonth.get(position));
                    holder.exerciseOfDay.setText("Worked\nOut!");
                    holder.exerciseOfDay.setTextColor(Color.parseColor("#48D1CC"));
                }
            }


        }

        //if(position == 8){
            //holder.dayOfMonth.setText(daysOfMonth.get(position));
            //holder.exerciseOfDay.setText("Worked\nOut!");
            //holder.exerciseOfDay.setTextColor(Color.parseColor("#48D1CC"));

        //}
        //else{
            //holder.dayOfMonth.setText(daysOfMonth.get(position));
            //holder.exerciseOfDay.setText("");
        //}

    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    public interface OnItemListener{
        void onItemClick(int position, String dayText);
    }



}
