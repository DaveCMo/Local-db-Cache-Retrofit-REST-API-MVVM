package com.codingwithmitch.foodrecipes.util;

import android.arch.lifecycle.LiveData;

import com.codingwithmitch.foodrecipes.requests.responses.ApiResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class LiveDataCallAdaptorFactory extends CallAdapter.Factory {

    /**
     * This method performs a number of checks and then returns the Response type for the retrofit requests
     * (@bodyType is the ResponseType. It can be RecipeResponse or RecipeSearchResponse
     * <p>
     * Check #1 returnType returns LiveData
     * Check #2 Type LiveData<T> is of ApiResponse.Class
     * #3 Make sure ApiResponse is parameterised. AKA ApiResponse<T> exists.
     */

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        // Check #1
        // Make sure the calladaptor is returning a type of LiveData

        if (CallAdapter.Factory.getRawType(returnType) != LiveData.class) {
            return null;
        }
        //Check 2
        //Type that LiveData is wrapping (Get <T>

        Type observableType = CallAdapter.Factory.getParameterUpperBound(0, (ParameterizedType) returnType);
        Type rawObservableType = CallAdapter.Factory.getRawType(observableType);
        if (rawObservableType != ApiResponse.class) {
            throw new IllegalArgumentException("Type must be a defined resource");
        }

        // Check #3
        // Check if ApiResponse is parametrised. AKA: Does ApiResponse<T> exists? (must wrap around T)
        //FYI: T is either RecipeResponse or T will be a RecipeSearchResponse

        if (!(observableType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Resource myst be parametrised!");
        }

        Type bodyType = CallAdapter.Factory.getParameterUpperBound(0, (ParameterizedType) observableType);

        return new LiveDataCallAdaptor<>(bodyType);
    }
}
