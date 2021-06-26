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
package opennlp.tools.parse_thicket.pattern_structure;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import opennlp.tools.parse_thicket.ParseTreeNode;
import opennlp.tools.textsimilarity.ParseTreeChunk;
import opennlp.tools.textsimilarity.ParseTreeMatcherDeterministic;
import textsimilarity.chunker2matcher.ParserPure2MatcherProcessor;

public class PhrasePatternStructure {

    int objectCount;
    int attributeCount;
    HashMap<Integer, String> context = null;
    public List<PhraseConcept> conceptList;
    ParseTreeMatcherDeterministic md;

    ParserPure2MatcherProcessor mp;

    public PhrasePatternStructure(int objectCounts, int attributeCounts, String fileName) throws IOException {
        objectCount = objectCounts;
        attributeCount = attributeCounts;
        conceptList = new ArrayList<PhraseConcept>();
        PhraseConcept bottom = new PhraseConcept();
        md = new ParseTreeMatcherDeterministic();
        //My
        mp = ParserPure2MatcherProcessor.getInstance();
        MyReadPhrase fr = new MyReadPhrase();
        fr.ReadContextFromCxt(fileName);
        this.context = fr.getContext();
        //
        /*Set<Integer> b_intent = new HashSet<Integer>();
		for (int index = 0; index < attributeCount; ++index) {
			b_intent.add(index);
		}
		bottom.setIntent(b_intent);*/
        bottom.setPosition(0);
        conceptList.add(bottom);

        createLatticeFromContext();
    }

    //My
    public void createLatticeFromContext() {
        LinkedHashSet<Integer> obj;
        List<List<ParseTreeChunk>> intent;
        // attributes list
        ArrayList<Integer> attributes = new ArrayList<Integer>();
        for (int i = 0; i < attributeCount; i++) {
            attributes.add(i);
        }
        // objects set
        LinkedHashSet<Integer> objects = new LinkedHashSet<Integer>();
        for (int i = 0; i < objectCount; i++) {
            objects.add(i);
        }

        this.conceptList.get(0).setIntent(new ArrayList<List<ParseTreeChunk>>());
        for (int i = 0; i < objectCount; i++) {
            obj = new LinkedHashSet<Integer>();
            obj.add(i);
            intent = mp.formGroupedPhrasesFromChunksForSentence(context.get(i));
            this.AddIntent(intent, 0);
        }
    }
    //

    public PhrasePatternStructure(int objectCounts, int attributeCounts) {
        objectCount = objectCounts;
        attributeCount = attributeCounts;
        conceptList = new ArrayList<PhraseConcept>();
        PhraseConcept bottom = new PhraseConcept();
        md = new ParseTreeMatcherDeterministic();

        /*Set<Integer> b_intent = new HashSet<Integer>();
		for (int index = 0; index < attributeCount; ++index) {
			b_intent.add(index);
		}
		bottom.setIntent(b_intent);*/
        bottom.setPosition(0);
        conceptList.add(bottom);
    }

    public int GetMaximalConcept(List<List<ParseTreeChunk>> intent, int Generator) {
        boolean parentIsMaximal = true;
        while (parentIsMaximal) {
            parentIsMaximal = false;
            for (int parent : conceptList.get(Generator).parents) {
                if (conceptList.get(parent).intent.containsAll(intent)) {
                    Generator = parent;
                    parentIsMaximal = true;
                    break;
                }
            }
        }
        return Generator;
    }

    public int AddIntent(List<List<ParseTreeChunk>> intent, int generator) {
        System.out.println("debug");
        System.out.println("called for " + intent);
        //printLattice();
        int generator_tmp = GetMaximalConcept(intent, generator);
        generator = generator_tmp;
        if (conceptList.get(generator).intent.equals(intent)) {
            System.out.println("at generator:" + conceptList.get(generator).intent);
            System.out.println("to add:" + intent);
            System.out.println("already generated");
            return generator;
        }
        Set<Integer> generatorParents = conceptList.get(generator).parents;
        Set<Integer> newParents = new HashSet<Integer>();
        for (int candidate : generatorParents) {
            if (!intent.containsAll(conceptList.get(candidate).intent)) {
                //if (!conceptList.get(candidate).intent.containsAll(intent)) {
                //Set<Integer> intersection = new HashSet<Integer>(conceptList.get(candidate).intent);
                //List<List<ParseTreeChunk>> intersection = new ArrayList<List<ParseTreeChunk>>(conceptList.get(candidate).intent);
                //intersection.retainAll(intent);
                List<List<ParseTreeChunk>> intersection = md
                        .matchTwoSentencesGroupedChunksDeterministic(intent, conceptList.get(candidate).intent);
                System.out.println("recursive call (inclusion)");
                candidate = AddIntent(intersection, candidate);
            }
            boolean addParents = true;
            System.out.println("now iterating over parents");
            Iterator<Integer> iterator = newParents.iterator();
            while (iterator.hasNext()) {
                Integer parent = iterator.next();
                if (conceptList.get(parent).intent.containsAll(conceptList.get(candidate).intent)) {
                    addParents = false;
                    break;
                } else {
                    if (conceptList.get(candidate).intent.containsAll(conceptList.get(parent).intent)) {
                        iterator.remove();
                    }
                }
            }
            /*for (int parent : newParents) {
				System.out.println("parent = " + parent);
				System.out.println("candidate intent:"+conceptList.get(candidate).intent);
				System.out.println("parent intent:"+conceptList.get(parent).intent);

				if (conceptList.get(parent).intent.containsAll(conceptList.get(candidate).intent)) {
					addParents = false;
					break;
				}
				else {
					if (conceptList.get(candidate).intent.containsAll(conceptList.get(parent).intent)) {
						newParents.remove(parent);
					}
				}
			}*/
            if (addParents) {
                newParents.add(candidate);
            }
        }
        System.out.println("size of lattice: " + conceptList.size());
        PhraseConcept newConcept = new PhraseConcept();
        newConcept.setIntent(intent);
        newConcept.setPosition(conceptList.size());
        conceptList.add(newConcept);
        conceptList.get(generator).parents.add(newConcept.position);
        for (int newParent : newParents) {
            if (conceptList.get(generator).parents.contains(newParent)) {
                conceptList.get(generator).parents.remove(newParent);
            }
            conceptList.get(newConcept.position).parents.add(newParent);
        }
        return newConcept.position;
    }

    public void printLatticeStats() {
        System.out.println("Lattice stats");
        System.out.println("max_object_index = " + objectCount);
        System.out.println("max_attribute_index = " + attributeCount);
        System.out.println("Current concept count = " + conceptList.size());

    }

    public void printLattice() {
        for (int i = 0; i < conceptList.size(); ++i) {
            printConceptByPosition(i);
        }
    }

    public void printConceptByPosition(int index) {
        System.out.println("Concept at position " + index);
        conceptList.get(index).printConcept();
    }

    public List<List<ParseTreeChunk>> formGroupedPhrasesFromChunksForPara(
            List<List<ParseTreeNode>> phrs) {
        List<List<ParseTreeChunk>> results = new ArrayList<List<ParseTreeChunk>>();
        List<ParseTreeChunk> nps = new ArrayList<ParseTreeChunk>(), vps = new ArrayList<ParseTreeChunk>(),
                pps = new ArrayList<ParseTreeChunk>();
        for (List<ParseTreeNode> ps : phrs) {
            ParseTreeChunk ch = convertNodeListIntoChunk(ps);
            String ptype = ps.get(0).getPhraseType();
            System.out.println(ps);
            if (ptype.equals("NP")) {
                nps.add(ch);
            } else if (ptype.equals("VP")) {
                vps.add(ch);
            } else if (ptype.equals("PP")) {
                pps.add(ch);
            }
        }
        results.add(nps);
        results.add(vps);
        results.add(pps);
        return results;
    }

    private ParseTreeChunk convertNodeListIntoChunk(List<ParseTreeNode> ps) {
        List<String> lemmas = new ArrayList<String>(), poss = new ArrayList<String>();
        for (ParseTreeNode n : ps) {
            lemmas.add(n.getWord());
            poss.add(n.getPos());
        }
        ParseTreeChunk ch = new ParseTreeChunk(lemmas, poss, 0, 0);
        ch.setMainPOS(ps.get(0).getPhraseType());
        return ch;
    }

    public void getExample() throws IOException{
        MyReadPhrase fr = new MyReadPhrase();
        md = new ParseTreeMatcherDeterministic();
        //My
        mp = ParserPure2MatcherProcessor.getInstance();
        fr.ReadContextFromCxt("C:\\Users\\eliza\\Documents\\NetBeansProjects\\FCA\\FCA\\Data.txt");
        context = fr.getContext();
        conceptList = new ArrayList<PhraseConcept>();
        PhraseConcept pc = new PhraseConcept();
        conceptList.add(pc);
        PhraseConcept newConcept = new PhraseConcept();
        newConcept.setPosition(0);
        ArrayList<List<ParseTreeChunk>> intent = new ArrayList<List<ParseTreeChunk>>();
        newConcept.setIntent(intent);
        newConcept.addExtents(new LinkedHashSet<Integer>());
        LinkedHashSet<Integer> ext = new LinkedHashSet<Integer>();
        conceptList.add(newConcept);
        System.out.println("Position" + newConcept.position);
        System.out.println("Intent" + intent);
        System.out.println("Extent" + ext);
        
        newConcept.setPosition(1);
        intent = new ArrayList<List<ParseTreeChunk>>();
        intent = (ArrayList<List<ParseTreeChunk>>) mp.formGroupedPhrasesFromChunksForSentence(context.get(0));
        newConcept.setIntent(intent);
        ext = new LinkedHashSet<Integer>();
        ext.add(0);
        newConcept.addExtents(ext);
        conceptList.add(newConcept);
        System.out.println("Position" + newConcept.position);
        System.out.println("Intent" + intent);
        System.out.println("Extent" + ext);
        
        
        newConcept.setPosition(2);
        ArrayList<List<ParseTreeChunk>> intent1 = new ArrayList<List<ParseTreeChunk>>();
        intent1 = (ArrayList<List<ParseTreeChunk>>) mp.formGroupedPhrasesFromChunksForSentence(context.get(1));
        newConcept.setIntent(intent);
        ext = new LinkedHashSet<Integer>();
        ext.add(1);
        newConcept.addExtents(ext);
        conceptList.add(newConcept);
        System.out.println("Position" + newConcept.position);
        System.out.println("Intent" + intent);
        System.out.println("Extent" + ext);
        
        newConcept.setPosition(3);
        ArrayList<List<ParseTreeChunk>> intent2 = new ArrayList<List<ParseTreeChunk>>();
        intent2 = (ArrayList<List<ParseTreeChunk>>) mp.formGroupedPhrasesFromChunksForSentence(context.get(2));
        newConcept.setIntent(intent);
        ext = new LinkedHashSet<Integer>();
        ext.add(2);
        newConcept.addExtents(ext);;
        conceptList.add(newConcept);
        System.out.println("Position" + newConcept.position);
        System.out.println("Intent" + intent);
        System.out.println("Extent" + ext);
        
        newConcept.setPosition(3);
        intent = new ArrayList<List<ParseTreeChunk>>();
        List<List<ParseTreeChunk>> intersection = md.matchTwoSentencesGroupedChunksDeterministic(intent1, intent2);
        newConcept.setIntent(intersection);
        System.out.println("Intent" + intersection);
        ext = new LinkedHashSet<Integer>();
        ext.add(1);
        ext.add(2);
        newConcept.addExtents(ext);
        conceptList.add(newConcept);
        System.out.println("Position" + newConcept.position);
        System.out.println("Intent" + mp.assessRelevance(context.get(1), context.get(2)).getMatchResult());
        System.out.println("Extent" + ext);
        //printLattice();
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException {

        PhrasePatternStructure cl = new PhrasePatternStructure(4, 1, "C:\\Users\\eliza\\Documents\\NetBeansProjects\\FCA\\FCA\\Data.txt");
        //cl.getExample();
        
    }

}
