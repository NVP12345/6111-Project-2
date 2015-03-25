package util;

import domain.Entity;
import domain.EntityProperties;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

public class FreebaseApiUtil {

    private static final String INITIAL_FREEBASE_SEARCH_URL_FORMAT = "https://www.googleapis.com/freebase/v1/search?key=%s&query=%s";
    private static final String INITIAL_FREEBASE_TOPIC_URL_FORMAT = "https://www.googleapis.com/freebase/v1/topic%s?key=%s";

    private static String FREEBASE_API_KEY; // = "AIzaSyBrtC56P8AyVaw9scsNSpYe-r1uJiTiRzE";
    private static String FREEBASE_SEARCH_URL_FORMAT;
    private static String FREEBASE_TOPIC_URL_FORMAT;

    private static final URLCodec URL_CODEC = new URLCodec();

    public static void setFreebaseApiKey(String freebaseApiKey) {
        FREEBASE_API_KEY = freebaseApiKey;
        FREEBASE_SEARCH_URL_FORMAT = String.format(INITIAL_FREEBASE_SEARCH_URL_FORMAT, freebaseApiKey, "%s");
        FREEBASE_TOPIC_URL_FORMAT = String.format(INITIAL_FREEBASE_TOPIC_URL_FORMAT, "%s", freebaseApiKey);
    }

    public static List<Entity> getEntitiesFromQuery(String query) {
        String url = String.format(FREEBASE_SEARCH_URL_FORMAT, tryUrlEncode(query));
        String content = getContentFromApiCall(url);
        return Entity.buildListFromApiResultJsonString(content);
    }

    public static EntityProperties entityPropertiesFromMid(String mid) {
        String url = String.format(FREEBASE_TOPIC_URL_FORMAT, mid);
        String content = getContentFromApiCall(url);
        return new EntityProperties(content);
    }

    private static String tryUrlEncode(String input) {
        try {
            return URL_CODEC.encode(input);
        } catch (EncoderException e) {
            throw new RuntimeException(e);
        }
    }

    // code based on this solution: http://stackoverflow.com/questions/4308554/simplest-way-to-read-json-from-a-url-in-java
    private static String getContentFromApiCall(String urlString) {
        InputStream is = null;
        try {
            is = new URL(urlString).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            return readAll(rd);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

}
