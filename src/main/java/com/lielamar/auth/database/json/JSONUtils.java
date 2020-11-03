package com.lielamar.auth.database.json;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JSONUtils {

    public static JSONObject read(InputStream is) throws IOException {
        String fileData = IOUtils.toString(is);
        return new JSONObject(fileData);
    }

    public static void write(JSONObject jsonObject, OutputStream os) throws IOException {
        byte[] bytes = jsonObject.toString().getBytes();
        os.write(bytes);
        os.close();
    }

}
