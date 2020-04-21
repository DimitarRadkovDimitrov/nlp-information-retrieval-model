import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Program which takes preprocessed (filtered, normalized, stemmed) document file
 * and prints out the corresponding inverted files used for indexing
 */
public class Indexer
{
    public static final String DICTIONARY_OUTPUT_FILE_PATH = "./dictionary.txt";
    public static final String POSTINGS_OUTPUT_FILE_PATH = "./postings.txt";
    public static final String DOCUMENT_ID_OUTPUT_FILE_PATH = "./docids.txt";

    /**
     * Generates stem map containing stems and their corresponding document frequencies as well as
     * the documents in which they appear (posting).
     * Generates list of documents encountered while parsing
     * @param stemMap
     * @param documentIds
     */
    public static void generateInvertedIndex(Map<String, List<Posting>> stemMap, List<String> documentIds)
    {
        long lineCounter = 0;
        int documentCounter = -1;
        String currentDocId = "";
        String currentDocTitle = "";
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext())
        {
            String line = scanner.nextLine();
            lineCounter++;
            
            if (line.startsWith("$DOC"))
            {
                String[] wordStems = line.split(" ");
                currentDocId = wordStems[1];
                documentCounter++;
            }
            else if (line.startsWith("$TITLE"))
            {
                /* Save document titles for documentId list */
                long docLineNumber = lineCounter - 1;
                currentDocTitle = "";
            
                while (scanner.hasNext())
                {
                    String nextTitleLine = scanner.nextLine();
                    if (nextTitleLine.startsWith("$TEXT"))
                    {
                        lineCounter++;
                        documentIds.add(String.format("%s %d %s\n", currentDocId, docLineNumber, currentDocTitle));
                        break;
                    }
                    else
                    {
                        lineCounter++;
                        currentDocTitle += nextTitleLine;
                        currentDocTitle += " ";
                    }
                }
            }
            else
            {
                String[] wordStems = line.split(" ");
                for (String wordStem: wordStems)
                {
                    if (stemMap.containsKey(wordStem))
                    {
                        List<Posting> currentStemPostings = stemMap.get(wordStem);
                        int lastElementIndex = currentStemPostings.size() - 1;
                        Posting lastPosting = currentStemPostings.get(lastElementIndex);
                        
                        if (lastPosting.documentId == documentCounter)
                        {
                            lastPosting.termFrequency += 1;
                        }
                        else
                        {
                            Posting posting = new Posting(documentCounter, 1);
                            currentStemPostings.add(posting);
                        }
                    }
                    else
                    {
                        List<Posting> postings = new ArrayList<>();
                        Posting posting = new Posting(documentCounter, 1);
                        postings.add(posting);
                        stemMap.put(wordStem, postings);
                    }
                }
            }
        }
        scanner.close();
    }

    /**
     * Prints out generated offline lists to files
     * @param stemMap
     * @param documentIds
     */
    public static void printInvertedIndexToFile(Map<String, List<Posting>> stemMap, List<String> documentIds)
    {
        try
        {
            PrintWriter dictionaryPW = new PrintWriter(DICTIONARY_OUTPUT_FILE_PATH, "UTF-8");
            PrintWriter postingsPW = new PrintWriter(POSTINGS_OUTPUT_FILE_PATH, "UTF-8");
            PrintWriter docIdsPW = new PrintWriter(DOCUMENT_ID_OUTPUT_FILE_PATH, "UTF-8");

            int numStems = stemMap.size();
            int numPostingEntries = 0;
            int numDocuments = documentIds.size();
            
            for (List<Posting> posting: stemMap.values())
            {
                numPostingEntries += posting.size();
            }

            dictionaryPW.println(numStems);
            postingsPW.println(numPostingEntries);
            docIdsPW.println(numDocuments);

            for (Entry<String, List<Posting>> entrySet: stemMap.entrySet())
            {
                String stem = entrySet.getKey();
                List<Posting> postings = entrySet.getValue();

                dictionaryPW.println(String.format("%s %d", stem, postings.size()));
                for (Posting posting: postings)
                {
                    postingsPW.println(String.format("%d %d", posting.documentId, posting.termFrequency));
                    numPostingEntries++;
                }
            }

            for (String docId: documentIds)
            {
                docIdsPW.print(docId);
            }

            dictionaryPW.close();
            postingsPW.close();
            docIdsPW.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args)
    {
        Map<String, List<Posting>> stemMap = new TreeMap<>();
        List<String> absDocumentIds = new ArrayList<>();
        generateInvertedIndex(stemMap, absDocumentIds);
        printInvertedIndexToFile(stemMap, absDocumentIds);
    }
}
