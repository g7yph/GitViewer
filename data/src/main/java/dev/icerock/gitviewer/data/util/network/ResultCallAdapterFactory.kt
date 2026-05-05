package dev.icerock.gitviewer.data.util.network

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class ResultCallAdapterFactory private constructor() : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) return null
        check(returnType is ParameterizedType) {
            "Call return type must be parameterized as Call<T> or Call<out T>"
        }

        val resultType = getParameterUpperBound(0, returnType)
        if (getRawType(resultType) != Result::class.java) return null
        check(resultType is ParameterizedType) {
            "Result must be parameterized as Result<T> or Result<out T>"
        }

        val responseType = getParameterUpperBound(0, resultType)
        return ResultCallAdapter<Any>(responseType = responseType)
    }

    companion object {
        @JvmStatic
        fun create() = ResultCallAdapterFactory()
    }
}