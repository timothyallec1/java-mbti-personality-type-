
// created by TIMOTHY_ALLEC on 10/24/22
//Discussed with: Vincent, Phinehas, Kai, Elijah,

import java.io.*;
import java.util.*;

public class Personality {
    public static final int TYPESIZE = 4;
    public static final String[][] TYPEOPTIONS = {{"E", "I"}, {"S", "N"}, {"T", "F"}, {"J", "P"}};
    public static String MESSAGE_PREFIX = "This program processes a file of answers to the\n" +
            "Keirsey Temperament Sorter. It converts the\n" +
            "various A and B answers for each person into\n" +
            "a sequence of B-percentages and then into a\n" +
            "four-letter personality type.";


    public static void main(String[] args) throws IOException {
        outputToFile();
    }

    public static void outputToFile() throws IOException{
        // creates scanner for input
        Scanner console = new Scanner(System.in);
        System.out.println(MESSAGE_PREFIX);
        // this scanner imports data from the input file
        System.out.print("input file name? ");
        Scanner input = new Scanner(new File(console.next()));
        // this will write to a specified output file
        System.out.print("output file name? ");
        // creates a print stream for the new created file


        PrintStream output = new PrintStream(new File(console.next()));

        while (input.hasNextLine()) {
            // print name to the output file
            output.print(input.nextLine() + ": ");
            String line = input.next();
            // creating an array to store the personality type
            String[] personalityType = new String[4];
            // calling a method to fill the new array
            personalityType = getLineAnswers(line);
            int[] compute = countAnswers(personalityType);
            int[] bPercentages = calculateB(compute);
            output.println(Arrays.toString(bPercentages) + " = " + findType(bPercentages));
            if (input.hasNextLine()) {
                input.nextLine();
            }
        }
        output.close();
        console.close();
    }

    // calculates the percentages of B answers for each character
    public static int[] calculateB(int[] newArray) {
        int[] bPercent = new int[TYPESIZE];
        for (int i = 0; i < TYPESIZE; i++) {
            bPercent[i] = (int) Math.round((double) newArray[(i * 2) + 1] / (newArray[i * 2] + newArray[(i * 2) + 1]) * 100);
        }
        return bPercent;
    }

    /* method to determine the letters for each personality section {{"E", "I"}, {"S", "N"}, {"T", "F"}, {"J", "P"}};
    above 50% Bpercentage will return the second letter
    below 50% will return the first letter
     */
    public static String findType(int[] bPercentages) {
        String personalityLetter = "";
        for (int i = 0; i < TYPESIZE; i++) {
            if (bPercentages[i] < 50) {
                personalityLetter = personalityLetter + TYPEOPTIONS[i][0];
            } else if (bPercentages[i] > 50) {
                personalityLetter = personalityLetter + TYPEOPTIONS[i][1];
            } else {
                personalityLetter = personalityLetter + "X";
            }
        }
        return personalityLetter;
    }

    // method to get the answers to each of the individual question secitons
    public static String[] getLineAnswers(String line) {
        String[] outputArray = new String[4];
        int j = 0;
        for (int i = 0; i < outputArray.length; i++) {
            outputArray[i] = "";
        }
        for (int i = 0; i < 70; i++) {
            // if statement to reset j when one cycle is complete (the end of each character's answers)
            if (j > 6) {
                j = 0;
            }
            // index to get the correct index in the personality type array (ranges from 1 to 4)
            int arrayIndex = (int) Math.round((j + 1) / 2);
            // add the corresponding letter to the correct slot in the array
            outputArray[arrayIndex] = outputArray[arrayIndex] + Character.toUpperCase(line.charAt(i));
            j++;
        }
        return outputArray;
    }

    // method to count the number of A's and B's in each of the individual sections
    public static int[] countAnswers(String[] inputArray) {
        int[] outputArray = new int[8];
        int index = 0;
        for (int i = 0; i < inputArray.length; i++) {
            for (int j = 0; j < inputArray[i].length(); j++) {
                if (inputArray[i].charAt(j) == 'A') {
                    outputArray[index] = outputArray[index] + 1;
                } else if (inputArray[i].charAt(j) == 'B') {
                    outputArray[index + 1] = outputArray[index + 1] + 1;
                }
            }
            index = index + 2;
        }
        return outputArray;
    }
}