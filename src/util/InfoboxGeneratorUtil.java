package util;

import domain.Entity;
import domain.EntityProperties;
import domain.EntityType;
import domain.infobox.ColumnInfoBoxRegion;
import domain.infobox.InfoBox;
import domain.infobox.TitleInfoBoxRegion;
import domain.infobox.SimpleTextInfoBoxRegion;
import domain.infobox.WrappingTextInfoBoxRegion;

import java.util.List;

public class InfoboxGeneratorUtil {

    public static void generateInfobox(String query) {
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
                    break;
                case AUTHOR:
                    addAuthorRegionsToInfoBox(infoBox, entityProperties);
                    break;
                case ACTOR:
                    addActorRegionsToInfoBox(infoBox, entityProperties);
                    break;
                case BUSINESS_PERSON:
                    addBusinessPersonRegionsToInfoBox(infoBox, entityProperties);
                    break;
                case LEAGUE:
                    addLeagueRegionsToInfoBox(infoBox, entityProperties);
                    break;
                case SPORTS_TEAM:
                    addSportsTeamRegionsToInfoBox(infoBox, entityProperties);
                    break;
                default:
                    throw new RuntimeException("Unsupported entity type: " + entityType.name());
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

        String deathInfo = entityProperties.getDeathInfo();
        if (deathInfo != null) {
            infoBox.addRegion(new SimpleTextInfoBoxRegion("Death", deathInfo));
        }

        infoBox.addRegion(new SimpleTextInfoBoxRegion("Place of birth", entityProperties.getPlaceOfBirth()));
        infoBox.addRegion(new WrappingTextInfoBoxRegion("Descriptions", entityProperties.getDescriptions()));
        infoBox.addRegion(new SimpleTextInfoBoxRegion("Siblings", entityProperties.getSiblings()));
        infoBox.addRegion(new SimpleTextInfoBoxRegion("Spouses", entityProperties.getSpouses()));
    }

    private static void addAuthorRegionsToInfoBox(InfoBox infoBox, EntityProperties entityProperties) {
        infoBox.addRegion(new SimpleTextInfoBoxRegion("Books", entityProperties.getBooks()));
        infoBox.addRegion(new SimpleTextInfoBoxRegion("Influenced By", entityProperties.getInfluencedBy()));
        infoBox.addRegion(new SimpleTextInfoBoxRegion("Books about", entityProperties.getBooksAbout()));
        infoBox.addRegion(new SimpleTextInfoBoxRegion("Influenced", entityProperties.getInfluenced()));
    }

    private static void addActorRegionsToInfoBox(InfoBox infoBox, EntityProperties entityProperties) {
        // Always include both Films and TV shows to make sure some credits appear for all actors
        infoBox.addRegion(new ColumnInfoBoxRegion("Films", entityProperties.getFilms(), "Character", "Film"));
        infoBox.addRegion(new ColumnInfoBoxRegion("TV Shows", entityProperties.getTvShows(), "Character", "TV Show"));
    }

    private static void addBusinessPersonRegionsToInfoBox(InfoBox infoBox, EntityProperties entityProperties) {
        infoBox.addRegion(new SimpleTextInfoBoxRegion("Founded", entityProperties.getOrganizationsFounded()));
        infoBox.addRegion(new ColumnInfoBoxRegion(
                "Leadership",
                entityProperties.getLeadershipRoles(),
                "Organization",
                "Role",
                "Title",
                "From / To"
        ));
        infoBox.addRegion(new ColumnInfoBoxRegion(
                "Board Member",
                entityProperties.getBoardMemberships(),
                "Organization",
                "Role",
                "Title",
                "From / To"
        ));
    }

    private static void addLeagueRegionsToInfoBox(InfoBox infoBox, EntityProperties entityProperties) {
        infoBox.addRegion(new SimpleTextInfoBoxRegion("Sport", entityProperties.getLeagueSport()));
        infoBox.addRegion(new SimpleTextInfoBoxRegion("Official Website", entityProperties.getOfficialWebsite()));
        infoBox.addRegion(new SimpleTextInfoBoxRegion("Championship", entityProperties.getChampionship()));
        infoBox.addRegion(new SimpleTextInfoBoxRegion("Teams", entityProperties.getTeams()));
        infoBox.addRegion(new WrappingTextInfoBoxRegion("Description", entityProperties.getDescriptions()));
    }

    private static void addSportsTeamRegionsToInfoBox(InfoBox infoBox, EntityProperties entityProperties) {
        infoBox.addRegion(new SimpleTextInfoBoxRegion("Sport", entityProperties.getTeamSport()));
        infoBox.addRegion(new SimpleTextInfoBoxRegion("Arena", entityProperties.getTeamArena()));
        infoBox.addRegion(new SimpleTextInfoBoxRegion("Championships", entityProperties.getTeamChampionShips()));
        infoBox.addRegion(new SimpleTextInfoBoxRegion("Founded", entityProperties.getTeamFounded()));
        infoBox.addRegion(new SimpleTextInfoBoxRegion("Leagues", entityProperties.getTeamLeagues()));
        infoBox.addRegion(new SimpleTextInfoBoxRegion("Locations", entityProperties.getTeamLocations()));
        infoBox.addRegion(new ColumnInfoBoxRegion(
                "Coaches",
                entityProperties.getTeamCoaches(),
                "Name",
                "Position",
                "From / To"
        ));
        infoBox.addRegion(new ColumnInfoBoxRegion(
                "PlayersRoster",
                entityProperties.getTeamPlayers(),
                "Name",
                "Position",
                "Number",
                "From / To"
        ));
        infoBox.addRegion(new WrappingTextInfoBoxRegion("Description", entityProperties.getDescriptions()));
    }
}
