package syntaxanalysis;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//词法分析器
public class SyntaxAnalysis {

    //使用数组来记录
    Production[] productions=new Production[100]; //用来存储规则
    int productionsCount=0; //用来记录数组的长度 起始长度为1

    //当表达式的右侧出现空串时
    Set<Production> NullAble=new HashSet<>();

    //非终结符号的集合
    Set<Nonterminal> NotMark=new HashSet<>();

    //当前已经存在的非终结符号
    ArrayList<String> N=new ArrayList<>();

    //分析表
    Form form=new Form();

    //查看当前的字是否在非终结符的表中 若存在则返回false  若不存在则返回true
    private boolean isInNot(String c){
        for (String s:N){
            if(s.equals(c)){
                return false; //则证明存在
            }
        }
        return true;
    }

    //查看c字符是否在NullAble中
    public boolean isInNullAble(String  c){
        for(Production p:NullAble){
            if(p.getLeft().equals(c)){
                return true;
            }
        }
        return false;
    }

    //构造空串集合
    public void  getNullAble(){
        //先把右部全部扫描一遍
        for(int i=0;i<=20;i++){
            int n=i;
            if(n>=productionsCount){
                n=n%productionsCount;
            }

            //如果右部产生式直接是空串，直接加入到其中
            if(productions[n].getRight().equals("*")){
                NullAble.add(productions[n]);
            }else{ //如果右部的产生式不为空串，则可能他的右部产生式
                Production p=productions[n]; //将当前使用的产生式取出
                String right = p.getRight();
                for(int j=0;j<right.length();j++){
                    System.out.println("2");
                    if(isInNullAble(right.charAt(j)+"")){ //判断一下是否在空集合中
                        NullAble.add(p);  //如果在的话就将产生式加到NullAble中
                    }
                }
            }
        }
    }           
                
    //求First集合
    public void getFirst(){

        //遍历每条表达式
        for(int i=0;i<=20;i++) {
            int n = i;
            if (n >= productionsCount) {
                n = n % productionsCount;
            }
            Production p = productions[n];

            //找到当前的非终结符的类
            Nonterminal nonterminal=FindNotMark(p);

            if(nonterminal==null){
                return;
            }
            String right = p.getRight();
            for(int j=0;j<right.length();j++){
                char c = right.charAt(j);
                //是终结符号
                if(c <='z'&& c >='a'){
                    nonterminal.addIntoFirst(c);
                    break;
                }

                //是非终结符号
                if(c <='Z'&& c >='A'){
                    Nonterminal n1 = FindNotMark(c + "");

                    //将n1的First集合添加到not的first集合
                    for(Character character : n1.first){
                        nonterminal.addIntoFirst(character);
                    }
                    if(isInNullAble(n1.getMark())){
                        break;
                    }
                }
            }
            //保存已经修改的非终结符对象
            SaveNot(nonterminal);
        }
    }

    private void SaveNot(Nonterminal nonterminal) {
        NotMark.remove(nonterminal.getMark()); //先删除
        NotMark.add(nonterminal); //再添加进去
    }

    private Nonterminal FindNotMark(Production p) {
        for(Nonterminal n:NotMark){
            if(p.getLeft().equals(n.getMark())){
                return n;
            }
        }
        return null;
    }

    private Nonterminal FindNotMark(String c) {
        for(Nonterminal n:NotMark){
            if(n.getMark().equals(c)){
                return n;
            }
        }
        return null;
    }

    //求Follow集合
    public void getFollow(){

        for(int i=0;i<=20;i++) {
            ArrayList<Character> list=new ArrayList<>();

            int n = i;
            if (n >= productionsCount) {
                n = n % productionsCount;
            }

            Production p = productions[n];//取出表达式
            String right = p.getRight();
            Nonterminal nonterminal = FindNotMark(p.getLeft()); //左部的非终结符号
            AddListIntoOther(list,nonterminal.follow);

            for(int j=p.getRight().length()-1;j>=0;j--) {
                char charAt = right.charAt(j);
                //如果开头是终结符号
                if(charAt >='a'&& charAt <='z'){
                    list.clear(); //清除list中的所有数字
                    list.add(charAt);
                }
                if(charAt >='A'&& charAt <='Z'){
                    Nonterminal nonterminal1 = FindNotMark(charAt + "");
                    AddListIntoOther(nonterminal1.follow,list); //把当前的集合全部加到产生式左边符号的follow集合中
                    if(!isInNullAble(charAt+"")){ //非终结符号没在空集合中
                        list.clear();
                        AddListIntoOther(list,nonterminal1.first);
                    }else {
                        AddListIntoOther(list,nonterminal1.first);
                    }
                }
            }
        }
    }

    //将一个集合中的全部放到另一个集合中 list2->list1中
    public void AddListIntoOther(List<Character> list1,Set<Character> list2){
        if(list2==null){
            return;
        }else {
            for(Character c:list2){
                list1.add(c);
            }
        }
    }
    public void AddListIntoOther(Set<Character> list1,List<Character> list2){
        if(list2==null){
            return;
        }else {
            for(Character c:list2){
                list1.add(c);
            }
        }
    }

    //填写分析表
    public void createForm(){
        // 对于多有的非终结符号集
        // 对于每条表达式
        // productions
        for(int i=0;i<productionsCount;i++){
            Production production = productions[i];
            String left = production.getLeft();//获得left
            Nonterminal nonterminal = FindNotMark(left); //获得非终结符号
             //获得该符号的first集合follow集合
            HashSet<Character> follow = nonterminal.follow;
            HashSet<Character> first = nonterminal.first;

            for(Character c:first){
                form.addP(left,c+"",i+"");
            }

            //如果在空集合中 就将follow集添加到表中
            if(isInNullAble(left)){
                for(Character c:follow){
                    form.addP(left,c+"",i+"");
                }
            }
        }
    }

    //核心控制器
    public void Core(){

    }

    //输入产生式
    public void inputProduction() throws IOException {
        String read;
        int i=0; //记录是第几条产生式
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\王敏航\\Desktop\\abc.txt")));
        while((read=br.readLine())!=null){ //读取一行
            Production p=new Production();//创建表达式
            Nonterminal nm=new Nonterminal();

            if(i==0){
                p.setBegin(true);
            }

            p.setLeft(read.charAt(0)+""); //取出产生式最左边的非终结符
            nm.setMark(read.charAt(0)+"");//为非终结符号创建一个对象
            p.setRight(read.substring(3)); //  取出产生式右部得到部分

            if(isInNot(nm.getMark())) { //如果返回true则表示可以存储
                NotMark.add(nm); //将非终结符号添加到集合中
                N.add(nm.getMark());
            }

            //将表达式传入到分析表中构建分析表
            form.addToForm(read.charAt(0)+"",read.substring(3));

            //将产生式加到数组中
            productions[productionsCount++]=p;
            i++;
        }
    }

    //测试输出函数
    public void TestOutPut(){
        // 输出非终结符号
        for(int j=0;j<form.i;j++){
            System.out.print(form.line[j]+",");
        }
        System.out.println();
        for(int i=0;i<form.j;i++){
            System.out.print(form.column[i]+",");
        }
        System.out.println();
        for(int i=0;i<form.i;i++){
            for(int j=0;j<form.j;j++){
                System.out.print(form.form[i][j]+" ");
            }
            System.out.println();
        }
    }
    public static void main(String[] args) throws IOException {
        SyntaxAnalysis syntaxAnalysis=new SyntaxAnalysis();
        syntaxAnalysis.inputProduction();
        syntaxAnalysis.getNullAble(); //成功构建了空集合
        syntaxAnalysis.getFirst();
        syntaxAnalysis.getFollow();
        syntaxAnalysis.createForm();
        syntaxAnalysis.TestOutPut();
    }
}
