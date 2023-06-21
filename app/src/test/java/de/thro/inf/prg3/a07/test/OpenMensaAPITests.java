package de.thro.inf.prg3.a07.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import de.thro.inf.prg3.a07.api.OpenMensaAPI;
import de.thro.inf.prg3.a07.model.Meal;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Created by Peter Kurfer on 11/19/17.
 */

public class OpenMensaAPITests {
    private static final Logger logger = Logger.getLogger(OpenMensaAPITests.class.getName());
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private OpenMensaAPI openMensaAPI;

	private static Date getCurrentDate(){
		LocalDate localDate = java.time.LocalDate.now();
		//LocalDate localDate = LocalDate.of(2023, 6, 21);
		while(localDate.getDayOfWeek() == DayOfWeek.SATURDAY || localDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
			localDate = localDate.plusDays(1);
			logger.info("added day to " + localDate.getDayOfWeek() + ", " + localDate);
		}
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

    @BeforeEach
    public void setup() {
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

    @Test
    public void testGetMeals() throws IOException {
        // TODO prepare call
		Call<List<Meal>> call = openMensaAPI.getMeals(dateFormat.format(getCurrentDate()));
        // TODO execute the call synchronously
		Response<List<Meal>> resp = call.execute();
        // TODO unwrap the body
		if (!resp.isSuccessful())
			throw new IOException("Request failed: " + resp.code());
        List<Meal> meals = resp.body();

        assertNotNull(meals);
        assertNotEquals(0, meals.size());

        for(Meal m : meals){
            logger.info(m.toString());
        }
    }

}
