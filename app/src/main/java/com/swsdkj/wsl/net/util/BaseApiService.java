package com.swsdkj.wsl.net.util;

import com.swsdkj.wsl.net.BaseUrl;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Ｔａｍｉｃ on 2016-07-08.
 * {@link # https://github.com/NeglectedByBoss/RetrofitClient}
 */
public interface BaseApiService {

    public static final String Base_URL = BaseUrl.URL;
    @GET("{url}")
    Observable<BaseResponse> executeGet(
            @Path("url") String url,
            @QueryMap Map<String, String> maps
    );
    @POST("{url}")
    Observable<BaseResponse> executePost(@Path("url") String url, @Body Object postQueryInfo);

    @Headers("content-type:application/json;charset=UTF-8")
    @POST
    Observable<BaseResponse> executePost2(@Url String url, @Body Map<String, String> map);

    @Multipart
    @POST("{url}")
    Observable<BaseResponse> uploadFile(@Path("url") String url,
                                        @PartMap Map<String, RequestBody> params);
    @Multipart
    @POST("{url}")
    Observable<ResponseBody> uploadFile2(
            @Path("url") String url,
            @QueryMap Map<String, String> usermaps,
            @PartMap Map<String, RequestBody> params
    );


    @Multipart
    @POST
    Observable<ResponseBody> uploadFileWithPartMap(
            @Url() String url,
            @PartMap() Map<String, RequestBody> partMap,
            @Part("file") MultipartBody.Part file);
    @Multipart
    @POST("http://api.stay4it.com/v1/public/core/?service=user.updateAvatar")
    Observable<ResponseBody> upLoadFile(
            @Part("file") String des,
            @Part("image\"; filename=\"image.jpg") RequestBody requestBody);


    @Multipart
    @POST("http://api.stay4it.com/v1/public/core/?service=user.updateAvatar")
    Observable<ResponseBody> uploadFilePhoto(@Part("file") String des,
                                             @PartMap Map<String, RequestBody> params);

  /*  @POST("{url}")
    Call<ResponseBody> uploadFiles(
            @Path("url") String url,
            @Path("headers") Map<String, String> headers,
            @Part("filename") String description,
            @PartMap() Map<String, RequestBody> maps);

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);
*/
}
