package com.example.tommy.masteryassistant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

/**
 * Created by Tommy on 9/27/2016.
 */

public class Create_Skills extends Activity {

    Button save, delete;
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    EditText name, hours, weeklygoal;
    TextView dbText;
    DBHandler dbHandler;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_skills);

        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.Mastery_Levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(), parent.getItemIdAtPosition(position) + " selcted", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        name = (EditText) findViewById(R.id.editText8);
        hours = (EditText) findViewById(R.id.editText9);
        weeklygoal = (EditText) findViewById(R.id.editText5);
        save = (Button) findViewById(R.id.saveButton);
        delete = (Button) findViewById(R.id.deleteButton);
        dbText = (TextView) findViewById(R.id.textView);

        dbHandler = new DBHandler(this);
        printDatabase();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void printDatabase() {
        String dbString = dbHandler.databaseToString();
        dbText.setText(dbString);
        name.setText("");
    }

    public void saveButtonClicked(View view) {
        String skillName = name.getText().toString();
        String weeklyText = weeklygoal.getText().toString();
        int weeklyGoal;
        if (weeklyText.equals(""))
            weeklyGoal = 10;
        else
            weeklyGoal = Integer.parseInt(weeklyText);

        //if skillname or weekly goal invalid, return
        if (!skillTextValidation(skillName, this) ||
                !weeklyGoalValidation(weeklyGoal, this)){
            return;
        }

        int hours_as_seconds;
        if (hours.getText().toString().equals(""))
            hours_as_seconds = 0;
        else{
            hours_as_seconds = Integer.parseInt(hours.getText().toString()) * 3600;
        }

        int MasteryLevelUnique_ID = (int) spinner.getSelectedItemId();
        int MasteryLevel_Hours;

        switch (MasteryLevelUnique_ID) {
            case 0:
                MasteryLevel_Hours = 100;
                break;
            case 1:
                MasteryLevel_Hours = 1000;
                break;
            case 2:
                MasteryLevel_Hours = 5000;
                break;
            case 3:
                MasteryLevel_Hours = 10000;
                break;
            default:
                MasteryLevel_Hours = 0;
                break;
        }

        Skill skill = new Skill(skillName, hours_as_seconds, MasteryLevel_Hours, weeklyGoal);
        dbHandler.addSkill(skill);
        printDatabase();
        Preferences.setCurrentSkill(this, skillName);
    }

    public boolean skillTextValidation(String name, Context context){
        DBHandler dbHandler = new DBHandler(context);
        SQLiteDatabase sqLiteDatabase = dbHandler.getReadableDatabase();
        Cursor cursor = dbHandler.getInformation(sqLiteDatabase);
        cursor.moveToFirst();
        boolean skillInDatabase = false;

        //get cursor to point to Current Skill
        while (!cursor.isAfterLast()){
            if (cursor.getString(0).equals(name)){
                skillInDatabase = true;
                break;
            }
            cursor.moveToNext();
        }

        if (name.equals("")) {
            Toast.makeText(getBaseContext(), "Skill Name is Empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (skillInDatabase) {
            Toast.makeText(getBaseContext(), "Duplicate Skill Not Valid", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean weeklyGoalValidation(int weeklyGoal, Context context){
        if (weeklyGoal > 133){
            Toast.makeText(getBaseContext(), "Max Weekly Goal is 133 Hours", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //Delete items
    public void deleteButtonClicked(View view) {
        // dbHandler delete needs string to find in the db
        String inputText = name.getText().toString();
        dbHandler.deleteSkill(inputText);
        if(inputText.equals(Preferences.getCurrentSkill(this))){
            Preferences.setCurrentSkill(this, "No Current Skill Set");
        }
        printDatabase();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Create_Skills Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}