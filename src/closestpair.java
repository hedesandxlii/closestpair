import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.*;

public class closestpair {

    static final boolean DEBUG_PRINTS = false;

    public static void main(String[] args) {
        if(args.length!=1) {
            System.err.println("Wrong number of arguments.");
            return;
        }

        List<Point> points = new ArrayList<>();
        try {
            inhabitList(args[0], points);
        } catch (FileNotFoundException e) {
            System.out.println("Could not find file, exiting...");
            return;
        }
    }

    private static void inhabitList(String fileName, List<Point> points) throws FileNotFoundException {
        int nonPointLines = 0;
        int n = 0;
        Scanner sc = new Scanner(new FileInputStream(fileName));
        while(sc.hasNextLine()) {
            String currLine = sc.nextLine();

            if(currLine.startsWith("DIMENSION")) {
                String[] split = currLine.split("\\s+");
                try {
                    n = Integer.parseInt(split[split.length-1]);
                } catch(NumberFormatException ignored){
                }
            }

            try {
                Point p = parsePoint(currLine);
                points.add(p);
            } catch(ParseException ignored) {
                nonPointLines++;
            }
        }
        if(n!=points.size()) {
            System.err.println("Could not parse "+n+" points as described in the file.");
        }
        if(DEBUG_PRINTS) {
            System.err.println("thrown lines count: " + nonPointLines);
            System.err.println("parsed lines count: " + points.size());
            System.err.println("head:\t"+points.get(0));
            System.err.println("last:\t"+points.get(points.size()-1));
        }
    }

    private static Point parsePoint(String pointString) throws ParseException {
        //regex for one or more whitespace characters(space, tab, linebreak, etc)
        String[] split = pointString.split("\\s+");


        String label;
        double x;
        double y;
        try {
            // Sorry, kom inte på något bättre sätt....
            int arrayIndex = 0;
            if(split[0].equals("")) {
                arrayIndex++;
            }
            // Sorry, kom inte på något bättre sätt....

            label = split[arrayIndex];
            x = Double.parseDouble(split[arrayIndex+1]);
            y = Double.parseDouble(split[arrayIndex+2]);
        } catch(Exception e) {
            throw new ParseException("Could not parse string.",-1);
        }

        return new Point(label, x, y);
    }

    private static class Point {
        String label;
        double x;
        double y;

        public Point(String label, double x, double y) {
            this.label = label;
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return label+": ("+x+", "+y+")";
        }
    }
        
}
