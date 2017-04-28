package com.rabidgremlin.mutters.core;


import com.rabidgremlin.mutters.util.NumberConversionUtility;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This utility class cleans a user's input for processing.
 * 
 * @author rabidgremlin
 *
 */
public final class InputCleaner
{

  /* Logger. */
  private static Logger log = LoggerFactory.getLogger(InputCleaner.class);

  /**
   * Private constrcutor for utility class.
   * 
   */
  private InputCleaner()
  {
  }


  /**
   * This method takes an utterance string and returns chunked input.
   *
   * @param text The input string.
   * @return The chunked input.
   */
  public static ArrayList<String> chunkInput(String text)
  {
    ArrayList<String> clauses = new ArrayList<>();
    ArrayList<Integer> count = new ArrayList<>();    // Number of intents

    text = parseVoiceToText(text);

    //Conjunctions
    String sep[] = {" after ", " although ", " because ", " before ", " but ",
            " even if ", " even though ", " in case ",
            " lest ", " for ",  " nor ", " or "," yet ",
            " once ",
            " since ",
            " than ", " that ", " though ", " till ",
            " unless ", " until ",
            " when ", " whenever ", " where ", " wherever ", " while ", " and ", "."};

    // look for separators
    for (int x = 0; x < sep.length; x++) {
      String word = sep[x];
      for (int i = -1; (i = text.indexOf(word, i + 1)) != -1; ) {
        System.out.println(i + "   " + word);
        count.add(i);
      }
    }

    // extract each clause
    count.add(text.length());
    Collections.sort(count);
    int last = 0;
    for (int i = 0; i < count.size(); i++) {
      //System.out.print("   "+count[i]);
      clauses.add(text.substring(last, count.get(i)));
      //System.out.println(text.substring(last,count[i]));

      last = count.get(i);
    }

    return clauses;
  }

  public static String parseVoiceToText(String inputString)
  {
    // change 'X a. x. t. Y' -> 'X axt Y'
    String parsedInput = inputString.replaceAll("(?:(?<=\\s\\w)(\\.\\s*)(?=\\w\\.)|(?<=\\s\\w)(\\.)(?=\\s*\\w))", "");

    // change 'one two three' -> '123'
    try {
       NumberConversionUtility numberConversion = new NumberConversionUtility();
       parsedInput = numberConversion.replaceWordsWithNumbers(parsedInput);

    } catch (Exception ex) {
      log.warn("Replace words with numbers failed on '" + parsedInput + "'");
    }

    // System.out.println("INPUT (AFTER VOICE TO TEXT): " + parsedInput);

    return parsedInput;
  }

  /**
   * This method takes an utterance string and returns cleaned input.
   * 
   * @param inputString The input string.
   * @return The cleaned input.
   */
  public static CleanedInput cleanInput(String inputString)
  {
    // String[] inputs = {inputString.trim().replaceAll("[\\?|!|,]*", "")};

    String parsedInput = inputString.toLowerCase().trim().replaceAll("[\\?|!']*", "");
    parsedInput = parsedInput.replaceAll("(?:--|[\\[\\]{}()+/,.\\\\])", "");


    // List<String> originalTokens = Arrays.asList((inputString.trim().replaceAll("[\\?|!|,]*", "").split("\\s+")));
    List<String> originalTokens = Arrays.asList(WhitespaceTokenizer.INSTANCE.tokenize(parsedInput));
    List<String> cleanedTokens = new ArrayList<String>();

    // Begin by importing documents from text to feature sequences
    /*ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

    // Pipes: lowercase, tokenize, remove stopwords, map to features
    pipeList.add( new CharSequenceLowercase() );
    pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
    pipeList.add( new TokenSequenceRemoveStopwords(false);
    //pipeList.add( new TokenSequenceRemoveStopwords(new File("stoplists/en.txt"), "UTF-8", false, false, false) );
    //pipeList.add( new TokenSequence2FeatureSequence() );

    InstanceList tokenize = new InstanceList(new SerialPipes(pipeList));
    tokenize.addThruPipe(new StringArrayIterator(inputs));
    InstanceList instances = new InstanceList (new SerialPipes(pipeList));*/

    for (String token : originalTokens)
    {
      cleanedTokens.add(token);
    }

    CleanedInput cleanedInput = new CleanedInput(originalTokens, cleanedTokens);

   // log.debug("Cleaned Input: {} -> {}", inputString, cleanedInput);

    return cleanedInput;
  }
}
