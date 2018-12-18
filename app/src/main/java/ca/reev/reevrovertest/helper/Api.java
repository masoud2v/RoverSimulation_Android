package ca.reev.reevrovertest.helper;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {

    @FormUrlEncoded
    @POST("./")
    Call<JsonObject> GetCommands(@Field("rover_id") String rover_id);
}
