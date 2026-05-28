package de.garten.training.depotflow.data.api

import de.garten.training.depotflow.core.LegacyLogger
import de.garten.training.depotflow.core.ResultCallback
import de.garten.training.depotflow.data.api.dto.OutboxDto
import de.garten.training.depotflow.data.api.dto.SyncResponseDto
import de.garten.training.depotflow.data.api.dto.WorkOrderDto
import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitDepotApi(baseUrl: String) : DepotApiClient {

    private val api: DepotApi

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        api = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DepotApi::class.java)
    }

    override fun loadWorkOrders(
        depotId: String,
        changedSince: String?,
        callback: ResultCallback<List<WorkOrderDto>>,
    ) {
        enqueue("loadWorkOrders", api.getWorkOrders(depotId, changedSince), callback)
    }

    override fun loadWorkOrder(serverId: String, callback: ResultCallback<WorkOrderDto>) {
        enqueue("loadWorkOrder", api.getWorkOrder(serverId), callback)
    }

    override fun completeWorkOrder(
        serverId: String,
        payload: OutboxDto,
        callback: ResultCallback<WorkOrderDto>,
    ) {
        enqueue("completeWorkOrder", api.completeWorkOrder(serverId, payload), callback)
    }

    override fun pushOutbox(
        payload: List<OutboxDto>,
        callback: ResultCallback<SyncResponseDto>,
    ) {
        enqueue("pushOutbox", api.pushOutbox(payload), callback)
    }

    private fun <T> enqueue(operation: String, call: Call<T>, callback: ResultCallback<T>) {
        call.enqueue(
            object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        callback.onSuccess(body)
                        return
                    }
                    callback.onError(
                        IOException("HTTP ${response.code()} while running $operation"),
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    LegacyLogger.e("api", "Request failed: $operation", t)
                    callback.onError(t)
                }
            },
        )
    }

    private companion object {
        const val CONNECT_TIMEOUT_SECONDS = 10L
        const val READ_TIMEOUT_SECONDS = 20L
    }
}
