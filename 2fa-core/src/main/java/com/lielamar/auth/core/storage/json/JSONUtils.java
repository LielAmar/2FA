package com.lielamar.auth.core.storage.json;

import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JSONUtils {

    /**
     * Reads an InputStream and creates a JSONObject
     *
     * @param is InputStream to create the JSONObject from
     * @return JSONObject of the InputStream
     * @throws IOException Throws an exception if something goes wrong
     */
    public static JSONObject read(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        StringBuilder fileDataBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            fileDataBuilder.append(line);
            fileDataBuilder.append('\n');
        }

        String fileData = fileDataBuilder.toString();

        if (fileData.length() < 2) {
            fileData = "{}";
        }
        is.close();
        return new JSONObject(fileData);
    }

    /**
     * Writes a JSONObject to and OutputStream
     *
     * @param jsonObject JSONObject to write
     * @param os OutputStream to write the JSONObject to
     * @throws IOException Throws an exception if something goes wrong
     */
    public static void write(JSONObject jsonObject, OutputStream os) throws IOException {
        byte[] bytes = jsonObject.toString().getBytes();
        os.write(bytes);
        os.close();
        os.flush();
    }
}
