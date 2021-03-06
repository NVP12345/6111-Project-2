COMS E6111 ADVANCED DATABASE SYSTEMS
PROJECT 2

a)TEAM:  NICOLO PIZZOFERRATO(nvp2015)
         NATASHA S KENKRE(nsk2141)

b)The following is a list of all the files that we are submitting:
> source files :
                 Project2Main.java (main file)
                 domain/infobox/ColumnInfoBoxRegion.java
                 domain/infobox/InfoBox.java
                 domain/infobox/InfoBoxRegion.java
                 domain/infobox/SimpleTextInfoBoxRegion.java
                 domain/infobox/WrappingTextInfoBoxRegion.java
                 domain/Entity.java
                 domain/EntityCreatedElement.java
                 domain/EntityProperties.java
                 domain/EntityType.java
                 util/FreebaseApiUtil.java
                 util/InfoboxGeneratorUtil.java
                 util/QuestionAnswerUtil.java
                 util/StringUtils.java
> libraries (jars):
                 lib/commons-codec-1.10.jar
                 lib/org.json-20120521.jar
> build files:
                 build.xml
> text files:
                 README.txt
                 transcript_infobox_billgates
                 transcript_infobox_jackson
                 transcript_infobox_miamiheat
                 transcript_infobox_nba
                 transcript_infobox_nfl
                 transcript_infobox_nyknicks
                 transcript_infobox_robertdowneyjr
                 transcript_question_google
                 transcript_question_lordoftherings
                 transcript_question_microsoft
                 transcript_question_romeoandjuliet


c)The language used for the implementation of this code is Java. The main file that should be executed is Project2Main.java.

Usage:

$ ant
$ java -cp "Proj2.jar:lib/*" Project2Main -key <freebase API key> -q <'query'> -t <infobox|question>

d) Since this is a simple program, most of the code is written as static methods. The main class contains all the program flow logic
   until we switch on the "question" or "infobox" type, at which point the appropriate classes handle fulfilling the request. Most of
   the parsing of the responses is found in the EntityProperties class (for the infobox) and in the EntityCreatedElement class (for
   question answering). The FreebaseApiUtil class handles formatting all requests. The InfoboxGeneratorUtil and QuestionAnswerUtil
   classes handle building the data and printing the output.

   The mapping of Freebase properties to entity properties of interest can be found in the EntityProperties class. The keys for the
   elements of interest are hard-coded in appropriate places, and the public methods of that class obfuscate the Freebase formatting and
   allow callers to simply get Strings or lists of Strings (or lists of lists of strings, in the cases of multiple columns) containing
   the desired data.

e) (No part e specified in the project description)

f) Freebase API key: AIzaSyBrtC56P8AyVaw9scsNSpYe-r1uJiTiRzE

g) There is logic in the EntityProperties constructor (lines 61 through 65) that ensure leagues are not also attributed as authors (to
   be safe, we exclude all types associated with being a Person when the entity in question is either a league or sports team).

   To make sure actors are always attributed with some credits (since we consider TV actors as well as film actors, there could be actors
   with only TV credits where the reference program would print no films) we always print both TV and film credits. This logic can be
   found in InfoboxGeneratorUtil (lines 95 and 96).
