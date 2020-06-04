
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
        copy.result = new StringBuilder(result.toString());
        return copy;
    }

    List<String> resultP = new ArrayList<>();
    List<String> resultS = new ArrayList<>();

    StringBuilder result = new StringBuilder();

    int missingP;
    int missingS;

    public void addToResultP(String oli){
        resultP.add(oli);
    }

    public void addToResultS(String oli){
        resultS.add(oli);
    }
    public int oliCount(){
        return resultP.size();
    }

    public void add(Candidate candidate) {
        String oli = finalFromBinaryChip(candidate.oliP, candidate.oliS);
        result.append(oli.substring(oli.length() - candidate.gap -1));
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
            }
            else if( p == 'Y'){
                if(s == 'W'){
                    result.append('T');
                }
                else if( s == 'S'){
                    result.append('C');
                }
            }

        }
        result.append(oliS.charAt(oliP.length() -1));
        return result.toString();
    }

}
