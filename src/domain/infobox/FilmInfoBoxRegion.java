package domain.infobox;

import domain.FilmData;
import util.StringUtils;

import java.util.LinkedList;
import java.util.List;

public class FilmInfoBoxRegion extends InfoBoxRegion {

    private final List<FilmData> filmDataList;

    public FilmInfoBoxRegion(List<FilmData> filmDataList) {
        this.filmDataList = filmDataList;
    }

    @Override
    public List<String> getLines(int indent, int length) {


        List<String> lines = new LinkedList<String>();
        lines.add(buildIndentWithName(indent, "Film") + buildTwoColumns(length, "Character", "Film Name"));
        lines.add(buildBlankIndent(indent) + "|" + StringUtils.multiplyString("-", length - 1));

        for (FilmData filmData : filmDataList) {
            lines.add(buildBlankIndent(indent) + buildTwoColumns(length, filmData.getCharacterName(), filmData.getFilmName()));
        }
        return lines;
    }

    private String buildTwoColumns(int length, String firstString, String secondString) {
        int firstColumnLength = length / 2;
        int secondColumnLength = length - firstColumnLength;

        StringBuilder sb = new StringBuilder();
        sb.append("| ");
        sb.append(buildTextWithinWidth(firstString, firstColumnLength - 2));
        sb.append("| ");
        sb.append(buildTextWithinWidth(secondString, secondColumnLength - 2));
        return sb.toString();
    }
}
