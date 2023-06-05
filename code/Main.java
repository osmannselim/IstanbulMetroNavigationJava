/* Osman Selim Yuksel
2021400105
26.03.2023
This code firstly reads the input file, collect and classify the data by using basic methods and arraylists. Secondly, the code checks if the input stations can be connected or not. If not, code handles the errors
Otherwise, code finds the path by using recursive findWay method, which basically tries all the ways that can be possible to go from the starting station to all other stations in the map by visiting all neighbors af a station.
After finding a way, code prints stations in the path and draws the animation like a navigation
There is also another method getLineIndexOfStation which gets the index of station's metro line
The main two principles used in this code are INDEXING (station's coordinate index and station index are the same for example, to reach them easily) and RECURSIVE call (trying all possible ways)
*/
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        int lineNumber = 10;
        int breakpointNumber = 7;
        ArrayList<ArrayList<String>> AllDataOfMetroLines = new ArrayList<>();      // firstly we will store the unprocessed lines from the text file in this arraylist. We will classify the data afterwards
        ArrayList< ArrayList<ArrayList<Integer> > > stationCoordinates3D = new ArrayList<>();    // 3D ArrayList { { {x0,y0}, {x1,y1},.. },..   }    keeps the coordinate information for each station. Note that a station's index in stations2D and its coordinate index will be the same
        ArrayList<String> metroLinesArray = new ArrayList<>();      // Initialize the arrayList containing the names of the metro lines
        ArrayList<ArrayList<Integer> > colorsOfLines = new ArrayList<>();       // Initialize the arrayList keeping the int RGB values representing the colors that each metro line has
        ArrayList<ArrayList<String>> stations2D = new ArrayList<>();    // Initialize the arrayList that has length 10 (for each metro line is stored as 1 ArrayList<String station>)
        ArrayList<ArrayList<String> > breakPointNeighborList = new ArrayList<>();  // initialize the arraylist to keep the neighbors of all the breakpoints

        FileInputStream fis=new FileInputStream("input.txt");
        Scanner sc=new Scanner(fis);
        int i=0; // initialize i just as a counter
        while(i<2*lineNumber)  // there will be 10 metro lines, every metro line is described in txt file by 2 separate lines, which means we should read 20 line to reach metroline info
        {
            String metroInfoLine = sc.nextLine();
            String lineName = metroInfoLine.split(" ")[0];   // get the line name
            String RGBInfo = metroInfoLine.split(" ")[1];   // get the color RGB info of the line (e.g 101,102,106)
            String[] RGBArray = RGBInfo.split(",");        // reach R,G, and B values separately
            metroLinesArray.add(lineName);                 // put the line name in the ArrayList
            ArrayList <Integer> tempColorNum= new ArrayList<>();  // Initialize an ArrayList to store the R,G,B values
            tempColorNum.add(Integer.parseInt(RGBArray[0]));       //add R value
            tempColorNum.add(Integer.parseInt(RGBArray[1]));      // add G value
            tempColorNum.add(Integer.parseInt(RGBArray[2]));     //  add B value
            colorsOfLines.add(tempColorNum);            // Add [<RValue><GValue><BValue>] arraylist to the master ArrayList
            String stationInfoLine = sc.nextLine();    // get the line expressing stations of a given metro line in txt file

            ArrayList <String> tempStations = new ArrayList<>();    // Initialize an ArrayList to store the lines that stores the station data for a certain metroline
            tempStations.add(stationInfoLine);                  // add the station data for a certain metro line to the temp array
            AllDataOfMetroLines.add(tempStations);   // keep the station data for every metro line in a 2D ArrayList

            i+=2;    // increment i by 2 in order for program to reach always odd number lines, which keeps the data of a metro line like this  <NameOfTheLine><RValue><GValue><BValue>
        }

        for (ArrayList<String> tempStationInfo : AllDataOfMetroLines) {      // Iterate over the AllDataOfMetroLines to classify the data

            String[] lineInfo = tempStationInfo.get(0).split(" ");    // reach the information about every metroLine in each iteration by splitting the spaces
            ArrayList <ArrayList <Integer> > tempXYCoordinates = new ArrayList<>();   // initialize a 2D temp ArrayList to keep coordinates of every metro station, then we will add it to the master 3d arraylist
            ArrayList<String> tempStations = new ArrayList<>();                // initialize an ArrayList to keep coordinates of a particular metro station
            for (int count = 0; count<lineInfo.length;count++) {  // iterate over the lineInfo. lineInfo is in the form of  <stationName1>,<x1,y1>,<stationName2>,<x2,y2>,...
                if (count%2 == 0){    // at every even index iteration, reach the name of the line
                    tempStations.add(lineInfo[count]);
                }
                else{     // at every odd index iteration, reach the x and y coordinates of the line as a String having the form of "xCoordinate,yCoordinate"
                    ArrayList<Integer> tempXY = new ArrayList<>();      // initialize an arraylist to store the coordinates like this {x,y}
                    tempXY.add(Integer.parseInt(lineInfo[count].split(",")[0]));   // reach the x coordinate, transform it into integer, and add it into an 2 length arraylist
                    tempXY.add(Integer.parseInt(lineInfo[count].split(",")[1]));   // reach the y coordinate, transform it into integer, and add it into an 2 length arraylist
                    tempXYCoordinates.add(tempXY);     // add the arraylist [x,y] to the 2D array list which keeps the data for a metro line in each iteration
                }
            }
            stations2D.add(tempStations);    // add station names arraylist for every metro line to the master station Arraylist
            stationCoordinates3D.add(tempXYCoordinates); // add the 2D ArrayList containing all coordinates of the stations as pairs {x,y} in a metro line to the Master coordinate arrayList
        }

        ArrayList<ArrayList <String> > commonMetroLines = new ArrayList<>();    // initialize the arraylist to keep all metro lines that are connected to a breakpoint
        ArrayList<String> brkPoints = new ArrayList<>();    // keep the breakpoints in an arraylist
        while(sc.hasNextLine()){   // continue to read the file
            String breakPtLine = sc.nextLine();
            String[] splittedLine = breakPtLine.split(" ");
            brkPoints.add(splittedLine[0]);

            ArrayList<String> lines = new ArrayList<>();  // lines is an arrayList to store the all lines that are connected to specific breakpoint
            for (int k=1; k<splittedLine.length; k++){
                String lineName = splittedLine[k];
                lines.add(lineName);
            }
            commonMetroLines.add(lines);
        }

        ArrayList< ArrayList<String> > allStationsToWrite = new ArrayList<>();  // to write stations in stdDraw, keep the stations whose names will be written in the animation
        for (int ind1 = 0; ind1<stations2D.size(); ind1++ ) {
            ArrayList<String> stations = stations2D.get(ind1);
            ArrayList<String> stationToWrite = new ArrayList<>();

            for (int ind2 = 0; ind2 < stations.size(); ind2++){
                String station = stations.get(ind2);
                if(station.startsWith("*")){   // if station names prefix is "*" this should be written in the canvas
                    stationToWrite.add(station.substring(1));   // add station name to the arraylist containing all stations in a Metro Line that must be written in the canvas
                    stations2D.get(ind1).set(ind2,station.substring(1));  // in the master station names arraylist, update the station name by getting rid of "*"
                }

            }
            allStationsToWrite.add(stationToWrite); // add the arraylist containing all stations to be written in a Metro Line to the array list that keeps all stations to write
        }

        sc.close();

        Scanner input = new Scanner(System.in);
        String station1 = input.nextLine();      // take input from the user
        String station2 = input.nextLine();

        // IF ST1 OR ST2 IS NOT PRESENT IN THE MAP, PRINT AN ERROR MESSAGE AND RETURN THE PROGRAM
        int lineIndexOfSt1 = getLineIndexOfStation(stations2D,station1);  // get the indices of stations. if -1 is returned, this means the station is not a valid station
        int lineIndexOfSt2 = getLineIndexOfStation(stations2D,station2);
        if (lineIndexOfSt1==-1 || lineIndexOfSt2==-1) {
            System.out.println("The station names provided are not present in this map.");
            return;
        }

        // finding the neighbors of the breakpoints
        for (int brkPointIndex=0; brkPointIndex<brkPoints.size(); brkPointIndex++) {   // iterate over the breakpoint arraylist
            ArrayList<String> commonLines = commonMetroLines.get(brkPointIndex);     // find the metro lines that are connected to the current breakpoint
            String breakPoint = brkPoints.get(brkPointIndex);
            ArrayList<String> tempLst = new ArrayList<>();
            for (String commonLine : commonLines) {
                int metroLineIndex = metroLinesArray.indexOf(commonLine);
                ArrayList<String> line = stations2D.get(metroLineIndex);
                int brkPtIndOfSt = line.indexOf(breakPoint);
                if (! (brkPtIndOfSt == 0)){   // if breakpoint is not the first station of the metro line
                    tempLst.add(line.get(brkPtIndOfSt-1));   // add the left neighbor of it to the temp neighbor list
                }
                if (! (brkPtIndOfSt+1 == line.size())){    // if breakpoint is not the last station of the metro line
                    tempLst.add(line.get(brkPtIndOfSt+1));   // add the right neighbor of it to the temp neighbor list
                }

            }
            breakPointNeighborList.add(tempLst);
        }

        // IF THERE IS NO WAY TO GO FROM ST1 TO ST2, PRINT AN ERROR MESSAGE AND RETURN THE PROGRAM
        ArrayList<String> road = new ArrayList<>();
        String emptStr = "";    // only for giving as a parameter to the find way function initially
        if (! findWay(breakPointNeighborList, brkPoints, stations2D, road,station1, station2, emptStr)){
            System.out.println("These two stations are not connected");
            return;
        }

        // printing the stations from starting station to the target station in the correct order
        else{
            for (int c = 0; c<road.size(); c++) {
                int tempIndex = road.size() - c - 1;   // since road is in the reverse order, we get the index by this operation
                System.out.println(road.get(tempIndex));
            }
        }

        // StdDraw Drawing Part
        StdDraw.setCanvasSize(1024, 482); // set the size of the drawing canvas
        StdDraw.setXscale(0,1024); // set the scale of the coordinate system of canvas
        StdDraw.setYscale(0,482);
        StdDraw.enableDoubleBuffering(); // Use for faster animations
        int pauseDuration = 300; // pause duration in milliseconds

        // since roadmap is given the reverse order by findWay method, iterate over the roadmap with the reverse order. at each iteration, update the current station and note that its size is bigger.
        ArrayList<String> passedStations = new ArrayList<>();
        for (int p = 0; p<road.size(); p++){
            StdDraw.clear();    // clear the canvas at each iteration, so that the canvas can be re drawn
            StdDraw.picture(512,241, "background.jpg");

            // Drawing the metro lines as lines and metro stations as points
            for (int ind1 = 0; ind1<stations2D.size(); ind1++){    // iterate over the 2D arraylist keeping all the station names
                for (int ind2 = 0; ind2<stations2D.get(ind1).size()-1; ind2++){
                    String station = stations2D.get(ind1).get(ind2);
                    int innerIndex = stations2D.get(ind1).indexOf(station);
                    int x0 = stationCoordinates3D.get(ind1).get(innerIndex).get(0);    // get the coordinates of first metro station
                    int y0 = stationCoordinates3D.get(ind1).get(innerIndex).get(1);
                    int x1 = stationCoordinates3D.get(ind1).get(innerIndex+1).get(0);  // get the coordinates of second metro station
                    int y1 = stationCoordinates3D.get(ind1).get(innerIndex+1).get(1);
                    Color color0fLine = new Color(colorsOfLines.get(ind1).get(0), colorsOfLines.get(ind1).get(1), colorsOfLines.get(ind1).get(2));
                    StdDraw.setPenRadius(0.012);
                    StdDraw.setPenColor(color0fLine);
                    StdDraw.line(x0,y0,x1,y1);     // Draw a line from first station to second station
                    int xCoordinate = stationCoordinates3D.get(ind1).get(ind2).get(0);   // get the x coordinate of station
                    int yCoordinate = stationCoordinates3D.get(ind1).get(ind2).get(1);   // get the y coordinate of station
                    StdDraw.setPenRadius(0.01);
                    StdDraw.setPenColor(StdDraw.WHITE);
                    StdDraw.point(xCoordinate, yCoordinate);  // Draw a point which reflects a station

                    if (ind2==stations2D.get(ind1).size()-2){       // since while drawing line between stations, lastly we go to the station that comes just before the last station of line.
                        int xCoordinateForLastSt = stationCoordinates3D.get(ind1).get(ind2+1).get(0);
                        int yCoordinateForLastSt = stationCoordinates3D.get(ind1).get(ind2+1).get(1);
                        StdDraw.setPenRadius(0.01);
                        StdDraw.setPenColor(StdDraw.WHITE);
                        StdDraw.point(xCoordinateForLastSt, yCoordinateForLastSt);  // to draw the last stations, we use the code here
                    }
                }
            }

            // Drawing the path to follow
            int tempIndex = road.size()-p-1;   // since road is in the reverse order, we get the index by this operation
            String curStation = road.get(tempIndex);    // get the current station
            passedStations.add(curStation);     // add the station to the list which stores the previous visited stations
            int mtrLineIndex = getLineIndexOfStation(stations2D, curStation);
            int metroStIndex = stations2D.get(mtrLineIndex).indexOf(curStation);
            int xCoordinate = stationCoordinates3D.get(mtrLineIndex).get(metroStIndex).get(0);  // get the coordinates of visited stations
            int yCoordinate = stationCoordinates3D.get(mtrLineIndex).get(metroStIndex).get(1);
            StdDraw.setPenRadius(0.02);
            StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
            StdDraw.point(xCoordinate, yCoordinate);      // draw the current station as a larger point

            // drawing the colored points that represent passed stations
            for (String passedStation : passedStations) {
                int indexOfLine = getLineIndexOfStation(stations2D, passedStation);
                int indexOfSt = stations2D.get(indexOfLine).indexOf(passedStation);
                int Xcoord = stationCoordinates3D.get(indexOfLine).get(indexOfSt).get(0);  // get the coordinates
                int Ycoord = stationCoordinates3D.get(indexOfLine).get(indexOfSt).get(1);
                StdDraw.setPenRadius(0.01);
                StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
                StdDraw.point(Xcoord, Ycoord);    // draw the visited station as smaller point relative to current station
            }

            // Writing the metro station names
            for (int a = 0; a<allStationsToWrite.size(); a++){    // iterate over the arraylist which keeps the station names which will be written to the animation
                for (int b = 0; b<allStationsToWrite.get(a).size(); b++){
                    String stationToWrite = allStationsToWrite.get(a).get(b);
                    int indexOfSt = stations2D.get(a).indexOf(stationToWrite);
                    int xCoord = stationCoordinates3D.get(a).get(indexOfSt).get(0);   // get the x and y coordinates of station
                    int yCoord = stationCoordinates3D.get(a).get(indexOfSt).get(1);
                    StdDraw.setPenRadius(0.01);
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.setFont(new Font("Helvetica", Font.BOLD, 8));
                    StdDraw.text(xCoord, yCoord+5, stationToWrite);     // Write the station name to the canvas
                }
            }
            StdDraw.show();
            StdDraw.pause(pauseDuration);
        }
    }
    public static int getLineIndexOfStation(ArrayList<ArrayList<String>> stations2D, String station) {   //method to find metro line index on which a given station stays
        int indexOfMLine = 0;
        boolean flag = false;
        for (int index1=0; index1<stations2D.size(); index1++){   // iterate over the master 2D station ArrayList
            ArrayList <String> metroLine = stations2D.get(index1);   // get a single metro line ArrayList containing its each station
            if (metroLine.contains(station)){          //  if station is in this metro line
                indexOfMLine = stations2D.indexOf(metroLine);  //  get the index of it
                flag = true;
                break;
            }
        }
        if (!flag)   // if station is not in the given map
            indexOfMLine = -1;
        return indexOfMLine; // if method returns -1 this means station is not a valid station
    }

    // write a recursive method to find a way from a starting station to target. if there is a way, returns true and updates the roadmap list which contains the path to go. If no way, returns false
    public static boolean findWay(ArrayList<ArrayList<String>> breakPointNeighborList, ArrayList<String> brkPoints, ArrayList<ArrayList<String>> stations2D, ArrayList<String> roadMap, String sourceStation, String targetStation, String previousStation) {
        int indexOfLine1 = getLineIndexOfStation(stations2D, sourceStation);       // find the metro line of target station
        ArrayList<String> metroLine1 = stations2D.get(indexOfLine1);   // find the metro line which contains the source station
        int indexOfStation1 = metroLine1.indexOf(sourceStation);   // reach the indices of source and target station in their own metro line lists

        if (Objects.equals(sourceStation, targetStation)){   // when recursive tree reaches the target station
            roadMap.add(targetStation);           // add the target station to the roadmap
            return true;  // return true, finish the call
        }

        // function call for break points separately, because they have more than 2 neighbors
        if (brkPoints.contains(sourceStation)){    // check whether source station is a break point
            int brkPtIndex = brkPoints.indexOf(sourceStation);  // get the index of break point
            ArrayList<String> neighbors = breakPointNeighborList.get(brkPtIndex);  // using this index, find the Array which contains all the neighbors of the source station
            for (String neighbor : neighbors) {   // generate a loop
                if (neighbor.equals(previousStation)) continue;   // if neighbor is already visited, not call the function again, If we do not check this, function will be called infinitely
                boolean flag = findWay(breakPointNeighborList, brkPoints, stations2D, roadMap, neighbor, targetStation, sourceStation);   // call the function call the function for all unvisited neighbors
                if (flag) {   // if the function returns true, this means the way is founded
                    roadMap.add(sourceStation);  // add the target station to the roadMap arraylist
                    return true;
                }
            }
        }

        // function call for neighbor station from the right
        if (! (indexOfStation1+1==metroLine1.size())){    // check whether the source station is tha last station from the right or not
            String rightNeighOfSt1 = metroLine1.get(indexOfStation1+1);   // get the rightmost neighbor of source station
            if (!rightNeighOfSt1.equals(previousStation)) {   // check if the neighbor was not visited. If visited, not call function again
                boolean flag = findWay(breakPointNeighborList, brkPoints, stations2D, roadMap, rightNeighOfSt1, targetStation, sourceStation);  // if neighbor is nat visited, visit it, meaning call the function
                if (flag) {     // if this way results in target
                    roadMap.add(sourceStation);  // // add the target station to the roadMap arraylist
                    return true;    // finish the program
                }
            }
        }

        // function call for neighbor station from the left
        if (! (indexOfStation1==0)){  // check whether the source station is the last station from the left or not
            String leftNeighOfSt1  = metroLine1.get(indexOfStation1-1);   // get the leftmost neighbor of source station
            if (!leftNeighOfSt1.equalsIgnoreCase(previousStation)) {   // check if the neighbor was not visited.
                boolean flag = findWay(breakPointNeighborList,brkPoints, stations2D, roadMap, leftNeighOfSt1, targetStation, sourceStation); // If neighbor is visited, call function
                if (flag) {     // if this way results in target
                    roadMap.add(sourceStation);  // add the target station to the roadMap arraylist
                    return true;   // finish the program
                }
            }
        }
        return false;   // if no way found, return false
    }
}


