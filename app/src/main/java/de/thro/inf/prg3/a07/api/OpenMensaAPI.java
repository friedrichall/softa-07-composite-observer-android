package de.thro.inf.prg3.a07.api;

import java.util.List;

import de.thro.inf.prg3.a07.model.Meal;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Peter Kurfer on 11/19/17.
 */

public interface OpenMensaAPI {
    // TODO add method to get meals of a day
    // example request: GET /canteens/229/days/2017-11-22/meals
	@GET("/canteens/269/days/2023-06-20/meals")
	public Call<List<Meal>> getMeals(String date);
}
