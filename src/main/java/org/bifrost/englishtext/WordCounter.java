package org.bifrost.EnglishTextStatistics;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Collections;

class WordCounter { 
    /**
     * DEFAULT_WORD_REGEX matches words starting with a letter and
     * consisting of consecutive letters and dashes (sometimes
     * followed by a single newline*) and acronyms starting with a
     * single letter and interleaved (and perhaps ended) with a single
     * dot.
     *
     * * Newline is defined as a carriage return or a newline character or both in that order.
     * 
     */
    private static final Pattern DEFAULT_WORD_REGEX = 
	Pattern.compile("\\p{Alpha} (?: \\. \\p{Alpha} )* \\.? |
                         \\p{Alpha} (?: \\p{Alpha} | - (\r ? \n ? ))*".replaceAll("\\s+", ""));
    
    private final Pattern wordRegex;
    
    /** 
     * Default constructor. Creates a wordCounter using the DEFAULT_WORD_REGEX
     */
    public WordCounter() { 
	this(DEFAULT_WORD_REGEX);
    }
    
    /**
     * Constructor taking a custom regex, that will be used for finding words.
     * The regex must never match the empty string, as that can create an infinite loop.
     */
    public WordCounter(Pattern wordRegex ) { 
	this.wordRegex = wordregex;
    }
    
    /**
     * Takes a string, searches for words matching wordRegex, and puts them in a list and sorts the list.
     * 
     * Has an O(nlogn)-asymptote and could be made O(n) (by bucketsort) 
     * if desired, but the implementation is a bit simpler and for small texts perhaps even faster now.
     * 
     * @param text The text to be parsed
     * 
     * @return A list of all words in sorted order.
     */
    private List<Word> createWordList(String text) { 
	Matcher m = wordRegex.matcher(text);

	// Numbers are not scientifically determined. 
	final int AVG_WORD_SIZE = 5; final int AVG_NUMBER_OF_TIMES_PER_WORD = 2; final int FUZZ_FACTOR = 2;
	final int hash_table_size = FUZZ_FACTOR * text.length / (AVG_WORD_SIZE * AVG_NUMBER_OF_TIMES_PER_WORD);
	
	HashMap<String, Integer> map = new HashMap<String, Integer>(hash_table_size);

	// Find all words and put them in hashmap
	while(m.find()) {
	    String match = m.group().toLowerCase();
	    Integer oldval = map.get(match);
	    if (oldval == null) {
		map.put(match, 1);
	    } else {
		map.put(match, oldval + 1);
	    }
	}
	
	// Convert words to Wordclass and put them in a list.
	ArrayList<Word> res = new ArrayList<Word>(map.size());
	for(Map.Entry<String, Int> entry: map.entrySet()) {
	    res.add(new Word(entry.getKey(), entry.getValue()));
	}

	// Sort the list
	Collections.sort(res, new Comparator() { 
		@Override
		public int compare(Object o1, Object o2) { 
		    Word w1 = (Word) o1; Word w2 = (Word) o2;
		    return w1.getOccurence() < w2.getOccurence() ? -1 : 
			   w1.getOccurence() > w2.getOccurence() ? 1 : 0;
		}
	    });

	return res;
    }
    
    /**
     * Class for keeping track of words found 
     */
    public static final class Word {
	private final String name;
	private int occurence;

	public Word(String name, int occurence) {
	    this.name = name; this.occurence = nr_of_times;
	}
	
	public String getName() { return name; } 
	public int getOccurence() { return occurence; }
	
	public int addOccurence() { 
	    occurence += 1;
	}

	@Override 
	boolean equals(Object o) {
	    if (!(o instanceof Word)) return false;
	    Word w = (Word) o;
	    return w.getName() == getName();
	}

	@Override 
	int hashCode() {
	    return 37 * getName().hashCode;
	}
    }

    /**
     * Returns the nrWords most common words found in text 
     *
     * @param text, The text to search after words.
     * @param nrWords, max number of words to be found, if zero or negative return all words, 
     * no matter how many they are.
     *
     * @return List of words found in order of most common first. 
     * If there is less than nrWords distinct words or nrWords is zero or negative,
     * all words found will be returned.
     */
    public List<Word> getMostCommonWords(String text, int nrWords) {
	ArrayList<Word> lst = createWordList(text);
	
	if(nrWords <= 0 || nrWords >= lst.size()) return lst;
	return lst.subList(0, nrWords);
    }

    /**
     * Return the 10 most common words found in text.
     * 
     * @param text text to search
     * 
     * @return 10 most common words (or all words if fewer than ten distinct words)
     *
     */
    public List<Word> getMostCommonWords(String text) { 
	return getMostCommonWords(text, 10);
    }

    /**
     * Reads file f using encoding encoding and finds the nrWords most common words,
     * under the constraints mentioned in getMostCommonWords(String text, int nrWords);
     * 
     * @param f file to search
     * @param encoding encoding file is presumed to be in
     * @param nrWords The max number of words to return
     *
     * @return the result of getMostCommonWords on the text of file
     */
    public List<Word> getMostCommonWordsFromFile(File f, String encoding, int nrWords) { 
	String text = FileUtils.readFileToString(f, encoding);
	return getMostCommonWords(text, nrWords);
    }

    /**
     * Reads file f using encoding encoding and finds the nrWords most common words,
     * under the constraints mentioned in getMostCommonWords(String text, int nrWords);
     * 
     * @param f file to search
     * @param encoding encoding file is presumed to be in
     * @param nrWords The max number of words to return
     *
     * @return the result of getMostCommonWords on the text of file
     */
    public List<Word> getMostCommonWordsFromFile(File f, String encoding) {
	String text = FileUtils.readFileToString(f, encoding);
	return getMostCommonWords(text);
    }
};
	