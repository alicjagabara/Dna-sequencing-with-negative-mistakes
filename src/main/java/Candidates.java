import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Candidates {

    List<Candidate> candidates = new ArrayList<>();

    public Candidates(Map<String, Integer> cP, Map<String, Integer> cS) {
        addMatching(cP, cS);
    }

    public Candidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }

    private void addMatching(Map<String, Integer> cP, Map<String, Integer> cS) {
        for(Map.Entry<String, Integer> p : cP.entrySet()){
            for(Map.Entry<String, Integer> s : cS.entrySet()){
                if(matches(p.getKey(), s.getKey()) && p.getValue() == (s.getValue())){
                    cS.remove(s.getKey());
                    candidates.add(new Candidate(p.getKey(), s.getKey(), p.getValue()));
                    break;
                }
            }
        }
    }

    public Candidates getCopy(){
        List<Candidate> copy = new ArrayList<>();
        Iterator<Candidate> iterator = candidates.iterator();

        while(iterator.hasNext())
        {
            copy.add((Candidate) iterator.next().clone());
        }
        return new Candidates(copy);
    }

    private boolean matches(String p, String s) {
        return p.charAt(p.length() - 1) == s.charAt(s.length() - 1);
    }
}
