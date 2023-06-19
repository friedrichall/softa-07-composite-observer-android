package de.thro.inf.prg3.a07;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // this will inflate the layout from res/layout/activity_main.xml
        setContentView(R.layout.activity_main);

        // add your code here
		CheckBox vegetarianCB = (CheckBox) findViewById(R.id.vegetarianCB);
		Button refreshBtn = (Button) findViewById(R.id.refreshBtn);
		ListView lv = (ListView) findViewById(R.id.listView);

		lv.setAdapter(new ArrayAdapter<>(
			MainActivity.this,     // context we're in; typically the activity
			R.layout.meal_entry,   // where to find the layout for each item
			new String[] {"Hello", "world", "test", "test", "testtts", "tralalalaa"} // your data
		));

		refreshBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(vegetarianCB.isChecked()){
					lv.setAdapter(new ArrayAdapter<>(
						MainActivity.this,     // context we're in; typically the activity
						R.layout.meal_entry,   // where to find the layout for each item
						new String[] {"Ein vegetarischer Eintrag"} // your data
					));
				}
				else{
					lv.setAdapter(new ArrayAdapter<>(
						MainActivity.this,     // context we're in; typically the activity
						R.layout.meal_entry,   // where to find the layout for each item
						new String[] {"Fleischgericht"} // your data
					));
				}
			}
		});
    }
}
