package domain.infobox;

import util.StringUtils;

import java.util.LinkedList;
import java.util.List;

public class InfoBox {

    private static final int NUM_INDENT_SPACES = 10;
    private static final int REGION_INDENT = 17;
    private static final int REGION_LENGTH = 82;
    private static final String BOUNDARY = "|";
    private final List<InfoBoxRegion> regions = new LinkedList<InfoBoxRegion>();

    public void addRegion(InfoBoxRegion infoBoxRegion) {
        regions.add(infoBoxRegion);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(buildLineSeparator());
        for (InfoBoxRegion region : regions) {
            for (String line : region.getLines(REGION_INDENT, REGION_LENGTH)) {
                sb.append(buildIndentSpaces());
                sb.append(BOUNDARY);
                sb.append(line);
                sb.append(BOUNDARY);
                sb.append("\n");
            }
            sb.append(buildLineSeparator());
        }
        return sb.toString();
    }

    private String buildLineSeparator() {
        return buildIndentSpaces() + " " + StringUtils.multiplyString("-", REGION_INDENT + REGION_LENGTH + 1) + "\n";
    }

    private String buildIndentSpaces() {
        return StringUtils.multiplyString(" ", NUM_INDENT_SPACES);
    }
}
