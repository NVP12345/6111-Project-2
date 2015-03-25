package domain.infobox;

import domain.EntityType;
import util.StringUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TitleInfoBoxRegion extends InfoBoxRegion {

    private final String title;

    public TitleInfoBoxRegion(String title, List<EntityType> types) {
        StringBuilder sb = new StringBuilder();
        sb.append(title);

        if ( types.size() > 1 || ! EntityType.PERSON.equals(types.get(0)) ) {
            sb.append("(");
            Iterator<EntityType> it = types.iterator();
            while(it.hasNext()) {
                EntityType type = it.next();
                if ( ! EntityType.PERSON.equals(type) ) {
                    sb.append(type.name());
                    if (it.hasNext()) {
                        sb.append(", ");
                    }
                }
            }
            sb.append(")");
        }

        this.title = sb.toString();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public List<String> getLines(int indent, int length) {
        int columns = indent + length;
        int columnDifference = columns - title.length();
        String leftCenteringSpace = "";
        String rightCenteringSpace = "";
        if (columnDifference > 0) {
            leftCenteringSpace = rightCenteringSpace = StringUtils.multiplyString(" ", columnDifference / 2);
            if (columnDifference % 2 == 1) {
                rightCenteringSpace = rightCenteringSpace + " ";
            }
        }

        List<String> lines = new LinkedList<String>();
        lines.add(leftCenteringSpace + title + rightCenteringSpace);
        return lines;
    }

}
