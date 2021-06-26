/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package textsimilarity.chunker2matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import opennlp.tools.textsimilarity.LemmaPair;
import opennlp.tools.textsimilarity.ParseTreeChunk;
import opennlp.tools.textsimilarity.ParseTreeMatcherDeterministic;
import opennlp.tools.textsimilarity.SentencePairMatchResult;
import opennlp.tools.textsimilarity.TextProcessor;
//import opennlp.tools.textsimilarity.chunker2matcher.ParserChunker2MatcherProcessor;
//import opennlp.tools.textsimilarity.chunker2matcher.SentenceNode;

public class ParserPure2MatcherProcessor extends ParserChunker2MatcherProcessor {
  protected static ParserPure2MatcherProcessor pinstance;
  private static Logger LOG = Logger
      .getLogger("opennlp.tools.textsimilarity.chunker2matcher.ParserPure2MatcherProcessor");

  public synchronized static ParserPure2MatcherProcessor getInstance() {
    if (pinstance == null)
      pinstance = new ParserPure2MatcherProcessor();

    return pinstance;
  }

  private ParserPure2MatcherProcessor() {
    initializeSentenceDetector();
    initializeTokenizer();
    initializePosTagger();
    initializeParser();
  }

  public synchronized List<List<ParseTreeChunk>> formGroupedPhrasesFromChunksForSentence(
      String sentence) {
    if (sentence == null || sentence.trim().length() < MIN_SENTENCE_LENGTH)
      return null;

    sentence = TextProcessor.removePunctuation(sentence);
    SentenceNode node = parseSentenceNode(sentence);
    if (node == null) {
      LOG.info("Problem parsing sentence '" + sentence);
      return null;
    }
    List<ParseTreeChunk> ptcList = node.getParseTreeChunkList();
    List<String> POSlist = node.getOrderedPOSList();
    List<String> TokList = node.getOrderedLemmaList();

    List<List<ParseTreeChunk>> listOfChunks = new ArrayList<List<ParseTreeChunk>>();
    List<ParseTreeChunk> nounPhr = new ArrayList<ParseTreeChunk>(), prepPhr = new ArrayList<ParseTreeChunk>(), verbPhr = new ArrayList<ParseTreeChunk>(), adjPhr = new ArrayList<ParseTreeChunk>(),
    // to store the whole sentence
    wholeSentence = new ArrayList<ParseTreeChunk>();

    wholeSentence.add(new ParseTreeChunk("SENTENCE", TokList, POSlist));
    for (ParseTreeChunk phr : ptcList) {
      String phrType = phr.getMainPOS();
      if (phrType.startsWith("NP")) {
        nounPhr.add(phr);
      } else if (phrType.startsWith("VP")) {
        verbPhr.add(phr);
      } else if (phrType.startsWith("PP")) {
        prepPhr.add(phr);
      } else if (phrType.endsWith("ADJP")) {
        adjPhr.add(phr);
      } else {
        // LOG.info("Unexpected phrase type found :"+ phr);
      }

    }

    listOfChunks.add(nounPhr);
    listOfChunks.add(verbPhr);
    listOfChunks.add(prepPhr);
    listOfChunks.add(adjPhr);
    listOfChunks.add(wholeSentence);

    return listOfChunks;
  }

  public SentencePairMatchResult assessRelevance(String para1, String para2) {

    List<List<ParseTreeChunk>> sent1GrpLst = formGroupedPhrasesFromChunksForPara(para1), sent2GrpLst = formGroupedPhrasesFromChunksForPara(para2);

    List<LemmaPair> origChunks1 = listListParseTreeChunk2ListLemmaPairs(sent1GrpLst); // TODO
                                                                                      // need
                                                                                      // to
                                                                                      // populate
                                                                                      // it!

    ParseTreeMatcherDeterministic md = new ParseTreeMatcherDeterministic();
    List<List<ParseTreeChunk>> res = md
        .matchTwoSentencesGroupedChunksDeterministic(sent1GrpLst, sent2GrpLst);
    return new SentencePairMatchResult(res, origChunks1);

  }

  public static void main(String[] args) throws Exception {
    ParserPure2MatcherProcessor parser = ParserPure2MatcherProcessor
        .getInstance();
    String text = "Its classy design and the Mercedes name make it a very cool vehicle to drive. ";

    //List<List<ParseTreeChunk>> res = parser
      //  .formGroupedPhrasesFromChunksForPara(text);
    //System.out.println(res);

    // System.exit(0);

    String phrase1 = "I use the 2018 iPad Pro 12 for 8-12 hours a day for work and play." + 
                "To set it up, I just set my iPad Air next to it, and it quickly and automatically transferred everything (apps, settings, etc)." +
                "The sound system is beyond belief, with an incredible depth and breadth and richness." + 
                "I've had systems with surround sound that didn't sound as good as this tiny critter." + 
                "(I was astounded, while watching a movie, when a truck drove across the screen, from right to left, and the sound started in the right speakers and moved to the left speakers as the truck crossed the screen)."
                + " I have hypersensitive hearing and can turn this down very low and still hear every sound and word perfectly.";
//"It has been widely reported that the camera on the 2020 MacBook Air is low quality, but mine was definitely in the non-acceptable range. Picture was very grainy, unfocused, and low light level. I strongly suspect the glass in front of the video chip was damaged, perhaps for a contamination during assembly, but have now way of checking directly. Cleaning the outside had no effect."
//"The engine makes it a powerful car. ";//"Up to 10 hours of surﬁng the web on Wi-Fi, watching video, or listening to music. Up to 9 hours of surﬁng the web using cellular data network. Charging via USB-C to computer system or power adapter.";
            //"Its classy design and the Mercedes name make it a very cool vehicle to drive with a great engine. ";
    String phrase2 = "Right out the box it was slow and even after updating it is still slow and seems to only be getting slower day by day.";
//            "The original MacBook Air is retailing for like $749.99 right now. Don't buy that. " +
//            "It looks ugly and it's old. " +
//            "Buy the new refreshed 2020 model. This model is authentic, straight from Apple, and it looks fresh."; 
//"The strong engine gives it enough power. ";//"Up to 10 hours of surﬁng the web on Wi‑Fi or watching video. Up to 9 hours of surﬁng the web using cellular data network. Charging via USB-C to computer system or power adapter.";
//"Its classy design and the Mercedes name make it a very cool vehicle to drive. "
//        + "The engine makes it a powerful car. "
        //+ "The strong engine gives it enough power. "
        //+ "The strong engine gives the car a lot of power.";
            //"This car has a great engine. ";
//    String sentence = "Not to worry with the 2cv.";

    System.out.println(parser.assessRelevance(phrase1, phrase2)
        .getMatchResult());

    System.out
        .println(parser
            .formGroupedPhrasesFromChunksForSentence(phrase1));//"Dedicated apps for music, TV, and podcasts. Smart new features like Sidecar, powerful technologies for developers, and your favorite iPad apps, now on Mac."));//"Its classy design and the Mercedes name make it a very cool vehicle to drive. "));
    
    System.out
        .println(parser
            .formGroupedPhrasesFromChunksForSentence(phrase2));//"Sounds too good to be true but it actually is, the world's first flying car is finally here. "));
    
    //System.out
      //  .println(parser
        //    .formGroupedPhrasesFromChunksForSentence("UN Ambassador Ron Prosor repeated the Israeli position that the only way the Palestinians will get UN membership and statehood is through direct negotiations with the Israelis on a comprehensive peace agreement"));

  }
}
