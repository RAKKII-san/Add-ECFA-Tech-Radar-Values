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

public class AddEcfaRadar {
    static Scanner kb = new Scanner(System.in);

    /** 
     * Gets a .ssc file to be read 
     * if no arguments were given in the command line.
     * @return The file given by the user.
     * @throws IOException
     */
    public static File getFile() throws IOException {
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
        return sscFile;
    }

    /** 
     * Processes a .ssc file and returns a list of charts with a meter
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
            String currLine;

            Pattern chartStartPtrn = Pattern.compile("#NOTEDATA:");
            Pattern chartEndPtrn = Pattern.compile("#NOTES:");
            Pattern chartMtrPtrn = Pattern.compile("#METER:");
            Pattern chartDiffPtrn = Pattern.compile("#DIFFICULTY:");
            Pattern chartStylePtrn = Pattern.compile("#CHARTSTYLE:");

            Chart tempChart = new Chart();
            while ((currLine = chartReader.readLine()) != null) {
                
                Matcher chartStart = chartStartPtrn.matcher(currLine);
                Matcher chartEnd = chartEndPtrn.matcher(currLine);
                if (chartStart.find()) {
                    isChart = true;
                }

                if (isChart) {
                    Matcher chartMeter = chartMtrPtrn.matcher(currLine);
                    Matcher chartDiff = chartDiffPtrn.matcher(currLine);
                    Matcher chartStyle = chartStylePtrn.matcher(currLine);
                    
                    if (chartMeter.find()) {
                        int meter = Integer.valueOf(currLine.replaceAll("[^0-9]", ""));
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
                            tempChart.setSpeed(Integer.valueOf(fields[1]));
                            tempChart.setStamina(Integer.valueOf(fields[3]));
                            tempChart.setTechnique(Integer.valueOf(fields[5]));
                            tempChart.setMovement(Integer.valueOf(fields[7]));
                            tempChart.setRhythms(Integer.valueOf(fields[9]));
                            tempChart.setGimmicks(fields[11]);
                        }
                    }
                    if (chartEnd.find()) {
                        if (isChart) {
                            chartList.add(tempChart);
                        }
                        if (tempChart.getPosition() == 0) {
                            tempChart.setPosition(lineNum - 1);
                        }
                        tempChart = new Chart();
                        isChart = false;
                    }
                }
                lineNum++;
            }
            chartReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return chartList;
    }

    /** 
     * Checks to see if there are any ECFA leveled charts (7+) available.
     * If not, exit the program
     * If yes, let the user choose what chart to modify
     * or type 0 to write to the file and exit the program.
     * @param chartList
     */
    public static int listChart(ArrayList<Chart> chartList) {
        int choice;
        
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
        do {
            System.out.print("Choose a chart to modify tech values " +
                             "or choose 0 to save and exit.");
            choice = kb.nextInt();
            if (choice < -1 || choice > chartList.size()) {
                System.out.println("Invalid choice. Please choose again.");
            }
        } while (choice < -1 || choice > chartList.size());
        return choice - 1;
    }

    /**
     * Stores the prompts and values given by the user.
     * @param chartChoice The chart being modified.
     * @param chartList The list of charts given by the .ssc file.
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
     * Prompts the user to input a number for each tech radar category.
     * @param prompt The category name.
     * @return The int given by the user.
     */
    public static int getValue(String prompt) {
        int choice;
        do {
            System.out.print("Enter a number from 1-10 for " + prompt + ": ");
            choice = kb.nextInt();
            if (choice < 1 || choice > 10) {
                System.out.println("Invalid input.");
            }
        } while (choice < 1 || choice > 10);
        return choice;
    }

    /**
     * Prompts the user to select a choice for Gimmicks.
     * @return The choice converted to string given by the user.
     */
    public static String getGimmicks() {
        int choice;
        do {
            System.out.println("Enter a number corresponding to the " +
                             "intensity of gimmicks,");
            System.out.println("0=none, 1=light, 2=medium, 3=heavy, 4=cmod ok");
            choice = kb.nextInt();
            if (choice < 0 || choice > 4) {
                System.out.println("Invalid input.");
            }
        } while (choice < 0 || choice > 4) ;

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
     * Updates the file once a change has been made.
     * @param myFile The file to be overwritten.
     * @param chartList The list of charts given by the file.
     * @throws IOException
     */
    public static void writeToFile(File myFile, ArrayList<Chart> chartList) 
                  throws IOException {
        String content = "";
        BufferedReader read = new BufferedReader(new FileReader(myFile));
        int process = 0;
        int styleLine = chartList.get(0).getPosition();
        int lineCount = 0;
        String nextLine;
        while ((nextLine = read.readLine()) != null) {
            if (lineCount == styleLine) {
                content += "#CHARTSTYLE:" + 
                           chartList.get(process).toString() + ";\n";
                process++;
                if (process < chartList.size()) {
                    styleLine = chartList.get(process).getPosition();
                }
                
            } else {
                content += nextLine + '\n';
            }
            lineCount++;
        }
        read.close();
        FileWriter newFile = new FileWriter(myFile.getName());
        newFile.write(content);
        newFile.close();
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
        int chartChoice;
        do {
            chartChoice = listChart(chartList);
            if (chartChoice > -1) {
                chartList = enterValues(chartList,chartChoice);
                writeToFile(myFile, chartList);
            }
        } while (chartChoice > -1); 
    }
}

