/**
 * Posting DTO with document where term appears and the number
 * of times it appears in said document
 */
public class Posting
{
    int documentId;
    int termFrequency;

    public Posting(int documentId, int termFrequency)
    {
        this.documentId = documentId;
        this.termFrequency = termFrequency;
    }
}