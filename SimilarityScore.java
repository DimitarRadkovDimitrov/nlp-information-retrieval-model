/**
 * Similarity score DTO with floating point score value and 
 * matching document identifier
 */
public class SimilarityScore implements Comparable<SimilarityScore>
{
    Float score;
    int documentId;

    public SimilarityScore(float score, int documentId)
    {
        this.score = score;
        this.documentId = documentId;
    }

    public int compareTo(SimilarityScore similarityScore)
    {
        return this.score.compareTo(similarityScore.score);
    }
}