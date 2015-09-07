package com.kinnack.dgmt2.service;

import android.util.Log;

import com.kinnack.dgmt2.event.ServerChanged;
import com.kinnack.dgmt2.model.Sample;
import com.kinnack.dgmt2.option.Either;
import com.kinnack.dgmt2.option.Left;
import com.kinnack.dgmt2.option.Right;
import com.google.gson.Gson;
import com.squareup.okhttp.Response;
import com.squareup.otto.Subscribe;

import java.io.IOException;

import static com.kinnack.dgmt2.service.HttpService.ServiceEndpoint.RecordSample;
import static com.kinnack.dgmt2.service.HttpService.ServiceEndpoint.UpdateTags;

public class SampleHttpService extends HttpService {

    public Either<IOException, Sample> recordSample(Sample sample) {

        String json = new Gson().toJson(sample);
        Log.d("SampleHttpService", "Sending: " + json);
        try {
            Response response = post(RecordSample, json);
            Log.d("SampleHttpService", "Reponse: " + response.code());
            if (response.code() - 200 < 100) {
                return new Right(sample);
            }
            return new Left(new IOException("Request was not successful"));
        } catch (IOException ioe) {
            return new Left(ioe);
        }
    }

    public Either<IOException, Sample> updateTags(Sample sample) {
        String json = new Gson().toJson(sample.getTags());
        Log.d("SampleHttpService", "PUTting: " + json);

        try {
            Response response = put(new ParameterizedEndpoint(UpdateTags, sample.getUserId(), sample.getWhen()+""), json);
            Log.d("SampleHttpService", "Reponse: " + response.code());
            if (response.code() - 200 < 100) {
                return new Right(sample);
            }
            return new Left(new IOException("Request was not successful"));
        } catch (IOException ioe) {
            return new Left(ioe);
        }
    }

    //This has to be here because @Subscribe doesnt work on super classes
    @Subscribe
    public void serverChanged(ServerChanged event) {
        super.serverChanged(event);
    }

}
