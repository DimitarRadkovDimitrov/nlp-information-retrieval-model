import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Program used to retrieve relevant documents given a user-defined
 * query
 */
public class Retriever
{
    /**
     * Loads dictionary index file into static array for fast indexing
     * @param dictionary
     * @param dictionaryFilePath
     * @return
     * @throws Exception
     */
    public static Dictionary[] loadDictionaryFile(Dictionary[] dictionary, String dictionaryFilePath) throws Exception
    {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(dictionaryFilePath));
        int dictionarySize = Integer.parseInt(bufferedReader.readLine());
        dictionary = new Dictionary[dictionarySize];
    
        int i = 0;
        int frequencySum = 0;
        String line = bufferedReader.readLine();

        while (line != null)
        {
            String[] tokens = line.split(" ");
            dictionary[i] = new Dictionary(tokens[0], frequencySum);
            frequencySum += Integer.parseInt(tokens[1]);
            i++;
            line = bufferedReader.readLine();
        }

        bufferedReader.close();
        return dictionary;
    }

    /**
     * Loads postings index file into static array for fast indexing
     * @param postings
     * @param postingsFilePath
     * @return
     * @throws Exception
     */
    public static Posting[] loadPostingsFile(Posting[] postings, String postingsFilePath) throws Exception
    {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(postingsFilePath));
        int postingsSize = Integer.parseInt(bufferedReader.readLine());
        postings = new Posting[postingsSize];
    
        int i = 0;
        String line = bufferedReader.readLine();
        while (line != null)
        {
            String[] tokens = line.split(" ");
            postings[i] = new Posting(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
            i++;
            line = bufferedReader.readLine();
        }

        bufferedReader.close();
        return postings;
    }

    /**
     * Loads documentId index file into static array for fast indexing
     * @param documentIds
     * @param docIdsFIlePath
     * @return
     * @throws Exception
     */
    public static DocumentId[] loadDocumentIdsFile(DocumentId[] documentIds, String docIdsFIlePath) throws Exception
    {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(docIdsFIlePath));
        int documentIdsSize = Integer.parseInt(bufferedReader.readLine());
        documentIds = new DocumentId[documentIdsSize];
    
        int i = 0;
        String line = bufferedReader.readLine();
        while (line != null)
        {
            String[] tokens = line.split(" ", 3);
            documentIds[i] = new DocumentId(tokens[0], tokens[2], Integer.parseInt(tokens[1]));
            i++;
            line = bufferedReader.readLine();
        }

        bufferedReader.close();
        return documentIds;
    }

    /**
     * Given a list of strings returns a map containing the term and the number of times 
     * it appears in the given list
     * @param queryTerms
     * @return
     */
    public static Map<String, Integer> getQueryTermFrequencyMap(String[] queryTerms)
    {
        Map<String, Integer> termFrequencyMap = new HashMap<>();

        for (String term: queryTerms)
        {
            if (termFrequencyMap.containsKey(term))
            {
                termFrequencyMap.put(term, termFrequencyMap.get(term) + 1);
            }
            else
            {
                termFrequencyMap.put(term, 1);
            }
        }
        return termFrequencyMap;
    }

    /**
     * Uses index arrays to calculate term weights for query terms and document terms. 
     * Returns sorted list of documents most relevant to query in ascending order
     */
    public static SimilarityScore[] calculateDocumentRankings(Dictionary[] dictionary, Posting[] postings, Map<String, Integer> queryTermFrequencyMap, int collectionSize)
    {
        SimilarityScore[] similarityScores = new SimilarityScore[collectionSize];

        for (int i = 0; i < collectionSize; i++)
        {
            similarityScores[i] = new SimilarityScore(0, i);
        }

        for (Map.Entry<String, Integer> entrySet: queryTermFrequencyMap.entrySet())
        {            
            String term = entrySet.getKey();
            int queryTermFrequency = entrySet.getValue();
            int documentFrequency = 0;
            float queryTermWeight = 0;
            float invertedDocumentFreq = 0;
            int termDictionaryIndex = searchDictionaryForTerm(dictionary, term);

            if (termDictionaryIndex != -1)
            {
                int postingsArrayOffset = dictionary[termDictionaryIndex].offset;

                if (termDictionaryIndex != dictionary.length - 1)
                {
                    documentFrequency = dictionary[termDictionaryIndex + 1].offset - postingsArrayOffset;
                }
                else
                {
                    documentFrequency = dictionary.length - postingsArrayOffset;
                }
    
                invertedDocumentFreq = (float) (Math.log10((collectionSize * 1.0) / documentFrequency) / Math.log10(2));
                queryTermWeight = queryTermFrequency * invertedDocumentFreq;

                for (int i = postingsArrayOffset; i < postingsArrayOffset + documentFrequency; i++)
                {
                    int documentId = postings[i].documentId;
                    float documentTermWeight = postings[i].termFrequency * invertedDocumentFreq;
                    similarityScores[documentId].score += (documentTermWeight * queryTermWeight);
                }
            }
        }
        
        Arrays.sort(similarityScores);
        return similarityScores;
    }

    /**
     * Performs binary search on dictionary array to find the index for a given 
     * term
     * @param dictionary
     * @param term
     * @return
     */
    public static int searchDictionaryForTerm(Dictionary[] dictionary, String term)
    {
        int low = 0;
        int high = dictionary.length - 1;

        while (low <= high)
        {
            int mid = (low + high) / 2;
            int compare = term.compareTo(dictionary[mid].key);

            if (compare < 0)
            {
                high = mid - 1;
            }
            else if (compare > 0)
            {
                low = mid + 1;
            }
            else
            {
                return mid;
            }
        }
        return -1;
    }

    /**
     * Returns the top 10 documents that have a positive similarity score given a sorted
     * list of document similarity scores
     * @param documentIds
     * @param documentSimilarityScores
     * @return
     */
    public static List<String> retrieveRelevantDocuments(DocumentId[] documentIds, SimilarityScore[] documentSimilarityScores)
    {
        List<String> results = new ArrayList<>();

        for (int i = documentSimilarityScores.length - 1; i >= documentSimilarityScores.length - 10; i--)
        {
            if (documentSimilarityScores[i] == null || documentSimilarityScores[i].score == 0.0)
            {
                break;
            }
            
            int documentIndex = documentSimilarityScores[i].documentId;
            results.add(documentSimilarityScores[i].score + " " + documentIds[documentIndex].toString());
        }
        return results;
    }

    public static void main(String[] args)
    {
        if (args.length < 3)
        {
            System.out.println("usage: java Retriever <dictionary_file> <postings_file> <docIds_file>");
            System.exit(0);
        }

        Dictionary[] dictionary = null;
        Posting[] postings = null;
        DocumentId[] documentIds = null;
        Scanner scanner = null;

        try
        {
            dictionary = loadDictionaryFile(dictionary, args[0]);
            postings = loadPostingsFile(postings, args[1]);
            documentIds = loadDocumentIdsFile(documentIds, args[2]);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        
        scanner = new Scanner(System.in);

        while (true)
        {
            System.out.println("Enter a search query:");
            String inputLine = scanner.nextLine();
            System.out.println();

            if (!inputLine.equalsIgnoreCase("quit") && !inputLine.equalsIgnoreCase("q"))
            {
                String[] normalizedQueryTerms = inputLine.split(" ");
                for (int i = 0; i < normalizedQueryTerms.length; i++)
                {
                    normalizedQueryTerms[i] = Preprocessor.normalizeToken(normalizedQueryTerms[i]);
                }

                Map<String, Integer> queryTermFrequencyMap = getQueryTermFrequencyMap(normalizedQueryTerms);
                SimilarityScore[] documentSimilarityScores = calculateDocumentRankings(dictionary, postings, queryTermFrequencyMap, documentIds.length);
                List<String> matchedDocuments = retrieveRelevantDocuments(documentIds, documentSimilarityScores);
                
                System.out.printf("Query matched %d documents:\n", matchedDocuments.size());
                System.out.println("--------------------------");
                for (String document: matchedDocuments)
                {
                    System.out.println(document);
                }
                System.out.println();
            }
            else
            {
                break;
            }
        }
    }
}

