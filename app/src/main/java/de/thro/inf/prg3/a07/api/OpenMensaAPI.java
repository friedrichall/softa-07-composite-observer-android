package de.thro.inf.prg3.a07.api;

import java.util.List;

import de.thro.inf.prg3.a07.model.Meal;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Peter Kurfer on 11/19/17.
 */

public interface OpenMensaAPI {
    // TODO add method to get meals of a day
    // example request: GET /canteens/229/days/2017-11-22/meals
	@GET("/api/v2/canteens/269/days/{date}/meals")
	Call<List<Meal>> getMeals(@Path("date") String date);
}
