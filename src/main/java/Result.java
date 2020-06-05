
import java.util.ArrayList;
import java.util.List;

public class Result {
    public Result(int missingP, int missingS) {
        this.missingP = missingP;
        this.missingS = missingS;
    }

    public Result getCopy(){
        Result copy = new Result(missingP, missingS);
        copy.resultP = new ArrayList<>(resultP);
        copy.resultS = new ArrayList<>(resultS);
        return copy;
    }

    List<String> resultP = new ArrayList<>();
    List<String> resultS = new ArrayList<>();

    List<String> result = new ArrayList<>();

    int missingP;
    int missingS;

    public void addToResultP(String oli, int gap){
        if(oli.contains("X")){
            oli = readMissingPValues(gap);
            missingP -= gap +1;
        }
        else {
            missingP -= gap;
        }
        resultP.add(oli);
    }
    public void addToResultS(String oli, int gap){
        if(oli.contains("X")){
            oli = readMissingSValues(gap);
            missingS -= gap +1;
        }
        else {
            missingS -= gap;
        }
        resultS.add(oli);
    }

    private String readMissingPValues(int gap) {
        return getValueFromPreviousP(gap, resultP);
    }

    private String readMissingSValues(int gap) {
        return getValueFromPreviousS(gap, resultS);
    }

    private String getValueFromPreviousP(int gap, List<String> resultP) {
        StringBuilder res = new StringBuilder();
        String previous = resultP.get(resultP.size() -1);
        res.append(previous, gap + 1, previous.length() - 1);
        char last = previous.charAt(previous.length() - 1);
        switch (last){
            case 'A' :
            case 'G' :
                res.append('R');
                break;
            case 'C' :
            case 'T' :
                res.append('Y');
                break;
            case 'X' :
                res.append('X');
        }
        for (int i = 0; i < gap + 1; i++) {
            res.append("X");
        }
        return res.toString();
    }
    public int oliCount(){
        return resultP.size();
    }

    private String getValueFromPreviousS(int gap, List<String> resultS) {
        StringBuilder res = new StringBuilder();
        String previous = resultS.get(resultS.size() -1);
        res.append(previous, gap + 1, previous.length() - 1);
        char last = previous.charAt(previous.length() - 1);
        switch (last){
            case 'A' :
            case 'T' :
                res.append('W');
                break;
            case 'C' :
            case 'G' :
                res.append('S');
                break;
            case 'X' :
                res.append('X');
        }
        for (int i = 0; i < gap + 1; i++) {
            res.append("X");
        }
        return res.toString();
    }

    public void add(Candidate candidate) {
        addToResultP(candidate.oliP, candidate.gap);
        addToResultS(candidate.oliS, candidate.gap);
    }

    private String finalFromBinaryChip(String oliP, String oliS) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < oliP.length() - 1; i++) {
            char p = oliP.charAt(i);
            char s = oliS.charAt(i);

            if(p == 'R'){
                if(s == 'W'){
                    result.append('A');
                }
                else if( s == 'S'){
                    result.append('G');
                }
                else{
                    result.append('X');
                }
            }
            else if( p == 'Y'){
                if(s == 'W'){
                    result.append('T');
                }
                else if( s == 'S'){
                    result.append('C');
                }
                else{
                    result.append('X');
                }
            }else{
                result.append('X');
            }

        }
        if(oliS.charAt(oliS.length() -1) == 'X' || oliP.charAt(oliP.length() -1) == 'X'){
            result.append('X');
        }else{
            result.append(oliS.charAt(oliP.length() -1));
        }
        return result.toString();
    }

    public String countResult() {
        StringBuilder result = new StringBuilder(finalFromBinaryChip(resultP.get(0), resultS.get(0)));
        for (int i = 1; i < resultP.size(); i++) {
            String part = finalFromBinaryChip(resultP.get(i), resultS.get(i));
            this.result.add(part);

            String first = result.substring(result.length() - part.length());
            int gap = Solution.countGap(first, part);
            int match = first.length() - gap - 1;
            result.replace(result.length() - match, result.length(), part.substring(0, match +1));
            result.append(part.substring(match +1));
        }
        return result.toString();
    }
}
