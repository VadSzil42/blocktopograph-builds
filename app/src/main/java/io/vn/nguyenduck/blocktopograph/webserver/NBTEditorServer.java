package io.vn.nguyenduck.blocktopograph.webserver;

import static io.vn.nguyenduck.blocktopograph.Constants.BOGGER;

import android.content.Context;

import java.io.IOException;
import java.net.BindException;
import java.net.URLConnection;

import fi.iki.elonen.NanoHTTPD;

public class NBTEditorServer extends NanoHTTPD {

    private final Context context;

    public NBTEditorServer(Context context) {
        super(4723);
        this.context = context;
        try {
            start();
        } catch (BindException ignored) {
        } catch (IOException e) {
            BOGGER.severe("Failed to start server");
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (uri.equals("/")) uri += "index.html";
        try {
            var is = context.getAssets().open("monaco" + uri);
            var type = URLConnection.guessContentTypeFromName(uri);
            Response response = NanoHTTPD.newFixedLengthResponse(Response.Status.OK, type, is, is.available());

            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "GET");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type");

            return response;
        } catch (IOException ignored) {
        }
        BOGGER.info("Failed to serve " + uri);
        return super.serve(session);
    }
}