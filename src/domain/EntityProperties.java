package domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import util.FreebaseApiUtil;

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
        return getSimpleProperty(type, "text");
    }

    public String getDocumentText() {
        return getSimpleValue("/common/document/text");
    }

    private String getSimpleValue(String type) {
        return getSimpleProperty(type, "value");
    }

    private String getSimpleProperty(String type, String propertyName) {
        return getSimplePropertyList(type, propertyName).get(0);
    }

    private List<String> getSimplePropertyList(String type, String propertyName) {
        try {
            JSONObject property = propertiesByType.get(type);
            if (property == null) {
                return null;
            }
            JSONArray valueArray = property.getJSONArray("values");
            List<String> values = new LinkedList<String>();
            for (int i = 0; i < valueArray.length(); i++) {
                JSONObject value = valueArray.getJSONObject(i);
                values.add(value.getString(propertyName));
            }
            return values;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getDescriptions() {
        JSONObject articleProperty = propertiesByType.get("/common/topic/article");
        try {
            JSONArray valueArray = articleProperty.getJSONArray("values");
            List<String> descriptions = new LinkedList<String>();
            for (int i = 0; i < valueArray.length(); i++) {
                JSONObject value = valueArray.getJSONObject(i);
                EntityProperties articleEntityProperties = FreebaseApiUtil.entityPropertiesFromMid(value.getString("id"));
                descriptions.add(articleEntityProperties.getDocumentText());
            }
            return descriptions;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getSiblings() {
        List<String> siblings = new LinkedList<String>();
        JSONObject siblingsProperty = propertiesByType.get("/people/person/sibling_s");
        try {
            JSONArray valueArray = siblingsProperty.getJSONArray("values");
            for (int i = 0; i < valueArray.length(); i++) {
                JSONObject value = valueArray.getJSONObject(i);
                JSONObject properties = value.getJSONObject("property");
                JSONObject siblingProperty = properties.getJSONObject("/people/sibling_relationship/sibling");
                JSONArray siblingValueArray = siblingProperty.getJSONArray("values");
                JSONObject siblingValue = siblingValueArray.getJSONObject(0);
                siblings.add(siblingValue.getString("text"));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return siblings;
    }

    public List<String> getSpouses() {
        List<String> spouses = new LinkedList<String>();
        JSONObject spousesProperty = propertiesByType.get("/people/person/spouse_s");
        try {
            JSONArray valueArray = spousesProperty.getJSONArray("values");
            for (int i = 0; i < valueArray.length(); i++) {
                JSONObject value = valueArray.getJSONObject(i);
                JSONObject properties = value.getJSONObject("property");

                StringBuilder sb = new StringBuilder();
                JSONObject spouseProperty = properties.getJSONObject("/people/marriage/spouse");
                JSONArray spouseValueArray = spouseProperty.getJSONArray("values");
                JSONObject spouseValue = spouseValueArray.getJSONObject(0);
                sb.append(spouseValue.getString("text"));

                sb.append(" ");

                JSONObject spouseFromProperty = properties.getJSONObject("/people/marriage/from");
                JSONArray spouseFromValueArray = spouseFromProperty.getJSONArray("values");
                JSONObject spouseFromValue = spouseFromValueArray.getJSONObject(0);
                sb.append("(");
                sb.append(spouseFromValue.getString("text"));

                sb.append(" - ");

                JSONObject spouseToProperty = properties.getJSONObject("/people/marriage/to");
                JSONArray spouseToValueArray = spouseToProperty.getJSONArray("values");
                String spouseToDate = "now";
                if (spouseToValueArray.length() > 0) {
                    spouseToDate = spouseToValueArray.getJSONObject(0).getString("text");
                }
                sb.append(spouseToDate);
                sb.append(")");

                JSONObject spouseCeremonyProperty = properties.getJSONObject("/people/marriage/location_of_ceremony");
                JSONArray spouseCeremonyValueArray = spouseCeremonyProperty.getJSONArray("values");
                if (spouseCeremonyValueArray.length() > 0) {
                    JSONObject spouseCeremonyValue = spouseCeremonyValueArray.getJSONObject(0);
                    sb.append(" @ ");
                    sb.append(spouseCeremonyValue.getString("text"));
                }

                spouses.add(sb.toString());
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return spouses;
    }

    public String getDeathInfo() {
        String placeOfDeath = getSimpleText("/people/deceased_person/place_of_death");
        if (placeOfDeath == null) {
            return null;
        }
        String dateOfDeath = getSimpleText("/people/deceased_person/date_of_death");
        List<String> causesOfDeath = getSimplePropertyList("/people/deceased_person/cause_of_death", "text");

        String causeOfDeath;
        if (causesOfDeath.size() == 1) {
            causeOfDeath = causesOfDeath.get(0);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            Iterator<String> it = causesOfDeath.iterator();
            while (it.hasNext()) {
                sb.append(it.next());
                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append(")");
            causeOfDeath = sb.toString();
        }

        return String.format("%s at %s, cause: %s", dateOfDeath, placeOfDeath, causeOfDeath);
    }
}
