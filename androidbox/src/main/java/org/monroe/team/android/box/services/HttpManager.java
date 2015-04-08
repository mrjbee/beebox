package org.monroe.team.android.box.services;

import org.json.JSONException;
import org.monroe.team.android.box.json.Json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpManager {

    public <BodyType> Response<BodyType> get(String url, ConnectionDetails details, ResponseBuilder<BodyType> builder) throws BadUrlException, NoRouteToHostException, InvalidBodyFormat, IOException {
        URL requestUrl = createURL(url);
        HttpURLConnection connection = prepareConnection(details, requestUrl);
        try {
            connection.connect();
            return builder.build(connection);
         } finally {
            connection.disconnect();
        }
    }

    public static ConnectionDetails details(){
        return new ConnectionDetails();
    }

    private boolean isRedirected(URL requestUrl, HttpURLConnection connection) {
        return !requestUrl.getHost().equals(connection.getURL().getHost());
    }

    private HttpURLConnection prepareConnection(ConnectionDetails details, URL requestUrl) throws NoRouteToHostException {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) requestUrl.openConnection();
        } catch (IOException e) {
            throw new NoRouteToHostException(e);
        }
        details.apply(connection);
        return connection;
    }


    private URL createURL(String url) throws BadUrlException {
        URL urlObject = null;
        try {
            urlObject = new URL(url);
        } catch (MalformedURLException e) {
            throw new BadUrlException(e);
        }
        return urlObject;
    }

    public static ResponseWithHeadersBuilder<Json> json() {
        return new ResponseWithHeadersBuilder<Json>() {
            @Override
            protected Json readBody(HttpURLConnection connection) throws IOException, InvalidBodyFormat {
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    try {
                        return Json.createFromString(sb.toString());
                    } catch (JSONException e) {
                        throw new InvalidBodyFormat(e);
                    }
                }finally {
                    if (br != null){
                        try {
                            br.close();
                        } catch (IOException e) {}
                    }
                }
           }
        };
    }

    public static class Response<Body> {

        public final Body body;
        public final int statusCode;
        public final String statusMessage;
        public final Map<String,String> headers;

        public Response(Body body, int statusCode, String statusMessage, Map<String, String> headers) {
            this.body = body;
            this.statusCode = statusCode;
            this.statusMessage = statusMessage;
            this.headers = Collections.unmodifiableMap(headers);
        }
    }

    public static interface ResponseBuilder<BodyType> {
        <BodyType> Response<BodyType> build(HttpURLConnection connection) throws IOException, InvalidBodyFormat;
    }

    public static abstract class ResponseWithHeadersBuilder<BodyType> implements ResponseBuilder<BodyType> {

        private List<String> requestedHeaders = new ArrayList<String>(1);

        @Override
        public Response<BodyType> build(HttpURLConnection connection) throws IOException, InvalidBodyFormat {
            int statusCode = connection.getResponseCode();
            String statusMessage = connection.getResponseMessage();
            Map<String,String> headers = new HashMap<>();
            for (String requestedHeader : requestedHeaders) {
                String value = connection.getHeaderField(requestedHeader);
                headers.put(requestedHeader,value);
            }
            BodyType body = readBody(connection);
            return new Response<>(body,statusCode,statusMessage,headers);
        }

        protected abstract BodyType readBody(HttpURLConnection connection) throws IOException, InvalidBodyFormat;

        public ResponseWithHeadersBuilder<BodyType> requestHeader(String headerName){
            requestedHeaders.add(headerName);
            return this;
        }
    }

    public static class ConnectionDetails{

        private int timeout = 1000;
        private int readTimeout = 50000;

        private void apply(HttpURLConnection connection) {
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(readTimeout);
        }

        public ConnectionDetails timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public ConnectionDetails readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }
    }

    public static class BadUrlException extends Exception {
        public BadUrlException(Throwable throwable) {
            super(throwable);
        }
    }

    public static class NoRouteToHostException extends Exception{
        public NoRouteToHostException(Throwable throwable) {
            super(throwable);
        }
    }

    public static class InvalidBodyFormat extends Exception{
        public InvalidBodyFormat(Throwable throwable) {
            super(throwable);
        }
    }
}
