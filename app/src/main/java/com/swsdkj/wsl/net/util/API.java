package com.swsdkj.wsl.net.util;


import com.swsdkj.wsl.base.BaseApplication;
import com.swsdkj.wsl.config.SharedConstants;
import com.swsdkj.wsl.net.BaseUrl;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

/**
 * Created by Anonymous on 2016/11/17.
 */

public class API {

    private static RetrofitAPI retrofitAPI;

    public static RetrofitAPI Retrofit() {
        if (retrofitAPI == null) {

            retrofitAPI = new Retrofit.Builder()
                    .baseUrl(BaseUrl.URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(genericClient())
                    .build()
                    .create(RetrofitAPI.class);
        }
        return retrofitAPI;
    }
    public static OkHttpClient genericClient() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                /*.addHeader("JSESSIONID", BaseApplication.mSharedPrefUtil.getString(SharedConstants.SESSIONID,""))*/
                                .build();
                        return chain.proceed(request);
                    }

                })
                .build();

        return httpClient;
    }
    public interface RetrofitAPI {

        //图片上传
        @Multipart
        @POST("{url}")
        Call<BaseResponse> updateImage(
                @Path("url") String url,
                @PartMap Map<String, RequestBody> params);

    }

}
