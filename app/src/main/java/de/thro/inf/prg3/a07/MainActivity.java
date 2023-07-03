package de.thro.inf.prg3.a07;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
	private int canteenId = 269;
	ListView lv;
	Spinner canteenSpinner;

	private void setupRetrofit() {
		// use this to intercept all requests and output them to the logging facilities
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

		OkHttpClient client = new OkHttpClient.Builder()
			.addInterceptor(interceptor)
			.build();

		Gson gson = new GsonBuilder()
			//.registerTypeAdapter(Joke.class, new JokeAdapter())
			.registerTypeAdapter(Meal.class, new TypeAdapter<Meal>() {
				Gson gson;
				@Override
				public void write(JsonWriter out, Meal value) throws IOException {
					//dont wanna send data to API
				}

				@Override
				public Meal read(JsonReader in) throws IOException {
					Meal result = gson.fromJson(in, Meal.class);
					return result;
				}
			})
			.create();

		Retrofit retrofit = new Retrofit.Builder()
			.addConverterFactory(GsonConverterFactory.create(new Gson()))
			//.addConverterFactory(GsonConverterFactory.create(gson)) //why doesnt this work? //TODO: get prices from JSON with retrofit + gson
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
	private void doAPICallAsync(boolean onlyVeg){
		//Call<List<Meal>> callAsync = openMensaAPI.getMeals(canteenId, "2023-07-04");
		Call<List<Meal>> callAsync = openMensaAPI.getMeals(canteenId, dateFormat.format(getCurrentDate()));
		callAsync.enqueue(new Callback<List<Meal>>() {
			@Override
			public void onResponse(Call<List<Meal>> call, Response<List<Meal>> response) {
				if (response.isSuccessful())
				{
					logger.info("BODY(): " + response.body().toString());
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
	private void setCanteenId(){
		int id;
		if (canteenSpinner.getSelectedItem() == "Kesslerplatz")
			canteenId = 268;
		 if (canteenSpinner.getSelectedItem() == "Hohfederstraße")
			canteenId = 269;
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // this will inflate the layout from res/layout/activity_main.xml
        setContentView(R.layout.activity_main);

		SwitchCompat vegetarianSwitch = (SwitchCompat) findViewById(R.id.vegetarianSwitch);
		lv = (ListView) findViewById(R.id.listView_meals);
		canteenSpinner = (Spinner) findViewById(R.id.spinner_canteens);

		ArrayAdapter<CharSequence> arrAdapter = ArrayAdapter.createFromResource(this, R.array.canteens, android.R.layout.simple_spinner_item);
		arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		canteenSpinner.setAdapter(arrAdapter);

		canteenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int id, long l) {
				if (id == 0) canteenId = 269;
				else if (id == 1) canteenId = 268;
				doAPICallAsync(vegetarianSwitch.isChecked());
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				//do nothing
			}
		});
		vegetarianSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
				if(isChecked) doAPICallAsync(true);
				else doAPICallAsync(false);
			}
		});

		setupRetrofit();
		doAPICallAsync(vegetarianSwitch.isChecked());

		/*switchBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(canteenId == 268) canteenId = 269;
				else if(canteenId == 269) canteenId = 268;

				doAPICallAsync(vegetarianCB.isChecked());
			}
		});*/
    }


}
