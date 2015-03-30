package util;

import domain.EntityCreatedElement;

import java.util.Collections;
import java.util.List;

public class QuestionAnswerUtil {

    public static void answerQuestion(String query) {
        query = query.toLowerCase();
        if ( ! query.startsWith("who created ") ) {
            System.out.println("Wrong question!!!");
            System.exit(1);
        }

        query = query.substring(12);
        while (query.endsWith("?")) {
            query = query.substring(0, query.length() - 1);
        }

        List<EntityCreatedElement> entityCreatedElements = FreebaseApiUtil.getEntitiesWhoCreatedQuery(query);
        Collections.sort(entityCreatedElements);
        for (int i = 0; i < entityCreatedElements.size(); i++) {
            System.out.format("%d. %s\n", i + 1, entityCreatedElements.get(i));
        }
    }

}
