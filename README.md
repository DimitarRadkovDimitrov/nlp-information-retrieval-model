# Information Retriever

Uses data preprocessing techniques such as sentence 
splitting, string tokenizing, token normalization, filtering, and
stemming to build an index file based information retrieval model.

<br>

## Build 

* Build in the root directory using makefile.
    ```
    make
    ```

<br>

## Run

* If you need don't already have a split and tokenized version of the collection run the following command to generate the stemmed document.
    ```
    java -cp opennlp-tools-1.9.1.jar:. SentenceSplitter < {INPUT_FILE} | java Scanner | java -cp opennlp-tools-1.9.1.jar:. Preprocessor > {OUTPUT_FILE_STEMMED}
    ```

* If you have a splitted and tokenized version of the collection, simply feed this to the preprocessing program to generate the stemmed version.
    ```
    java -cp opennlp-tools-1.9.1.jar:. Preprocessor < {INPUT_FILE_TOKENIZED} > {OUTPUT_FILE_STEMMED}
    ```

* Run the Indexer program to generate all the Inverted files.
    ```
    java Indexer < {INPUT_FILE_STEMMED}
    ```

* Run the Retriever program with the dictionary, postings, and documentIds files as command line arguments respectively.
    ```
    java -cp opennlp-tools-1.9.1.jar:. Retriever {DICTIONARY_FILE} {POSTINGS_FILE} {DOCIDS_FILE}
    ```

<br>

## Known Limitations

* Programs depend on the $DOC-$TITLE-$TEXT formatting for each document provided as input.

* Little handling for user error

* Similarity values calculated in the Retriever program use a float value. Values will not be as precise and may be equal to similar to other similarity values.

<br>

## Test Plan

* This test plan tests for correctness by examining differences in calculations between program results and results found through empirical testing (by hand). All tests are done using queries specified in 'queries.txt' and the documents in 'documents.txt'.

    * Go through sample queries in 'queries.txt'. Calculate the query vectors by hand and compare scores outputed by program. Do the same for documents matched in the dictionary. Differences between the two should be minimal (nearest hundredth or so).

    * Execute sample queries. See if top ranked documents in search results match those expected. Expected documents are considered based on term frequency and topic of document when doing manual search through 'documents.txt' file.

    (1) Appears exactly in document<br>
        Input Query: 'timber'<br>
        Expected: '$DOC LA010190-0071, '$DOC LA010990-0122'

    (2) Does not appear in any document<br>
        Input Query: 'garfield'<br>
        Expected: ''

    (3) Multiple contexts - shoot - general<br>
        Input: 'shoot'<br>
        Expected: 'LA011090-0130, LA011190-0202, LA010690-0130, ...'

    (4) Multiple contexts - shoot - refined (basketball)<br>
        Input: 'shoot basketball'<br>
        Expected: 'LA011090-0130, LA010390-0118, LA011190-0202, ...'

    (5) Multiple contexts - shoot - refined (gun)<br>
        Input: 'shoot gun'<br>
        Expected: 'LA011190-0202, LA011090-0130, LA010490-0126, ...'

