package com.kinnack.dgmt2.service;

import android.util.Log;

import com.kinnack.dgmt2.event.ServerChanged;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by prodrive555 on 4/12/15.
 */
public abstract class HttpService {

    protected String host;
    protected int port;

    public enum ServiceEndpoint {
        RecordSample("/samples"),
        UpdateTags("/samples/%s/%s/tags");

        String mPath;
        ServiceEndpoint(String path) {
            mPath = path;
        }

    }

    public class ParameterizedEndpoint {
        private ServiceEndpoint mUrl;
        private String[] mParams;
        public ParameterizedEndpoint(ServiceEndpoint url, String... params) {
            mUrl = url;
            mParams = params;
        }
        public ParameterizedEndpoint(ServiceEndpoint url) {
            this(url, new String[0]);
        }

        public String asUrlString() {
            return String.format(asUrlString(mUrl), mParams);
        }

        String asUrlString(ServiceEndpoint endpoint) {
            String url = "http://"+host+":"+port+endpoint.mPath;
            url = String.format(url, mParams);
            Log.d("HttpService", url);
            return url;
        }
    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    static OkHttpClient client = new OkHttpClient();

    Response post(ServiceEndpoint url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(new ParameterizedEndpoint(url).asUrlString())
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }

    Response put(ParameterizedEndpoint endpoint, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
                .url(endpoint.asUrlString())
                .put(body)
                .build();
        Response response = client.newCall(request).execute();
        return response;
    }


    public void serverChanged(ServerChanged event) {
        Log.d("HttpService", "Setting server to "+event.getHost()+":"+event.getPort());
        this.host = event.getHost();
        this.port = event.getPort();
        Log.d("HttpService", "Server to "+host+":"+port);
    }

}
