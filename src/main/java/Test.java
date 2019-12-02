import syntaxanalysis.Nonterminal;

public class Test {

    Nonterminal nonterminal=new Nonterminal();

    public Test(){
        nonterminal.addIntoFirst('c');
    }

    public static void main(String[] args) {
        Test t=new Test();
    }
}
