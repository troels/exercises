package org.bifrost;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.Enumeration;
import java.io.IOException;
import java.io.File;
import java.net.URL;
import org.bifrost.wordcounter.WordCounter;

public class WordCounterTest extends TestCase {
    public WordCounterTest() {
	super("WordCounterTest");
    }
    
    public static Test suite() {
	return new TestSuite(WordCounterTest.class);
    }

    /**
     * This is a textgenerator that generates texts with certain number of words and acronyms (randomized)
     * and keeps track of how many words, have been created in the text. 
     * 
     * The output from it, will be compared againts wordcounter to see if there is any unexpected errors.
     */
    public static class TextGenerator {
	final static String lowerCaseLetters = "abcdefghjiklmnopqrstuvwxyz";
	final static String upperCaseLetters = lowerCaseLetters.toUpperCase();
	final static String letters = lowerCaseLetters + upperCaseLetters;
	final static String spaceBetween = " \r\n";
	final Random rng;

	public TextGenerator() {
	    rng = new Random();
	}

	String generateAcronym(int len) {
	    StringBuilder b = new StringBuilder();
	    for (int i = 0; i < len; ++i) {
		if (b.length() != 0) b.append(".");
		b.append(letters.charAt(rng.nextInt(letters.length())));
	    }
	    if (rng.nextBoolean()) {
		b.append(".");
	    }
	    return b.toString();
	}
	
	String generateEnglishLookingWord(int len) { 
	    StringBuilder b = new StringBuilder();
	    for(int i = 0; i < len; ++i) {
		if (b.length() > 0 && rng.nextFloat() < 0.01) {
		    b.append("-");
		}
		b.append(letters.charAt(rng.nextInt(letters.length())));
	    }
	    
	    return b.toString();
	}
	
	List<String> generateWords(int nr) { 
	    ArrayList<String> words = new ArrayList<String>(nr);
	    
	    for(int i = 0; i < nr; ++i) {
		if (rng.nextFloat() < 0.1) { 
		    words.add(generateAcronym(rng.nextInt(2) + 2));
		} else {
		    words.add(generateEnglishLookingWord(rng.nextInt(7) + 1));
		}
	    }
	    return words;
	}
	
	String generateFiller() { 
	    StringBuilder b = new StringBuilder();
	    do {
		b.append(spaceBetween.charAt(rng.nextInt(spaceBetween.length())));
	    } while (rng.nextBoolean());
	    return b.toString();
	}
	 
	static class TextWithWordCounts { 
	    final String text;
	    final List<WordCounter.Word> words;

	    public String getText() { return text; }
	    public List<WordCounter.Word> getWords() { return words; }

	    TextWithWordCounts(String text, List<WordCounter.Word> words) { 
		this.text = text;
		this.words = words;
	    }
	}

	public TextWithWordCounts generateText(int nr_words) { 
	    List<String> words = generateWords(Math.max(nr_words / 20, 1));
	    HashMap<String, Integer> m = new HashMap<String, Integer>();
	    StringBuilder b = new StringBuilder();

	    for (int i = 0; i < nr_words; ++i) {
		String word = words.get(rng.nextInt(words.size()));
		String normalizedWord = word.toLowerCase();
		Integer curCount = m.get(normalizedWord);
		m.put(normalizedWord, curCount == null ? 1 : (curCount + 1));
		b.append(maybeAddLineBreak(word));
		b.append(generateFiller());
	    }
	    
	    ArrayList<WordCounter.Word> al = new ArrayList<WordCounter.Word>(m.size());
	    for(Map.Entry<String, Integer> entry: m.entrySet()) {
		al.add(new WordCounter.Word(entry.getKey(), entry.getValue()));
	    }

	    Collections.sort(al, new WordCounter.WordComparator());

	    return new TextWithWordCounts(b.toString(), al);
	}
	
	String maybeAddLineBreak(String str) { 
	    if (rng.nextFloat() < 0.03 && str.indexOf(".") == -1 && str.length() > 2) {
		int splitPoint = rng.nextInt(str.length() - 2) + 1;
		String s = str.substring(0, splitPoint) + "-" + (rng.nextFloat() < 0.3 ? "\r" : 
								 rng.nextFloat() < 0.5 ? "\n" :  "\r\n") +
		    str.substring(splitPoint);
		return s;
	    } else {
		return str;
	    }
	}
    }
    
    /** 
     * Test ten generated texts with 10000 words each, to see if the Generator and Counter agrees that everything 
     * is peachy.
     */
    public void testGeneratedText() {
	TextGenerator t = new TextGenerator();
	WordCounter wc = new WordCounter();
	for (int i = 0 ; i < 10; ++i) {
	    TextGenerator.TextWithWordCounts twwc = t.generateText(10000);
	    assertEquals(wc.getMostCommonWords(twwc.getText(), 0), twwc.getWords());
	}
    }

    public void testFiles() throws IOException {
	Enumeration elems = getClass().getClassLoader().getResources("wordcounttest/test");
	WordCounter wc = new WordCounter();
	while(elems.hasMoreElements()) {
	    URL u = (URL) elems.nextElement();
	    File f = new File(u.getFile());
	    System.out.println(wc.getMostCommonWordsFromFile(f, "UTF-8"));
	}
    }
};
