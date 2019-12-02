package syntaxanalysis;

import java.util.HashSet;

//非终结fu
public class Nonterminal {

    //非终结符号
    private String mark;

    //First集合
    HashSet<Character> first=new HashSet<>();

    //Follow集合
    HashSet<Character> follow=new HashSet<>();


    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public void addIntoFirst(Character c){
        first.add(c);
    }

    public void addIntoFollow(Character c){
        follow.add(c);
    }

    public void addIntoAllFollow(HashSet<Character> characters){
        if (characters==null) {
            return;
        } else {
            for(Character c:characters){
                follow.add(c);
            }
        }
    }
    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append(mark+" : ");
        sb.append("first : ");
        for(Character character:first){
            sb.append(character.toString());
        }
        sb.append("follow : ");
        for(Character character:follow){
            sb.append(character.toString());
        }
        return sb.toString();
    }
}
