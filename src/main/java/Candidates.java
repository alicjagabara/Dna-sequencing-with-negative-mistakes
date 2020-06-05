import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Candidates {

    List<Candidate> candidates = new ArrayList<>();

    public Candidates(Map<String, Integer> cP, Map<String, Integer> cS, int missingP, int missingS) {
        addMatching(cP, cS, missingP, missingS);
    }

    public Candidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    private void addMatching(Map<String, Integer> cP, Map<String, Integer> cS,  int missingP, int missingS) {
        ///pairs
        for(Map.Entry<String, Integer> p : cP.entrySet()){
            for(Map.Entry<String, Integer> s : cS.entrySet()){
                if(matches(p.getKey(), s.getKey()) && p.getValue().equals(s.getValue())){
                    candidates.add(new Candidate(p.getKey(), s.getKey(), p.getValue()));
                    break;
                }
            }
        }
        //without pair
        for(Map.Entry<String, Integer> p : cP.entrySet()){
            if(p.getValue() < missingS){
                StringBuilder second = new StringBuilder();
                for (int i = 0; i < p.getKey().length(); i++) {
                    second.append('X');
                }
                candidates.add(new Candidate(p.getKey(), second.toString(), p.getValue()));
            }
        }
        for(Map.Entry<String, Integer> s : cS.entrySet()){
            if(s.getValue() < missingP){
                StringBuilder first = new StringBuilder();
                for (int i = 0; i < s.getKey().length(); i++) {
                    first.append('X');
                }
                candidates.add(new Candidate(first.toString(), s.getKey(), s.getValue()));
            }
        }
    }

    public Candidates getCopy(){
        List<Candidate> copy = new ArrayList<>();

        for (Candidate candidate : candidates) {
            copy.add((Candidate) candidate.clone());
        }
        return new Candidates(copy);
    }

    private boolean matches(String p, String s) {
        return (p.charAt(p.length() - 1) == s.charAt(s.length() - 1)) ||
                ((p.charAt(p.length() - 1) == 'X' ) ||
                        (s.charAt(s.length() - 1)) == 'X');
    }
}
