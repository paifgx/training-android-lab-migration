package de.garten.training.depotflow.data.api;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.garten.training.depotflow.core.LegacyLogger;
import de.garten.training.depotflow.core.ResultCallback;
import de.garten.training.depotflow.data.api.dto.OutboxDto;
import de.garten.training.depotflow.data.api.dto.SyncResponseDto;
import de.garten.training.depotflow.data.api.dto.WorkOrderDto;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitDepotApi implements DepotApiClient {

    private final DepotApi api;

    public RetrofitDepotApi(String baseUrl) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(DepotApi.class);
    }

    @Override
    public void loadWorkOrders(String depotId, String changedSince, ResultCallback<List<WorkOrderDto>> callback) {
        enqueue("loadWorkOrders", api.getWorkOrders(depotId, changedSince), callback);
    }

    @Override
    public void loadWorkOrder(String serverId, ResultCallback<WorkOrderDto> callback) {
        enqueue("loadWorkOrder", api.getWorkOrder(serverId), callback);
    }

    @Override
    public void completeWorkOrder(String serverId, OutboxDto payload, ResultCallback<WorkOrderDto> callback) {
        enqueue("completeWorkOrder", api.completeWorkOrder(serverId, payload), callback);
    }

    @Override
    public void pushOutbox(List<OutboxDto> payload, ResultCallback<SyncResponseDto> callback) {
        enqueue("pushOutbox", api.pushOutbox(payload), callback);
    }

    private <T> void enqueue(final String operation, Call<T> call, final ResultCallback<T> callback) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                    return;
                }
                callback.onError(new IOException("HTTP " + response.code() + " while running " + operation));
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                LegacyLogger.e("api", "Request failed: " + operation, t);
                callback.onError(t);
            }
        });
    }
}
