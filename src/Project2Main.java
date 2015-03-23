import util.FreebaseApiUtil;
import util.InfoboxGeneratorUtil;
import util.QuestionAnswerUtil;

public class Project2Main {

    public static void main(String[] args) {
        if (args.length < 6) {
            System.out.println("Usage: java Proj2 -key <Freebase API key> -q <query> -t <infobox|question>");
            System.exit(1);
        }

        String freebaseApiKey = null;
        String query = null;
        String type = null;

        for (int i = 0; i < args.length; i += 2) {
            String flag = args[i];
            if ("-key".equals(flag)) {
                if (freebaseApiKey == null) {
                    freebaseApiKey = args[i + 1];
                } else {
                    exitDueToDuplicateFlag(flag);
                }

            } else if ("-q".equals(flag)) {
                if (query == null) {
                    query = args[i + 1];
                } else {
                    exitDueToDuplicateFlag(flag);
                }
            } else if ("-t".equals(flag)) {
                if (type == null) {
                    type = args[i + 1];
                } else {
                    exitDueToDuplicateFlag(flag);
                }
            } else {
                System.out.format("Error: Unknown flag '%s'\n", flag);
                System.exit(1);
            }
        }

        FreebaseApiUtil.setFreebaseApiKey(freebaseApiKey);

        if ("infobox".equals(type)) {
            InfoboxGeneratorUtil.generateInfobox(query);
        } else if ("question".equals(type)) {
            QuestionAnswerUtil.answerQuestion(query);
        } else {
            System.out.format("Error: Unknown type '%s'\n", type);
            System.exit(1);
        }

    }

    private static void exitDueToDuplicateFlag(String flag) {
        System.out.format("Error: Flag '%s' specified twice", flag);
        System.exit(1);
    }

}
