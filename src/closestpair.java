import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.*;

public class closestpair {

    private static final boolean DEBUG_PRINTS = true;
    private static final boolean DEBUG_TIMES = true;
    /*
     Hörde labbledaren igår sa att 'roten ur' är en långsam operation och eftersom x<y => x^2<y^2(^2 är ju stört effektiv)
     kan man jämföra kvadrerade avstånd istället och i slutet ta roten ur 1 GÅNG.

     huge.tsp gick från 40s till 9.9s.
      */
    private static final boolean SQUARED_DISTANCES = true;
    private static int n = 0;

    public static void main(String[] args) {
        List<Point> points;
        long startTime = System.currentTimeMillis();

        try {
            // inläsning
            points = inhabitList(args[0]);
            final Point[] pointsArray = points.toArray(new Point[points.size()]);

            if(DEBUG_PRINTS || DEBUG_TIMES) {
                long fileTime = System.currentTimeMillis()-startTime;
                System.err.println("\tfile read: " + formatLongTime(fileTime));
            }

            // init sortering
            Arrays.sort(pointsArray, Comparator.comparingDouble(p -> p.x));

            if(DEBUG_PRINTS || DEBUG_TIMES) {
                long sortTime = System.currentTimeMillis()-startTime;
                System.err.println("\tarray sort: " + formatLongTime(sortTime));
            }

            // allt löst
            double squaredResult = kindaGoodSolution(pointsArray,0, pointsArray.length-1);
            double result = Math.sqrt(squaredResult); //Enda roten ur som händer

            System.out.println(args[0] + ": "+n+" "+result);

            if(DEBUG_PRINTS || DEBUG_TIMES) {
                long doneTime = System.currentTimeMillis()-startTime;
                System.err.println("\tdone: " + formatLongTime(doneTime));
            }

        } catch (FileNotFoundException e) {
            System.err.println("Could not find file, exiting...");
            return;
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            System.err.println("Wrong number of arguments, exiting...");
            return;
        }

    }

    private static double kindaGoodSolution(Point[] points, int lowerIndex, int upperIndex) {
        int spliceSize = upperIndex-lowerIndex+1;
        if(spliceSize <= 30) {
            //                                                      vv skiten är exclusive.
            Point[] splice = Arrays.copyOfRange(points,lowerIndex,upperIndex+1);
            return quadraticSolution(splice);
        }

        double deltaL = kindaGoodSolution(points, lowerIndex,lowerIndex+spliceSize/2);
        double deltaR = kindaGoodSolution(points,lowerIndex+spliceSize/2+1, upperIndex);
        double delta = Math.min(deltaL, deltaR);

        // Finding indicies for the strip array.
        int start;
        int end;
        int middle = lowerIndex + spliceSize/2;
        int i = middle;

        while(points[i].x >= points[middle].x - delta) {
            if(i==lowerIndex) {
                break;
            } else {
                i--;
            }
        }
        start = i;
        i = middle;

        while(points[i].x <= points[middle].x + delta) {
            if(i==upperIndex) {
                break;
            } else {
                i++;
            }
        }
        end = i;

        Point[] strip = Arrays.copyOfRange(points, start, end);

        // sorting in Y-axis
        Arrays.sort(strip, Comparator.comparingDouble(p -> p.y));
        // finding the smallest distance in the strip.
        double result = delta;
        for(int j = 0; j<strip.length; j++) {
            for(int k = 1; k<=15; k++) {
                try {
                    double distance = strip[j].distanceTo(strip[j+k]);
                    if(distance<result) {
                        result = distance;

                    } else {
                        // continues on the next point j++ if distance if > result.
                        break;
                    }
                } catch (ArrayIndexOutOfBoundsException ignored){
                }
            }
        }

        return result;
    }

    private static double quadraticSolution(Point[] points) {
        double shortestDistance = Double.MAX_VALUE;

        for(Point p : points) {
            for(Point pp : points) {
                double currDist = p.distanceTo(pp);
                if(!p.equals(pp) && currDist<shortestDistance) {
                    shortestDistance = currDist;
                }
            }
        }
        return shortestDistance;
    }

    private static List<Point> inhabitList(String fileName) throws FileNotFoundException {
        ArrayList<Point> points = new ArrayList<>();
        Scanner sc = new Scanner(new FileInputStream(fileName));
        int nonPointLines = 0;

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
            if(SQUARED_DISTANCES) {
                return Math.pow(this.x-other.x,2)+Math.pow(this.y-other.y,2);
            } else {
                return Math.hypot(this.x-other.x,this.y-other.y);
            }
        }
    }

    private static String formatLongTime(long time) {
        long second = (time / 1000) % 60;
        long minute = (time / (1000 * 60)) % 60;

        return String.format("%02dm %02ds %dms", minute, second, time);
    }
}

