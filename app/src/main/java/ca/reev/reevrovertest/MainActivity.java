package ca.reev.reevrovertest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import ca.reev.reevrovertest.helper.Api;
import ca.reev.reevrovertest.helper.Config;
import ca.reev.reevrovertest.helper.NetworkHandler;
import ca.reev.reevrovertest.model.Point;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Api api = NetworkHandler.StartApi(Config.baseUrl);

        Call<JsonObject> call = api.GetCommands(Config.roverId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.code() == 200) {
                    JsonObject start_point = response.body().get("start_point").getAsJsonObject();
                    JsonArray weirs = response.body().get("weirs").getAsJsonArray();
                    String command = response.body().get("command").getAsString();

                    int startPointX = start_point.get("x").getAsInt();
                    int startPointY = start_point.get("y").getAsInt();
                    Point startPoint = new Point(startPointX, startPointY);

                    ArrayList<Point> weirsArrayList = new ArrayList<>();
                    for (int i = 0; i < weirs.size(); i++) {
                        weirsArrayList.add(new Point(weirs.get(i).getAsJsonObject().get("x").getAsInt(),
                                weirs.get(i).getAsJsonObject().get("y").getAsInt()));
                    }

                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.rover);
                    CanvasView canvasView = new CanvasView(MainActivity.this, startPoint, weirsArrayList, command, bitmap);

                    canvasView.setNumberOfColumns(10);
                    canvasView.setNumberOfRows(20);

                    setContentView(canvasView);
                } else
                    ShowToast();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                ShowToast();
            }
        });
    }

    private void ShowToast() {
        Toast.makeText(MainActivity.this, "Please Check your internet connection", Toast.LENGTH_LONG).show();
    }
}
