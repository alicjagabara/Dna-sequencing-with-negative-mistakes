public class Candidate {
    String oliP;
    String oliS;
    int gap;

    public Candidate(String oliP, String oliS, int match) {
        this.oliP = oliP;
        this.oliS = oliS;
        this.gap = match;
    }


    @Override
    protected Object clone(){
        return new Candidate(oliP, oliS, gap);
    }
}
