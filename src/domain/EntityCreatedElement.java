package domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EntityCreatedElement implements Comparable<EntityCreatedElement> {

    private final String name;
    private final String type;
    private final List<String> createdItemList = new ArrayList<String>();

    private EntityCreatedElement(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public static List<EntityCreatedElement> buildListFromApiResultJsonStringForAuthor(String content) {
        return buildListFromApiResultJsonStringForTypeAndNode(content, "Author", "/book/author/works_written");
    }

    public static List<EntityCreatedElement> buildListFromApiResultJsonStringForBusinessPerson(String content) {
        return buildListFromApiResultJsonStringForTypeAndNode(content, "Businessperson", "/organization/organization_founder/organizations_founded");
    }

    private static List<EntityCreatedElement> buildListFromApiResultJsonStringForTypeAndNode(String content, String type, String node) {
        try {
            JSONObject contentJson = new JSONObject(content);
            JSONArray resultArray = contentJson.getJSONArray("result");

            List<EntityCreatedElement> elements = new LinkedList<EntityCreatedElement>();
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject result = resultArray.getJSONObject(i);
                EntityCreatedElement element = new EntityCreatedElement(result.getString("name"), type);
                JSONArray books = result.getJSONArray(node);
                for (int j = 0; j < books.length(); j++) {
                    element.addCreatedItem(books.getJSONObject(j).getString("a:name"));
                }
                elements.add(element);
            }
            return elements;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void addCreatedItem(String createdItem) {
        createdItemList.add(createdItem);
    }

    @Override
    public int compareTo(EntityCreatedElement other) {
        int nameCompare = name.compareTo(other.name);
        if (nameCompare != 0) {
            return nameCompare;
        }
        return type.compareTo(other.type);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < createdItemList.size(); i++) {
            String createdItem = createdItemList.get(i);
            sb.append(String.format("<%s>", createdItem));
            if (i < createdItemList.size() - 2) {
                sb.append(String.format(", "));
            } else if (i == createdItemList.size() - 2) {
                if (createdItemList.size() == 2) {
                    sb.append(String.format(" and "));
                } else {
                    sb.append(String.format(", and "));
                }
            }
        }
        return String.format("%s (as %s) created %s", name, type, sb.toString());
    }

}
