package domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class Entity {

    private final String mid;
    private final String id;
    private final String name;
    private final double score;

    public Entity(JSONObject entityJson) throws JSONException {
        mid = entityJson.getString("mid");
        id = entityJson.getString("id");
        name = entityJson.getString("name");
        score = entityJson.getDouble("score");
    }

    protected Entity(Entity entity) {
        mid = entity.getMid();
        id = entity.getId();
        name = entity.getName();
        score = entity.getScore();
    }

    public static List<Entity> buildListFromApiResultJsonString(String content) {
        try {
            JSONObject contentJson = new JSONObject(content);
            JSONArray resultArray = contentJson.getJSONArray("result");

            List<Entity> entityList = new LinkedList<Entity>();
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject result = resultArray.getJSONObject(i);
                entityList.add(new Entity(result));
            }
            return entityList;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMid() {
        return mid;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getScore() {
        return score;
    }

}
