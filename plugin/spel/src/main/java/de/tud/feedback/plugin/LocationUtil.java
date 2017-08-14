package de.tud.feedback.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocationUtil {
    private static final Logger logger = LoggerFactory.getLogger(LocationUtil.class);

    // pattern
    // ^\s*P:\s*((?:-?[0-9]+[,|\.][0-9]+\s+){2,3})\s*O:\s*((?:(?:(?:-?[0-9]+[,|\.][0-9]+(?:\s+)){3}|(?:-?[0-9]+[,|\.][0-9]+(?:\s+)){1})-?[0-9]+[,|\.][0-9]+(?:\s*)))

    private static final String rxPos = "^\\s*P:\\s*((?:-?[0-9]+[,|\\.][0-9]+\\s+){2,3})\\s*O:\\s*((?:(?:(?:-?[0-9]+[,|\\.][0-9]+(?:\\s+)){3}|(?:-?[0-9]+[,|\\.][0-9]+(?:\\s+)){1})-?[0-9]+[,|\\.][0-9]+(?:\\s*)))";
    private static final Pattern pattern = Pattern.compile(rxPos);
    private static final int DEFAULT_PRECISION = 18;

    private Position position;
    private Orientation orientation;
    private boolean matches = false;

    /**
     * Takes a location string e.g. P: 3,3 1,2 O: 3,3 0,3
     * <br>
     * Takes also 3 arguments for P: (Position) or 4 args for O: (Orientation)
     *
     * @param locationString
     */
    public LocationUtil(String locationString) {
        if (locationString == null) {
            return;
        }
        Matcher matcher = pattern.matcher(locationString);
        matches = matcher.matches() && matcher.groupCount() == 2;
        if (matches) {
            createPosAndOr(matcher);
        }
    }

    public Position getPosition() {
        return position;
    }

    public Orientation getOrientation() {
        return orientation;
    }


    private static String getStringFormat(int precision) {
        return "P: %." + precision + "f %." + precision + "f %." + precision + "f O: %." + precision + "f %."
                + precision + "f %." + precision + "f %." + precision + "f";
    }

    private void createPosAndOr(Matcher matcher) {
        // we must replace , for . otherwise parseDouble will fail
        String posStr = matcher.group(1).replaceAll(",", ".");
        String orStr = matcher.group(2).replaceAll(",", ".");
        String[] posSpl = posStr.trim().split(" ");
        String[] orSpl = orStr.trim().split(" ");
        createPos(posSpl);
        createOr(orSpl);
    }

    private double[] getCoordinates(String[] split) {
        double[] coord = null;
        try {
            if (split.length >= 2) {
                coord = new double[split.length];
                coord[0] = Double.parseDouble(split[0].trim());
                coord[1] = Double.parseDouble(split[1].trim());
            } else {
                logger.error("cant find coordinates for robot location");
                return null;
            }

            if (split.length >= 3) {
                coord[2] = Double.parseDouble(split[2].trim());
            }

            if (split.length >= 4) {
                coord[3] = Double.parseDouble(split[3].trim());
            }
        } catch (NullPointerException | NumberFormatException e) {
            e.printStackTrace();
        }
        return coord;
    }

    private void createPos(String[] posSpl) {
        double[] coord = getCoordinates(posSpl);
        if (coord == null) {
            return;
        }
        if (coord.length == 2) {
            position = new Position(coord[0], coord[1]);
        } else if (coord.length == 3) {
            position = new Position(coord[0], coord[1], coord[2]);
        }
    }

    private void createOr(String[] orSpl) {
        double[] coord = getCoordinates(orSpl);
        if (coord == null) {
            return;
        }
        if (coord.length == 2) {
            orientation = new Orientation(coord[0], coord[1]);
        } else if (coord.length == 4) {
            orientation = new Orientation(coord[0], coord[1], coord[2], coord[3]);
        }
    }

    public static class Orientation{
        double x;
        double y;
        double z;
        double zz;

        public Orientation(double x, double y){
            this(x, y, 0, 0);
        }

        public Orientation(double x, double y, double z, double zz){
            this.x = x;
            this.y = y;
            this.z = z;
            this.zz = zz;
        }

    }

    public static class Position{
        double x;
        double y;
        double z;

        public Position(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Position(double x, double y) {
            this(x, y, 0);
        }

        boolean isInRangeOf(double x, double y, double precision){
            boolean matchX = this.x == x ? true : Math.abs(this.x - x) < precision;
            boolean matchY = this.y == y ? true : Math.abs(this.y - y) < precision;
            return  matchX && matchY;
        }
    }
}
