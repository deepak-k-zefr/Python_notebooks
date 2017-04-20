package com.allstate.allie.allstatebot.slots.SLOTS;
import com.allstate.allie.allstatebot.AllstateBot;
import com.rabidgremlin.mutters.core.*;
import com.rabidgremlin.mutters.templated.MultiSlotMatch;
import edu.mit.ll.mitie.EntityMention;
import edu.mit.ll.mitie.EntityMentionVector;
import edu.mit.ll.mitie.NamedEntityExtractor;
import edu.mit.ll.mitie.StringVector;
import opennlp.tools.ngram.NGramGenerator;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by dualpahse_ai on 3/14/17.
 */
public class ValidateTemplateIntents {
    private HashMap<Slot, SlotMatch> slotMatches;


    //Get all Files
    public File[] GetFiles(){
        //Files
        File f = null;
        File folder = new File("/Users/dualpahse_ai/allie-conversational-ai/src/main/resources/models/test/");
        File[] listOfFiles = folder.listFiles();

        return listOfFiles;
    }




    public static void main(String[] args) throws IOException {
        {

            ValidateTemplateIntents obj=new ValidateTemplateIntents();

            // Write test results
            File fout = new File("test_names.txt");
            FileOutputStream fos = new FileOutputStream(fout);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            // For MITIE name extractor
            NamedEntityExtractor ner = new NamedEntityExtractor("/Users/dualpahse_ai/Documents/NLP/MITIE/examples/python/new_ner_model.dat");
            StringVector test = new StringVector();

            //Score Hashmap
            HashMap<String, Double> score = new HashMap<String, Double>();

            //Get all Files
            File[] listOfFiles=obj.GetFiles();
            InputStream inStream = null;



            AllstateBot commonBot = new AllstateBot();

            //Loop through all the intents
            for (int i = 0; i < commonBot.getIntentNames().length; i++) {
                String chosen_intent = (commonBot.getIntentNames()[i].toString()); //  Returns all templated group intents
                score.put(chosen_intent, 0.0);
            }



            // Loop through all files
            for (File file : listOfFiles) {


                if (file.isFile() && !file.getName().equals(".DS_Store")) {
                    String file_name = file.getName().toString();
                    String path = combine("models/test/", file_name);
                    String fileNameWithOutExt = FilenameUtils.removeExtension(file_name);
                    inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream
                            (path);

                    String allWords = IOUtils.toString(inStream, "UTF-8").replace('\uFEFF', ' ');
                    String[] tokens = allWords.split("\n");
                    String token = " ";
                    System.out.println("\n\n" + fileNameWithOutExt + "\n");


                        //Split token into utterance and entity
                        for (int count = 0; count < tokens.length; count++) {
                            token = tokens[count];

                            for (int i = 0; i < commonBot.getIntentNames().length; i++) {
                                String chosen_intent = (commonBot.getIntentNames()[i].toString()); //  Returns all templated group intents

                                if (chosen_intent.equals(fileNameWithOutExt)) {

                                    String[] utterance = token.split("===");
                                    String correct = utterance[0].toLowerCase();
                                    token = utterance[1].toLowerCase();

                                    Intent intent = commonBot.getIntent(chosen_intent); // return an intent
                                    CleanedInput cleanedUtterance = InputCleaner.cleanInput(token); // clean the input
                                    MultiSlotMatch matches = intent.matches(cleanedUtterance, null, true);

                                    if ((matches != null && matches.isMatched())) {

                                        if (fileNameWithOutExt.equals("CGaveName")) {
                                        String name="";

                                             name=(String)matches.getSlotMatch("slot_name").getValue();

                                            if (name.equals(correct)) {
                                                score.replace(chosen_intent, score.get(chosen_intent) + 1);

                                                System.out.println("PASS " + token + "  ( " + name + ")   " + correct);//   "+"ENTITY     "+correct+"    ");
                                                // bw.write("PASS "+match.getValue());//   "+"ENTITY     "+correct+"    ");
                                            }
                                            else {
                                                System.out.println("FAIL" + token + "   ("+ name + ")   " + correct);//   "+"ENTITY     "+correct+"    ");
//                                                bw.write("FAIL " + token + "   " + match.getValue());//   "+"ENTITY     "+correct+"    ");
//                                                bw.newLine();

                                            }

                                            }
                                            else {

                                            for (SlotMatch match : matches.getSlotMatches().values()) {

                                                if (match.getValue().toString().equals(correct)) {
                                                    score.replace(chosen_intent, score.get(chosen_intent) + 1);
                                                    System.out.println("PASS " + token + "  ( " + match.getValue() + ")   " + correct);//   "+"ENTITY     "+correct+"    ");
                                                    // bw.write("PASS "+match.getValue());//   "+"ENTITY     "+correct+"    ");
                                                }
                                                else {
                                                    System.out.println("FAIL" + token + "   (" + match.getValue() + ")   " + correct);//   "+"ENTITY     "+correct+"    ");
//                                                bw.write("FAIL " + token + "   " + match.getValue());//   "+"ENTITY     "+correct+"    ");
//                                                bw.newLine();

                                                }
//
//                                                // Format Phone Number
//                                                if (chosen_intent.equals("CGavePhoneNumber")) {
//                                                    String Ph = (match.getValue().toString().replaceAll("\\D+", ""));
//                                                    System.out.println("(" + Ph.substring(0, 3) + ")" + "-" + Ph.substring(4, 7) + "-" + Ph.substring(7, 10));
//                                                }

                                            }
                                        }
                                    }
                                    }

                                }
                            }

                        //bw.newLine();
                    }
                }
                System.out.println("\n\n" + Arrays.asList(score)); // method 1
                bw.close();

            }


        }




    public static String combine (String path1, String path2)
    {
        File file1 = new File(path1);
        File file2 = new File(file1, path2);
        return file2.getPath();
    }

}





