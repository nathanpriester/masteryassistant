package com.example.tommy.masteryassistant;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.webkit.ServiceWorkerClient;
import android.widget.ProgressBar;

/**
 * Created by Danie_000 on 10/5/2016.
 */

public class Chronometer implements Runnable {
    public static final long MILIS_TO_SECONDS = 1000;
    public static final long MILIS_TO_MINUTES = 60000;
    public static final long MILIS_TO_HOURS = 3600000;
    private String currentSkill;
    private Context mContext;
    private long mStartTime;
    int seconds_as_int;
    private DBHandler dbHandler;
    private SQLiteDatabase sqLiteDatabase;
    private Cursor cursor;



    public Chronometer(Context mContext) {

        this.mContext = mContext;
        //Get skill data bassed off preference NEEDS TO BE IMPLEMENTED *current retrieval temporary
        currentSkill = Preferences.getCurrentSkill(mContext);

        this.dbHandler = new DBHandler(mContext);
        this.sqLiteDatabase = dbHandler.getReadableDatabase();
        this.cursor = dbHandler.getInformation(sqLiteDatabase);
        cursor.moveToFirst();

        while(!cursor.getString(0).equals(currentSkill)){
            cursor.moveToNext();
        }


        //seconds_as_int = cursor.getInt(1);
        cursor.close();
        dbHandler.close();
    }

    private boolean mIsRunning;

    public void start( ) {
        mStartTime = System.currentTimeMillis();
        mIsRunning = true;
    }

    public void stop(){
        mIsRunning = false;
    }

    @Override
    public void run() {
        DBHandler dbHandler = new DBHandler(mContext);
        Milestone_Helper milestone_helper = new Milestone_Helper();
        long elapsed_time, started_time = System.currentTimeMillis();
        //seconds_as_int = dbHandler.getSkillTime(currentSkill);
        int starting_db_total_seconds = dbHandler.getSkillTime(currentSkill);
        String currentLevel = milestone_helper.getLevel(starting_db_total_seconds);
        String currentRank = milestone_helper.getRank(starting_db_total_seconds);
        int estimated_db_total_seconds = 0;
        while(mIsRunning){

            elapsed_time = System.currentTimeMillis() - started_time;

            if (elapsed_time >= 1000){
                started_time = System.currentTimeMillis();
                dbHandler.addSkillTime(currentSkill, 1);
            }
            long since = System.currentTimeMillis() - mStartTime;
            int since_seconds = (int) (since / 1000);
            int seconds = (int) ((since / 1000) % 60);
            int minutes = (int) ((since / MILIS_TO_MINUTES) % 60);
            int hours = (int) ((since / MILIS_TO_HOURS) % 24);

            estimated_db_total_seconds += 300;

            int db_seconds_display = (estimated_db_total_seconds % 3600) % 60;
            int db_minutes_display = (estimated_db_total_seconds % 3600) / 60;
            int db_hours_display = (estimated_db_total_seconds / 3600);

            String tot_hours = Integer.toString(db_hours_display) + " Hours";
            String tot_leftOver_minutes = Integer.toString(db_minutes_display) + " Minutes";
            String tot_leftOver_seconds = Integer.toString(db_seconds_display) + " Seconds";


            ((Skill_Hub)mContext).updateTimerText(String.format(
                    "%02d : %02d : %02d", hours, minutes, seconds), tot_hours, tot_leftOver_minutes, tot_leftOver_seconds,
                    estimated_db_total_seconds);

            if (!currentLevel.equals(milestone_helper.getLevel(estimated_db_total_seconds))){
                currentLevel = milestone_helper.getLevel(estimated_db_total_seconds);
                currentRank = milestone_helper.getRank(estimated_db_total_seconds);
                ((Skill_Hub)mContext).updateLevelTitle(currentLevel);
                ((Skill_Hub)mContext).updateRankTitle(currentRank);
                (mContext).startActivity(new Intent(mContext, Pop.class));
            }
            else if (!currentRank.equals(milestone_helper.getRank(estimated_db_total_seconds))){
                currentRank = milestone_helper.getRank(estimated_db_total_seconds);
                ((Skill_Hub)mContext).updateRankTitle(currentRank);
                (mContext).startActivity(new Intent(mContext, Pop.class));
            }
            else if (estimated_db_total_seconds == 36000000){
                (mContext).startActivity(new Intent(mContext, Pop.class));
            }

            //Sleep the thread for a short amount, to prevent high CPU usage!
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}