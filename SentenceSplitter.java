import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;

import opennlp.tools.sentdetect.SentenceDetectorME; 
import opennlp.tools.sentdetect.SentenceModel;  

/**
 * Program used to split lines from $DOC-$TITLE-$TEXT formatted input to lines corresponding
 * to english sentences
 */
class SentenceSplitter
{
    public static final String ENGLISH_SENTENCE_MODEL_LOCATION = "./OpenNLP_models/en-sent.bin";
    public static final int MINIMUM_SIZE_TAG = 5;

    /**
     * Takes $DOC-$TITLE-$TEXT formatted input from STDIN and prints each sentence
     * from document on a new line
     * @param sentenceDetector
     */
    public static void printFile(SentenceDetectorME sentenceDetector)
    {
        StringBuilder fileAsString = new StringBuilder();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext())
        {
            String line = scanner.nextLine();
            if (line.startsWith("$DOC") || line.startsWith("$TEXT"))
            {
                printSentences(sentenceDetector, fileAsString.toString());
                fileAsString.setLength(0);
                System.out.println(String.format("%s", line));
            }
            else if (line.startsWith("$TITLE"))
            {
                System.out.println(String.format("%s", line));
            }
            else
            {
                fileAsString.append(line + " ");
            }
        }
        scanner.close();
        printSentences(sentenceDetector, fileAsString.toString());
    }

    /**
     * Given a string of characters returns a list of english sentences
     * @param sentenceDetector
     * @param content
     */
    public static void printSentences(SentenceDetectorME sentenceDetector, String content)
    {
        String[] sentences = sentenceDetector.sentDetect(content);
        for (String sentence: sentences)
        {
            System.out.println(sentence);
        }
    }

    public static void main(String[] args)
    {
        try (InputStream englishSentenceModel = new FileInputStream(SentenceSplitter.ENGLISH_SENTENCE_MODEL_LOCATION)) 
        {
            SentenceModel sentenceModel = new SentenceModel(englishSentenceModel);
            SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentenceModel);
            printFile(sentenceDetector);
        }
        catch(Exception e)
        {
            System.out.println("Unexpected exception:");
            e.printStackTrace();
        }
    }
}