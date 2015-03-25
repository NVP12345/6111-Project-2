package domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
        List<String> properties = getSimplePropertyList(type, propertyName);
        if (properties == null) {
            return null;
        }
        return properties.get(0);
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
            return null;
        }
    }

    public List<String> getDescriptions() {
        return getSimplePropertyList("/common/topic/description", "value");
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
            return null;
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
            return null;
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

    public List<String> getBooks() {
        return getSimplePropertyList("/book/author/works_written", "text");
    }

    public List<String> getInfluencedBy() {
        return getSimplePropertyList("/influence/influence_node/influenced_by", "text");
    }

    public List<String> getBooksAbout() {
        return getSimplePropertyList("/book/book_subject/works", "text");
    }

    public List<String> getInfluenced() {
        return getSimplePropertyList("/influence/influence_node/influenced", "text");
    }

    public List<List<String>> getFilms() {
        return getNestedPropertyLists("/film/actor/film", "/film/performance/character", "/film/performance/film");
    }

    public List<List<String>> getNestedPropertyLists(String initialPropertyType, String... nestedProperties) {
        JSONObject initialProperty = propertiesByType.get(initialPropertyType);
        if (initialProperty == null) {
            return null;
        }
        try {
            JSONArray initialValues = initialProperty.getJSONArray("values");
            List<List<String>> nestedPropertyLists = new ArrayList<List<String>>();
            for (int i = 0; i < initialValues.length(); i++) {
                JSONObject property = initialValues.getJSONObject(i).getJSONObject("property");
                List<String> nestedPropertyList = new ArrayList<String>();
                for (String nestedPropertyKey : nestedProperties) {
                    JSONObject nestedProperty = property.optJSONObject(nestedPropertyKey);
                    if (nestedProperty == null) {
                        nestedPropertyList.add("");
                    } else {
                        JSONArray nestedPropertyValues = nestedProperty.getJSONArray("values");
                        if (nestedPropertyValues.length() == 0) {
                            nestedPropertyList.add("");
                        } else {
                            nestedPropertyList.add(
                                    nestedPropertyValues
                                            .getJSONObject(0)
                                            .getString("text")
                            );
                        }
                    }
                }
                nestedPropertyLists.add(nestedPropertyList);
            }
            return nestedPropertyLists;
        } catch (JSONException e) {
            return null;
        }
    }

    public List<String> getOrganizationsFounded() {
        return getSimplePropertyList("/organization/organization_founder/organizations_founded", "text");
    }

    public List<List<String>> getLeadershipRoles() {
        return getNestedPropertyListsWithFromToConversions(
                "/business/board_member/leader_of",
                "/organization/leadership/organization",
                "/organization/leadership/role",
                "/organization/leadership/title",
                "/organization/leadership/from",
                "/organization/leadership/to"
        );
    }

    public List<List<String>> getBoardMemberships() {
        return getNestedPropertyListsWithFromToConversions(
                "/business/board_member/organization_board_memberships",
                "/organization/organization_board_membership/organization",
                "/organization/organization_board_membership/role",
                "/organization/organization_board_membership/title",
                "/organization/organization_board_membership/from",
                "/organization/organization_board_membership/to"
        );
    }

    private List<List<String>> getNestedPropertyListsWithFromToConversions(String initialPropertyType, String... nestedProperties) {
        List<List<String>> nestedLists = getNestedPropertyLists(initialPropertyType, nestedProperties);
        convertLastTwoColumnsToFromToDates(nestedLists);
        return nestedLists;
    }

    private void convertLastTwoColumnsToFromToDates(List<List<String>> input) {
        if (input == null) {
            return;
        }

        for (List<String> row : input) {
            int toDateIndex = row.size() - 1;
            int fromDateIndex = toDateIndex - 1;
            String fromDate = row.get(fromDateIndex);
            String toDate = row.get(toDateIndex);

            if (toDate.isEmpty()) {
                toDate = "now";
            }

            row.remove(toDateIndex);
            row.remove(fromDateIndex);
            row.add(String.format("%s / %s", fromDate, toDate));
        }
    }

    public String getLeagueSport() {
        return getSimpleText("/sports/sports_league/sport");
    }

    public String getOfficialWebsite() {
        return getSimpleValue("/common/topic/official_website");
    }

    public String getChampionship() {
        return getSimpleText("/sports/sports_league/championship");
    }

    public List<String> getTeams() {
        return getExtractedNestedPropertyList("/sports/sports_league/teams", "/sports/sports_league_participation/team");
    }

    public List<String> getExtractedNestedPropertyList(String initialPropertyType, String... nestedProperties) {
        List<List<String>> nestedLists = getNestedPropertyLists(initialPropertyType, nestedProperties);
        if (nestedLists == null) {
            return null;
        }
        List<String> list = new LinkedList<String>();
        for (List<String> nestedList : nestedLists) {
            list.add(nestedList.get(0));
        }
        return list;
    }


    public String getTeamSport() {
        return getSimpleText("/sports/sports_team/sport");
    }

    public List<String> getTeamArena() {
        return getExtractedNestedPropertyList("/sports/sports_team/venue", "/sports/team_venue_relationship/venue");
    }

    public List<String> getTeamChampionShips() {
        return getSimplePropertyList("/sports/sports_team/championships", "text");
    }

    public String getTeamFounded() {
        return getSimpleValue("/sports/sports_team/founded");
    }

    public List<String> getTeamLeagues() {
        return getExtractedNestedPropertyList("/sports/sports_team/league", "/sports/sports_league_participation/league");
    }

    public List<String> getTeamLocations() {
        return getSimplePropertyList("/sports/sports_team/location", "text");
    }

    public List<List<String>> getTeamCoaches() {
        return getNestedPropertyListsWithFromToConversions(
                "/sports/sports_team/coaches",
                "/sports/sports_team_coach_tenure/coach",
                "/sports/sports_team_coach_tenure/position",
                "/sports/sports_team_coach_tenure/from",
                "/sports/sports_team_coach_tenure/to"
        );
    }

    public List<List<String>> getTeamPlayers() {
        return getNestedPropertyListsWithFromToConversions(
                "/sports/sports_team/roster",
                "/sports/sports_team_roster/player",
                "/sports/sports_team_roster/position",
                "/sports/sports_team_roster/number",
                "/sports/sports_team_roster/from",
                "/sports/sports_team_roster/to"
        );
    }
}
