import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.*;

public class closestpair {

    private static final boolean DEBUG_PRINTS = true;

    public static void main(String[] args) {
        List<Point> points;
        long startTime = System.currentTimeMillis();

        try {
            points = inhabitList(args[0]);
            Point[] pointsArray = points.toArray(new Point[0]);
            long fileTime = System.currentTimeMillis()-startTime;
            System.err.println("\n\tfile read: " + formatLongTime(fileTime));

            Arrays.sort(pointsArray, Comparator.comparingDouble(p -> p.x));
            long sortTime = System.currentTimeMillis()-startTime;
            System.err.println("\n\tarray sort: " + formatLongTime(sortTime));

            System.out.println(nlognSolution(pointsArray,0, pointsArray.length-1));
            long doneTime = System.currentTimeMillis()-startTime;
            System.err.println("\n\tdone: " + formatLongTime(doneTime));

        } catch (FileNotFoundException e) {
            System.err.println("Could not find file, exiting...");
            return;
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            System.err.println("Wrong number of arguments.");
            return;
        }

    }

    private static double nlognSolution(Point[] points, int lower, int upper) {
        int spliceSize = upper-lower;
        if(spliceSize <= 3) {
            Point[] splice = Arrays.copyOfRange(points,lower,upper);
            return quadraticSolution(splice);
        }

        double deltaL = nlognSolution(points,0,spliceSize/2);
        double deltaR = nlognSolution(points,spliceSize/2+1,spliceSize);
        double delta = Math.min(deltaL, deltaR);

        // Finding indicies for the strip array.
        int start;
        int end;
        int middle = lower + spliceSize/2;
        int i = middle;

        while(points[i].x >= points[middle].x - delta) {
            if(i==0) {
                break;
            } else {
                i--;
            }
        }
        start = i;
        i = middle;

        while(points[i].x <= points[middle].x + delta) {
            if(i==upper) {
                break;
            } else {
                i++;
            }
        }
        end = i;

        Point[] strip = Arrays.copyOfRange(points, start, end);

        Arrays.sort(strip, Comparator.comparingDouble(p -> p.y));

        double result = delta;
        for(int j = 0; j<strip.length; j++) {
            for(int k = 1; k<=15; k++) {
                try {
                    double distance = strip[j].distanceTo(strip[j+k]);
                    if(distance<result) result=distance;
                } catch (ArrayIndexOutOfBoundsException ignored){
                }
            }
        }
        return result;
    }

    private static double quadraticSolution(Point[] points) {
        //ArrayList<Point> closestPair = new ArrayList<>();
        double shortestDistance = Double.MAX_VALUE;

        for(Point p : points) {
            for(Point pp : points) {
                double currDist = p.distanceTo(pp);
                if(!p.equals(pp) && currDist<shortestDistance) {
                    shortestDistance = currDist;
//                    closestPair.clear();
//                    closestPair.add(p);
//                    closestPair.add(pp);
                }
            }
        }
        return shortestDistance;
    }

    private static List<Point> inhabitList(String fileName) throws FileNotFoundException {
        ArrayList<Point> points = new ArrayList<>();
        Scanner sc = new Scanner(new FileInputStream(fileName));
        int nonPointLines = 0;
        int n = 0;

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
        if(DEBUG_PRINTS) {
            if(n!=points.size()) {
                System.err.println("Could not parse "+n+" points as described in the file.");
            }
            System.err.println("thrown lines count: " + nonPointLines);
            System.err.println("parsed lines count: " + points.size());
            System.err.println("head:\t"+points.get(0));
            System.err.println("last:\t"+points.get(points.size()-1));
        }
        return points;
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

        public double distanceTo(Point other) {
            return Math.hypot(this.x-other.x, this.y-other.y);
        }
    }

    private static String formatLongTime(long time) {
        long second = (time / 1000) % 60;
        long minute = (time / (1000 * 60)) % 60;

        return String.format("%02dm %02ds %dms", minute, second, time);
    }
}

