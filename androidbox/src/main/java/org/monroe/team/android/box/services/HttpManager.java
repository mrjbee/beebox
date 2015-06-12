package org.monroe.team.android.box.services;

import org.json.JSONException;
import org.monroe.team.android.box.json.Json;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpManager {

    public <BodyType> Response<BodyType> get(String url, ConnectionDetails details, ResponseBuilder<BodyType> responseBuilder) throws BadUrlException, NoRouteToHostException, InvalidBodyFormatException, IOException {
        URL requestUrl = createURL(url);
        HttpURLConnection connection = prepareConnection(details, requestUrl);
        try {
            InputStream inputStream = null;
            try {
                inputStream = connection.getInputStream();
            } catch (Exception e) {
                closeStream(inputStream);
                throw e;
            }
            Response<BodyType> response = null;
            try {
                response = responseBuilder.build(inputStream, connection);
            }catch (Exception e){
                closeStream(inputStream);
                throw e;
            }

            final InputStream finalInputStream = inputStream;
            response.closeOperation(new Runnable() {
                @Override
                public void run() {
                    closeStream(finalInputStream);
                }
            });

            if (!response.isLazyReleaseRequired()) {
                response.release();
            }
            return response;
         } finally {
            if (details.disconnect()){
                connection.disconnect();
            }
        }
    }

    public <BodyType> Response<BodyType> post(String url, RequestBuilder requestBuilder, ConnectionDetails details, ResponseBuilder<BodyType> responseBuilder)
            throws BadUrlException, NoRouteToHostException, InvalidBodyFormatException, IOException {
        URL requestUrl = createURL(url);
        HttpURLConnection connection = prepareConnection(details, requestUrl);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        requestBuilder.setRequestProperties(connection);
        try {
            final OutputStream outputStream = openOutputStream(connection);
            buildRequest(requestBuilder, connection, outputStream);
            InputStream inputStream = null;
            try {
                inputStream = connection.getInputStream();
            } catch (FileNotFoundException e){
                inputStream = null;
            } catch (Exception e) {
                closeStream(outputStream);
                closeStream(inputStream);
                throw e;
            }
            Response<BodyType> response = null;
            try {
                response = responseBuilder.build(inputStream, connection);
            }catch (Exception e){
                closeStream(outputStream);
                closeStream(inputStream);
                throw e;
            }

            final InputStream finalInputStream = inputStream;
            response.closeOperation(new Runnable() {
                @Override
                public void run() {
                    closeStream(finalInputStream);
                    closeStream(outputStream);
                }
            });

            if (!response.isLazyReleaseRequired()) {
                response.release();
            }

            return response;
        } finally {
            if (details.disconnect) {
                connection.disconnect();
            }
        }
    }

    private void buildRequest(RequestBuilder requestBuilder, HttpURLConnection connection, OutputStream stream) throws IOException {
        try {
            requestBuilder.buildRequest(stream,connection);
        }catch (Exception e){
            closeStream(stream);
            throw e;
        }
    }

    private void closeStream(Closeable stream) {
        try{if (stream!=null) stream.close();}catch (Exception howCare){}
    }

    private OutputStream openOutputStream(HttpURLConnection connection) throws IOException {
        OutputStream stream = null;
        try {
            stream = connection.getOutputStream();
        } catch (IOException e){
            if (stream != null){
                closeStream(stream);
            }
            throw e;
        }
        return stream;
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

    public static RequestWithHeadersBuilder request_json(Json json){
        String jsonString = json.toJsonString();
        StringRequestBuilder builder =new StringRequestBuilder(jsonString);
        builder.header("content-type","application/json");
        return builder;
    }

    public static ResponseWithHeadersBuilder<Json> response_json() {
        return new ResponseWithHeadersBuilder<Json>() {
            @Override
            protected Json readBody(InputStream input) throws IOException, InvalidBodyFormatException {
                if (input == null)return null;
                BufferedReader br = null;
                br = new BufferedReader(new InputStreamReader(input));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                try {
                    return Json.createFromString(sb.toString());
                } catch (JSONException e) {
                    throw new InvalidBodyFormatException(e);
                }
           }

            @Override
            protected boolean readBodyCompletely() {
                return true;
            }
        };
    }

    public static class Response<Body> {

        public final Body body;
        public final int statusCode;
        public final String statusMessage;
        public final Map<String,String> headers;
        private Runnable closeOperation;
        private final boolean lazyReleaseRequired;

        public Response(Body body, int statusCode, String statusMessage, Map<String, String> headers, boolean lazyReleaseRequired) {
            this.body = body;
            this.statusCode = statusCode;
            this.statusMessage = statusMessage;
            this.lazyReleaseRequired = lazyReleaseRequired;
            this.headers = Collections.unmodifiableMap(headers);
        }

        void closeOperation(Runnable runnable) {
            closeOperation = runnable;
        }

        boolean isLazyReleaseRequired() {
            return lazyReleaseRequired;
        }

        public synchronized void release(){
            if (closeOperation != null){
                closeOperation.run();
                closeOperation = null;
            }
        }


    }

    public static interface RequestBuilder {
        public void setRequestProperties(HttpURLConnection connection);
        public void buildRequest(OutputStream stream, HttpURLConnection connection) throws IOException;
    }

    public static abstract class RequestWithHeadersBuilder implements RequestBuilder {

        private final Map<String,String> headers = new HashMap<>();

        public RequestWithHeadersBuilder header(String name, String value){
            headers.put(name, value);
            return this;
        }


        @Override
        public void setRequestProperties(HttpURLConnection connection) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                connection.setRequestProperty(headerEntry.getKey(), headerEntry.getValue());
            }
        }

        @Override
        public void buildRequest(OutputStream stream, HttpURLConnection connection) throws IOException {
            dealsWithRequest(stream);
        }

        protected abstract void dealsWithRequest(OutputStream stream) throws IOException;
    }

    public static class StringRequestBuilder extends RequestWithHeadersBuilder{

        private final String body;

        public StringRequestBuilder(String body) {
            this.body = body;
        }

        @Override
        protected void dealsWithRequest(OutputStream stream) throws IOException {
            OutputStreamWriter wr= new OutputStreamWriter(stream);
            wr.write(body);
            wr.flush();
        }
    }

    public static interface ResponseBuilder<BodyType> {
        <BodyType> Response<BodyType> build(InputStream input, HttpURLConnection connection) throws IOException, InvalidBodyFormatException;
    }

    public static abstract class ResponseWithHeadersBuilder<BodyType> implements ResponseBuilder<BodyType> {

        private List<String> requestedHeaders = new ArrayList<String>(1);

        @Override
        public Response<BodyType> build(InputStream input, HttpURLConnection connection) throws IOException, InvalidBodyFormatException {
            int statusCode = connection.getResponseCode();
            String statusMessage = connection.getResponseMessage();
            Map<String,String> headers = new HashMap<>();
            for (String requestedHeader : requestedHeaders) {
                String value = connection.getHeaderField(requestedHeader);
                headers.put(requestedHeader,value);
            }
            BodyType body = readBody(input);
            return new Response<>(body,statusCode,statusMessage,headers, !readBodyCompletely());
        }

        protected boolean readBodyCompletely() {
            return false;
        }

        protected abstract BodyType readBody(InputStream input) throws IOException, InvalidBodyFormatException;

        public ResponseWithHeadersBuilder<BodyType> requestHeader(String headerName){
            requestedHeaders.add(headerName);
            return this;
        }
    }

    public static class ConnectionDetails{

        private int timeout = 5000;
        private int readTimeout = 50000;
        private boolean disconnect = false;

        private void apply(HttpURLConnection connection) {
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(readTimeout);
        }

        public ConnectionDetails disconnect(boolean disconnect) {
            this.disconnect = disconnect;
            return this;
        }

        public ConnectionDetails timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public ConnectionDetails readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        boolean disconnect() {
            return disconnect;
        }
    }

    public static class BadUrlException extends RuntimeException {
        public BadUrlException(Throwable throwable) {
            super(throwable);
        }
    }

    public static class NoRouteToHostException extends IOException{
        public NoRouteToHostException(Throwable throwable) {
            super(throwable);
        }
    }

    public static class InvalidBodyFormatException extends Exception{
        public InvalidBodyFormatException(Throwable throwable) {
            super(throwable);
        }
    }
}
