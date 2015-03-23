package util;

import domain.Entity;
import domain.EntityProperties;
import domain.EntityType;
import domain.infobox.InfoBox;
import domain.infobox.TitleInfoBoxRegion;
import domain.infobox.SimpleTextInfoBoxRegion;

import java.util.List;

public class InfoboxGeneratorUtil {

    public static void generateInfobox(String freebaseApiKey, String query) {
        System.out.format("generateInfobox with key %s and query '%s'\n", freebaseApiKey, query);

        List<Entity> entities = FreebaseApiUtil.getEntitiesFromQuery(query);
        printInfoBoxForFirstValidEntity(entities);
    }

    private static void printInfoBoxForFirstValidEntity(List<Entity> entities) {
        for (Entity entity : entities) {
            EntityProperties entityProperties = FreebaseApiUtil.entityPropertiesFromMid(entity.getMid());
            List<EntityType> entityTypes = entityProperties.getEntityTypes();
            if (!entityTypes.isEmpty()) {
                printInfoBoxForEntity(entityProperties);
                System.exit(0);
            }
        }

        System.out.println("No valid entities were found for the given query. Terminating.");
        System.exit(1);
    }

    private static void printInfoBoxForEntity(EntityProperties entityProperties) {
        InfoBox infoBox = new InfoBox();
        addTitleAndNameToInfoBox(infoBox, entityProperties);
        List<EntityType> entityTypes = entityProperties.getEntityTypes();
        for (EntityType entityType : entityTypes) {
            switch (entityType) {
                case PERSON:
                    addPersonRegionsToInfoBox(infoBox, entityProperties);
            }
        }
        System.out.print(infoBox);
    }

    private static void addTitleAndNameToInfoBox(InfoBox infoBox, EntityProperties entityProperties) {
        String name = entityProperties.getName();
        infoBox.addRegion(new TitleInfoBoxRegion(name, entityProperties.getEntityTypes()));
        infoBox.addRegion(new SimpleTextInfoBoxRegion("Name", name));
    }

    private static void addPersonRegionsToInfoBox(InfoBox infoBox, EntityProperties entityProperties) {
        infoBox.addRegion(new SimpleTextInfoBoxRegion("Birthday", entityProperties.getBirthday()));
        infoBox.addRegion(new SimpleTextInfoBoxRegion("Place of birth", entityProperties.getPlaceOfBirth()));
    }

}
