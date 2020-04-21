USER=dimitar
JAVAC=javac
CLASSPATH=opennlp-tools-1.9.1.jar:.
#JFLEX=jflex
JFLEX=/Users/Dimitar/jflex-1.7.0/bin/jflex

all: SentenceSplitter.class Token.class Lexer.class Scanner.class Preprocessor.class Indexer.class Retriever.class Posting.class Dictionary.class DocumentId.class SimilarityScore.class

%.class: %.java
	$(JAVAC) -cp $(CLASSPATH) $^

Lexer.java: article.flex
	$(JFLEX) article.flex

clean:
	rm -f Lexer.java *.class dictionary.txt postings.txt docids.txt *~

tar:
	tar -czvf $(USER)_a2.tar.gz Makefile README documents_small.txt article.flex stopwords.txt opennlp-tools-1.9.1.jar OpenNLP_models SentenceSplitter.java Token.java Scanner.java Preprocessor.java Indexer.java Retriever.java Posting.java Dictionary.java DocumentId.java SimilarityScore.java 
