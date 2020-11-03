import com.lielamar.auth.database.json.JSONUtils;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.io.*;

public class JSONTest {

    public static void main(String[] args) {
        JSONWriterTest test = new JSONWriterTest();

        System.out.println(test.getSecretKey("LielAmar"));
        System.out.println(test.hasSecretKey("LielAmar"));
        System.out.println(test.setSecretKey("LielAmar", "3462346"));
        System.out.println(test.getSecretKey("LielAmar"));
        test.removeSecretKey("LielAmar");
        System.out.println(test.getSecretKey("LielAmar"));
    }

    public static class JSONWriterTest {

        private File dir;

        public JSONWriterTest() {
            this.setup();
        }

        public boolean setup() {
            this.dir = new File("/auth/");
            return this.dir.exists() || this.dir.mkdir();
        }


        public File getFile(String s) {
            return createFile(s);
        }

        private File createFile(String s) {
            File file = new File(this.dir, s + ".json");

            if(!file.exists()) {
                try {
                    // If we create a new file, load the defaults
                    if(file.createNewFile()) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("secret_key", JSONObject.NULL);
                        JSONUtils.write(jsonObject, new FileOutputStream(file));
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                    return file;
                }
            }
            return file;
        }


        public String setSecretKey(String s, String secretKey) {
            try {
                File file = getFile(s);
                JSONObject jsonObject = JSONUtils.read(new FileInputStream(file));

                if(secretKey == null) jsonObject.put("secret_key", JSONObject.NULL);
                else jsonObject.put("secret_key", secretKey);

                JSONUtils.write(jsonObject, new FileOutputStream(file));
                return secretKey;
            } catch(IOException e) {
                e.printStackTrace();
            }
            return secretKey;
        }

        public String getSecretKey(String s) {
            try {
                File file = getFile(s);
                JSONObject jsonObject = JSONUtils.read(new FileInputStream(file));

                if(!jsonObject.has("secret_key")) return null;

                Object key = jsonObject.get("secret_key");
                if(key == JSONObject.NULL) return null;

                return key.toString();
            } catch(IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public boolean hasSecretKey(String s) {
            return getSecretKey(s) != null;
        }

        public void removeSecretKey(String s) {
            setSecretKey(s, null);
        }
    }
}
