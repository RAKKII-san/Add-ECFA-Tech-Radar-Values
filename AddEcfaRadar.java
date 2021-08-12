import java.util.Scanner;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/** 
 * 
 */
public class AddEcfaRadar {
    /** Gets a .ssc file to be read 
     *  if no arguments were given in the command line.
     * @return The file given by the user.
     * @throws IOException
     */
    public static File getFile() throws IOException {
        Scanner kb = new Scanner(System.in);
        String filename;
        File sscFile;
        Pattern sscPtrn = Pattern.compile("\\.ssc$");
        boolean fileFound = false;
        do {
            System.out.println("Enter the .ssc filename you want " +
                               "to modify: ");
            filename = kb.nextLine();
            sscFile = new File(filename);

            if (!sscFile.exists()) {
                System.out.println("That file does not exist. "
                    + "Please try again or press ctrl->c to exit.");
                continue;
            }

            Matcher matchSsc = sscPtrn.matcher(filename);

            if (matchSsc.find()) {
                fileFound = true;
            } else {
                System.out.println("File is not a .ssc file. Please " +
                                   "enter an existing .ssc file.");
            }
        } while (!fileFound);
        
        kb.close();
        return sscFile;
    }

    /** Processes a .ssc file and returns a list of charts with a meter
     * higher than 7.
     * @param input The input file.
     * @return The list of charts.
     */
    public static ArrayList<Chart> getCharts(File input) {
        ArrayList<Chart> chartList = new ArrayList<Chart>();
        try {
            BufferedReader chartReader = 
                new BufferedReader(new FileReader(input));
            int lineNum = 0;
            boolean isChart = false;
            Pattern chartStartPtrn = Pattern.compile("^#NOTEDATA:");
            Pattern chartEndPtrn = Pattern.compile("^#NOTES:");
            Pattern chartMtrPtrn = Pattern.compile("^#METER:");
            Pattern chartDiffPtrn = Pattern.compile("^#DIFFICULTY:");
            Pattern chartStylePtrn = Pattern.compile("^#CHARTSTYLE:");
            Chart tempChart = new Chart();
            while (chartReader.readLine() != null) {
                String currLine = chartReader.readLine();
                Matcher chartStart = chartStartPtrn.matcher(currLine);
                Matcher chartEnd = chartEndPtrn.matcher(currLine);
                lineNum++;
                if (chartStart.find()) {
                    isChart = true;
                }

                if (isChart) {
                    Matcher chartMeter = chartMtrPtrn.matcher(currLine);
                    Matcher chartDiff = chartDiffPtrn.matcher(currLine);
                    Matcher chartStyle = chartStylePtrn.matcher(currLine);
                    
                    if (chartMeter.find()) {
                        int meter = Integer.parseInt(currLine);
                        if (meter < 7) {
                            tempChart = new Chart();
                            isChart = false;
                        } else {
                            tempChart.setMeter(meter);
                        }
                    }
                    
                    if (chartDiff.find()) {
                        String[] fields = currLine.split("[:;]");
                        tempChart.setDifficulty(fields[1]);
                        tempChart.setStyle(true);
                    }
                    
                    if (chartStyle.find()) {
                        tempChart.setPosition(lineNum);
                        String techValString = "speed=\\d{1,}," +
                            "stamina=\\d{1,},tech=\\d{1,}," +
                            "movement=\\d{1,},timing=\\d{1,}," +
                            "gimmick=\\D{1,}";
                        Pattern techValPtrn = Pattern.compile(techValString);
                        Matcher techValMatch = techValPtrn.matcher(currLine);
                        if (techValMatch.find()) {
                            String[] fields = currLine.split("[=,;]");
                            tempChart.setSpeed(Integer.parseInt(fields[1]));
                            tempChart.setStamina(Integer.parseInt(fields[3]));
                            tempChart.setTechnique(Integer.parseInt(fields[5]));
                            tempChart.setMovement(Integer.parseInt(fields[7]));
                            tempChart.setRhythms(Integer.parseInt(fields[9]));
                            tempChart.setGimmicks(fields[11]);
                        }
                    }
                }
                if (chartEnd.find()) {
                    if (isChart) {
                        chartList.add(tempChart);
                    }
                    if (tempChart.getPosition() == 0) {
                        tempChart.setPosition(lineNum - 4);
                    }
                    tempChart = new Chart();
                    isChart = false;
                }
            }
            chartReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return chartList;
    }

    /** Checks to see if there are any ECFA leveled charts (7+) available.
     *  If not, exit the program
     *  If yes, let the user choose what chart to modify
     *  or type 0 to write to the file and exit the program.
     * @param chartList
     */
    public static int listChart(ArrayList<Chart> chartList) {
        int choice = -1;
        Scanner kb = new Scanner(System.in);
        if (chartList.size() == 0) {
            System.out.println("There is no chart with a level " +
                               "higher than 7 in this file. " +
                               "The program will now exit.");
            System.exit(0);
        }

        for (int i = 0; i < chartList.size(); i++) {
            Chart currCht = chartList.get(i);
            String chartLine = String.valueOf(i+1) + ") " +
                            currCht.getDifficulty() + " " +
                            currCht.getMeter() + ": Speed: " +
                            currCht.getSpeed() + ": Stamina: " +
                            currCht.getStamina() + ": Technique: " +
                            currCht.getTechnique() + ": Movement: " +
                            currCht.getMovement() + ": Rhythms: " +
                            currCht.getRhythms() + ": Gimmicks: " +
                            currCht.getGimmicks();
            System.out.println(chartLine);
        }
        System.out.println();
        while (choice < 0 || choice > chartList.size()) {
            System.out.print("Choose a chart to modify tech values " +
                             "or choose 0 to save and exit.");
            choice = kb.nextInt();
            if (choice < 0 || choice > chartList.size()) {
                System.out.println("Invalid choice. Please choose again.");
            }
        }
        kb.close();
        return choice;
    }

    /**
     * @param chartChoice
     * @param chartList 
     * 
     */
    public static ArrayList<Chart> enterValues(ArrayList<Chart> chartList, 
                                               int chartChoice) {
        String[] prompt = {"Speed", "Stamina", "Technique", "Movement",
                           "Rhythms"};
        Chart chart = chartList.get(chartChoice);
        int speed = getValue(prompt[0]);
        int stamina = getValue(prompt[1]);
        int tech = getValue(prompt[2]);
        int movement = getValue(prompt[3]);
        int timing = getValue(prompt[4]);
        String gimmick = getGimmicks();

        chart.setSpeed(speed);
        chart.setStamina(stamina);
        chart.setTechnique(tech);
        chart.setMovement(movement);
        chart.setRhythms(timing);
        chart.setGimmicks(gimmick);
        chartList.set(chartChoice, chart);
        return chartList;
    }

    /**
     * 
     * @param prompt
     * @return
     */
    public static int getValue(String prompt) {
        Scanner kb = new Scanner(System.in);
        int choice = 0;
        while (choice < 1 || choice > 10) {
            System.out.print("Enter a number from 1-10 for " + prompt + ": ");
            choice = kb.nextInt();
            if (choice < 1 || choice > 10) {
                System.out.println("Invalid input.");
            }
        }
        kb.close();
        return choice;
    }

    /**
     * 
     * @return
     */
    public static String getGimmicks() {
        Scanner kb = new Scanner(System.in);
        int choice = -1;
        while (choice < 0 || choice > 4) {
            System.out.println("Enter a number corresponding to the " +
                             "intensity of gimmicks,");
            System.out.println("0=none, 1=light, 2=medium, 3=heavy, 4=cmod ok");
            choice = kb.nextInt();
            if (choice < 0 || choice > 4) {
                System.out.println("Invalid input.");
            }
        }
        kb.close();

        switch (choice) {
            case 0:
                return "none";
            case 1:
                return "light";
            case 2:
                return "medium";
            case 3:
                return "heavy";
            case 4:
                return "cmod";
            default:
                return "none";
        }
    }

    /**
     * 
     * @param myFile
     * @param chartList
     */
    public static File writeToFile(File myFile, ArrayList<Chart> chartList) {
        File newFile = new File(myFile.getName());
        /*
            
        */
        return newFile;
    }

    public static void main(String[] args) throws IOException {
        File myFile;
        Pattern sscPtrn = Pattern.compile("\\.ssc$");
        if (args.length == 1) {
            myFile = new File(args[0]);
            Matcher matchSsc = sscPtrn.matcher(args[0]);
            if (!myFile.exists()) {
                System.out.println("File does not exist. Please enter " +
                                   "an existing .ssc file.");
                System.exit(1);
            }
            if (!matchSsc.find()) {
                System.out.println("File is not a .ssc file. Please " +
                                   "enter an existing .ssc file.");
                System.exit(1);
            }
        } else {
            myFile = getFile();
        }
        ArrayList<Chart> chartList = getCharts(myFile);
        int chartChoice = 1;
        while (chartChoice > 0) {
            chartChoice = listChart(chartList);
            chartList = enterValues(chartList,chartChoice);
        }
        
    }
}

