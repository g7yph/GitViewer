package dev.icerock.gitviewer.data.util

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.lang.reflect.Type

internal class ResultCallAdapter<T>(private val responseType: Type) : CallAdapter<T, Call<Result<T>>> {
    override fun responseType() = responseType

    override fun adapt(call: Call<T>): Call<Result<T>> = ResultCall(delegate = call)
}

private class ResultCall<T>(val delegate: Call<T>) : Call<Result<T>> {

    override fun enqueue(callback: Callback<Result<T>>) {
        delegate.enqueue(
            object : Callback<T> {

                @Suppress("UNCHECKED_CAST")
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        callback.onResponse(
                            this@ResultCall,
                            Response.success(
                                response.code(),
                                Result.success(value = response.body() ?: Unit as T)
                            )
                        )
                    } else {
                        callback.onResponse(
                            this@ResultCall,
                            Response.success(
                                Result.failure(exception = HttpException(response))
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    callback.onResponse(
                        this@ResultCall,
                        Response.success(Result.failure(exception = t))
                    )
                }
            }
        )
    }

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun execute(): Response<Result<T>> {
        return Response.success(Result.success(delegate.execute().body()!!))
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun clone(): Call<Result<T>> = ResultCall(delegate.clone())

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()
}