package de.thro.inf.prg3.a07;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.thro.inf.prg3.a07.api.OpenMensaAPI;
import de.thro.inf.prg3.a07.model.Meal;
import retrofit2.Callback;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
	Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private OpenMensaAPI openMensaAPI;
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

	ListView lv;

	private void setupRetrofit() {
		// use this to intercept all requests and output them to the logging facilities
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

		OkHttpClient client = new OkHttpClient.Builder()
			.addInterceptor(interceptor)
			.build();

		Retrofit retrofit = new Retrofit.Builder()
			.addConverterFactory(GsonConverterFactory.create())
			.baseUrl("http://openmensa.org")
			.client(client)
			.build();

		openMensaAPI = retrofit.create(OpenMensaAPI.class);
	}
	private Date getCurrentDate(){
		LocalDate localDate = java.time.LocalDate.now();
		//LocalDate localDate = LocalDate.of(2023, 6, 21);
		while(localDate.getDayOfWeek() == DayOfWeek.SATURDAY || localDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
			localDate = localDate.plusDays(1);
			logger.info("added day to " + localDate.getDayOfWeek() + ", " + localDate);
		}
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // this will inflate the layout from res/layout/activity_main.xml
        setContentView(R.layout.activity_main);

        // add your code here
		CheckBox vegetarianCB = (CheckBox) findViewById(R.id.vegetarianCB);
		Button refreshBtn = (Button) findViewById(R.id.refreshBtn);
		lv = (ListView) findViewById(R.id.listView);

		setupRetrofit();

		doAPICallAsync(vegetarianCB.isChecked());

		refreshBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				doAPICallAsync(vegetarianCB.isChecked());
			}
		});
    }
	private void doAPICallAsync(boolean onlyVeg){
		Call<List<Meal>> callAsync = openMensaAPI.getMeals(dateFormat.format(getCurrentDate()));
		callAsync.enqueue(new Callback<List<Meal>>() {
			@Override
			public void onResponse(Call<List<Meal>> call, Response<List<Meal>> response) {
				if (response.isSuccessful())
				{
					List<Meal> meals = response.body();
					List<String> mealsStr = new ArrayList<>(0);

					if(onlyVeg){
						for(Meal m : meals){
							if (m.isVegetarian()){
								mealsStr.add(m.getName());
								logger.info("added to String List: " + m.getName());
							}
						}
					}
					else{
						for(Meal m : meals){
							mealsStr.add(m.getName());
							logger.info("added to String List: " + m.getName());
						}
					}
					lv.setAdapter(new ArrayAdapter<>(
						MainActivity.this,     // context we're in; typically the activity
						R.layout.meal_entry,   // where to find the layout for each item
						mealsStr
					));
					logger.info("refreshed and not checked vegCB");

				}
				else
				{
					System.out.println("Request Error :: " + response.errorBody());
				}
			}

			@Override
			public void onFailure(Call<List<Meal>> call, Throwable t) {

			}
		});
	}

}
