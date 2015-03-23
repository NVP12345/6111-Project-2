package domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntityProperties {

    private static final Map<String, EntityType> FREEBASE_TYPES_TO_ENTITY_TYPES;

    static {
        FREEBASE_TYPES_TO_ENTITY_TYPES = new HashMap<String, EntityType>();
        FREEBASE_TYPES_TO_ENTITY_TYPES.put("/people/person", EntityType.PERSON);
        FREEBASE_TYPES_TO_ENTITY_TYPES.put("/book/author", EntityType.AUTHOR);
        FREEBASE_TYPES_TO_ENTITY_TYPES.put("/film/actor", EntityType.ACTOR);
        FREEBASE_TYPES_TO_ENTITY_TYPES.put("/tv/tv_actor", EntityType.ACTOR);
        FREEBASE_TYPES_TO_ENTITY_TYPES.put("/organization/organization_founder", EntityType.BUSINESS_PERSON);
        FREEBASE_TYPES_TO_ENTITY_TYPES.put("/business/board_member", EntityType.BUSINESS_PERSON);
        FREEBASE_TYPES_TO_ENTITY_TYPES.put("/sports/sports_league", EntityType.LEAGUE);
        FREEBASE_TYPES_TO_ENTITY_TYPES.put("/sports/sports_team", EntityType.SPORTS_TEAM);
        FREEBASE_TYPES_TO_ENTITY_TYPES.put("/sports/professional_sports_team", EntityType.SPORTS_TEAM);
    }

    private final Map<String, JSONObject> propertiesByType;
    private final List<EntityType> entityTypes;

    public EntityProperties(String fullJsonString) {
        try {
            JSONObject fullJson = new JSONObject(fullJsonString);
            propertiesByType = new HashMap<String, JSONObject>();
            JSONObject propertiesJson = fullJson.getJSONObject("property");
            Iterator keys = propertiesJson.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                propertiesByType.put(key, propertiesJson.getJSONObject(key));
            }

            try {
                JSONObject typeProperty = propertiesByType.get("/type/object/type");
                JSONArray typeArray = typeProperty.getJSONArray("values");
                Set<EntityType> uniqueEntityTypes = new HashSet<EntityType>();
                for (int i = 0; i < typeArray.length(); i++) {
                    JSONObject type = typeArray.getJSONObject(i);
                    String typeId = type.getString("id");
                    if (FREEBASE_TYPES_TO_ENTITY_TYPES.containsKey(typeId)) {
                        uniqueEntityTypes.add(FREEBASE_TYPES_TO_ENTITY_TYPES.get(typeId));
                    }
                }
                entityTypes = new LinkedList<EntityType>(uniqueEntityTypes);
                Collections.sort(entityTypes, new Comparator<EntityType>() {
                    @Override
                    public int compare(EntityType o1, EntityType o2) {
                        return o1.ordinal() - o2.ordinal();
                    }
                });
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public List<EntityType> getEntityTypes() {
        return entityTypes;
    }

    public String getName() {
        return getSimpleText("/type/object/name");
    }

    public String getBirthday() {
        return getSimpleText("/people/person/date_of_birth");
    }

    public String getPlaceOfBirth() {
        return getSimpleText("/people/person/place_of_birth");
    }

    private String getSimpleText(String type) {
        try {
            JSONObject property = propertiesByType.get(type);
            JSONArray valueArray = property.getJSONArray("values");
            JSONObject firstValue = valueArray.getJSONObject(0);
            return firstValue.getString("text");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
