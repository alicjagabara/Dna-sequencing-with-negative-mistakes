import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class Solution {

    static final String strongWeakPattern = "Z";
    static final String purynyPiramidynyPattern = "P";

    static int resultLength;
    static int oliNumbers;
    static int oligonucleotideLength;
    static String start;
    static List<Snapshot> snapshots= new ArrayList<>();
    static List<String> results= new ArrayList<>();
    static List<String> pOligonucleotides = new ArrayList<>();
    static List<String> sOligonucleotides = new ArrayList<>();


    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        File file = new File("src/main/resources/bio.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        doc.getDocumentElement().normalize();

        start = doc.getDocumentElement().getAttribute("start");
        resultLength = Integer.parseInt(doc.getDocumentElement().getAttribute("length"));
        oligonucleotideLength = start.length();
        oliNumbers = resultLength - oligonucleotideLength + 1;

        NodeList nodeList = doc.getElementsByTagName("probe");

        for (int itr = 0; itr < nodeList.getLength(); itr++)
        {
            Node probe = nodeList.item(itr);
            System.out.println("\nNode Name :" + probe.getNodeName());
            String pattern = probe.getAttributes().getNamedItem("pattern").getTextContent();
            if (probe.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) probe;
                NodeList cells = eElement.getElementsByTagName("cell");

                for (int j = 0; j < cells.getLength(); j++) {
                    Node cell = cells.item(j);
                    if(cell.getNodeType() == Node.ELEMENT_NODE){
                        if(pattern.startsWith(strongWeakPattern)){
                            sOligonucleotides.add(cell.getFirstChild().getTextContent());
                        }
                        else if (pattern.startsWith(purynyPiramidynyPattern)){
                            pOligonucleotides.add(cell.getFirstChild().getTextContent());
                        }
                    }
                }
            }
        }
        runSearch();
    }

    private static void runSearch() {

        Result result = findFirst();
        int best = 0;

        snapshots.add(new Snapshot(result, findCandidates(result)));
        while (!snapshots.isEmpty()){
            Snapshot current = snapshots.remove(0);
            addCandidateAndCreateSnapshot(current);
            boolean res = true;
            while (res) {
                current.candidates = findCandidates(current.result);
                res = !current.candidates.candidates.isEmpty();
                if(res){
                    addCandidateAndCreateSnapshot(current);
                }
            }
            if(current.result.oliCount() > best) {
                System.out.println("New best: " + current.result.oliCount() +
                        " \n" + current.result.countResult() +
                        "\n" + current.result.result);
                best = current.result.oliCount();
            }
            if(usedAll(current.result)){
                addResult(current.result);
            }
        }
        System.out.println(results);
    }

    private static boolean usedAll(Result result) {
        for(String oli : sOligonucleotides){
            if(!result.resultS.contains(oli)){
                return false;
            }
        }
        for(String oli : pOligonucleotides){
            if(!result.resultP.contains(oli)){
                return false;
            }
        }
        return true;
    }

    private static Result findFirst(){
        Optional<String> firstP = pOligonucleotides.stream().filter(nucleotide -> matchesP(nucleotide, start)).findFirst();
        Optional<String> firstS = sOligonucleotides.stream().filter(nucleotide -> matchesS(nucleotide, start)).findFirst();
        Result result = new Result(oliNumbers - pOligonucleotides.size(),
                oliNumbers - sOligonucleotides.size() );

        if (firstP.isPresent()) {
            result.addToResultP(firstP.get(), 0);
        } else {
            result.addToResultP(toRY(start), 1);
        }

        if (firstS.isPresent()) {
            result.addToResultS(firstS.get(), 0);
        } else {
            result.addToResultS(toSW(start), 1);

        }
        return result;
    }

    private static String toRY(String nucleotide) {
        char last = nucleotide.charAt(nucleotide.length()-1);
        nucleotide = nucleotide.substring(0, nucleotide.length() -1);
        nucleotide = nucleotide.replaceAll("T", "Y");
        nucleotide = nucleotide.replaceAll("A", "R");
        nucleotide = nucleotide.replaceAll("G", "R");
        nucleotide = nucleotide.replaceAll("C", "Y");
        nucleotide += last;
        return nucleotide;
    }
    private static String toSW(String nucleotide) {
        char last = nucleotide.charAt(nucleotide.length()-1);
        nucleotide = nucleotide.substring(0, nucleotide.length() -1);
        nucleotide = nucleotide.replaceAll("T", "W");
        nucleotide = nucleotide.replaceAll("A", "W");
        nucleotide = nucleotide.replaceAll("G", "S");
        nucleotide = nucleotide.replaceAll("C", "S");
        nucleotide += last;
        return nucleotide;

    }

    private static boolean matchesS(String nucleotideS, String nucleotide) {
        return toSW(nucleotide).equals(nucleotideS);
    }
    private static boolean matchesP(String nucleotideP, String nucleotide) {
        return toRY(nucleotide).equals(nucleotideP);
    }

    private static void addResult(Result resultDNA) {
        String result = resultDNA.countResult();
        System.out.println("adding new result: " + result);
        results.add(result);
    }

    private static Candidates findCandidates(Result result){
        return new Candidates(findCandidatesP(result),
                findCandidatesS(result),
                result.missingP, result.missingS);
    }

    private static void addCandidateAndCreateSnapshot(Snapshot current) {
        Candidate candidate = current.candidates.candidates.remove(0);
        if(!current.candidates.candidates.isEmpty()) {
            Result resultCopy = current.result.getCopy();
            Candidates candidatesCopy = current.candidates.getCopy();
            snapshots.add(new Snapshot(resultCopy, candidatesCopy));
        }
        current.result.add(candidate);
    }

    private static Map<String, Integer> findCandidatesP(Result result) {
        int possibleDistance = result.missingP;
        Map<String, Integer> candidates = new HashMap<>();
        for(String p : pOligonucleotides){
            if(pOligonucleotides.stream().filter(p2 -> p2.equals(p)).count() >
                    result.resultP.stream().filter(r -> r.equals(p)).count()){
                int distance = countDistanceP(result.resultP.get(result.resultP.size() -1), p);
                if(distance <= possibleDistance){
                    candidates.put(p, distance);
                }
            }
        }
        return candidates;
    }

    private static Map<String, Integer> findCandidatesS(Result result) {
        int possibleDeference = result.missingS;
        Map<String, Integer> candidates = new HashMap<>();
        for(String s : sOligonucleotides){
            if(sOligonucleotides.stream().filter( p2 -> p2.equals(s)).count() >
                    result.resultS.stream().filter(r -> r.equals(s)).count()){
                int distance = countDistanceS(result.resultS.get(result.resultS.size() -1), s);
                if(distance <= possibleDeference){
                    candidates.put(s, distance);
                }
            }
        }
        return candidates;
    }

    private static int countDistanceP(String first, String second) {
    char last = first.charAt(first.length() -1);
    first = first.substring(0, first.length() - 1);
    switch (last){
        case 'A' :
        case 'G' :
            first += 'R';
            break;
        case 'C' :
        case 'T' :
            first += 'Y';
            break;
        case 'X' :
            first += 'X';
    }

    return countGap(first, second);
    }

    private static int countDistanceS(String first, String second) {
        char last = first.charAt(first.length() -1);
        first = first.substring(0, first.length() - 1);
        switch (last){
            case 'A' :
            case 'T' :
                first += 'W';
                break;
            case 'C' :
            case 'G' :
                first += 'S';
                break;
            case 'X' :
                first += 'X';
        }
        return countGap(first, second);
    }

    public static int countGap(String first, String second) {
        int gap = first.length() - 1;
        for (int i = 1; i < first.length(); i++) {
            boolean good = true;
            for (int j = i; j < first.length(); j++) {
                if (first.charAt(j) == second.charAt(j - i) ||
                        first.charAt(j) == 'X' || second.charAt(j - i) == 'X') {
                    continue;
                }
                good = false;
                break;
            }
            if(good){
                gap = i - 1;
                break;
            }
        }
        return gap;
    }

}