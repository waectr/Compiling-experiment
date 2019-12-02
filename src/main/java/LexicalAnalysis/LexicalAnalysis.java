package LexicalAnalysis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

//C语言词法分析器
public class LexicalAnalysis {

    class Unit{
        String key;
        String value;
    }

    public LexicalAnalysis(String str){ //传入目标文件
        this.objectFile=str;
    }

    private String objectFile;

    /*
        一词一类
     */

    //标示符定义为
    //定义C语言保留字表(32个)
    static String[] reserveWord={
        "auto", "break", "case", "char", "const", "continue",
        "default", "do", "double", "else", "enum", "extern",
        "float", "for", "goto", "if", "int", "long",
        "register", "return", "short", "signed", "sizeof", "static",
        "struct", "switch", "typedef", "union", "unsigned", "void",
        "volatile", "while"
    };

    //界符
    static String[] delimiters={
            "+","-","*","/","<","<=",">",">=","=","==",
            "!=",";","(",")","^",",","\"","\'","#","&",
            "&&","|","||","%","~","<<",">>","[","]","{",
            "}","\\\\",".","\\?",":","!"
    };

    /*
        一类 FLAG
     */

    //建立标示符表
    int variablePoint=0;//标示符表现在的位置（当前长度）
    String[] variableName=new String[1000];

    /*
        一类  NUM
     */
    //建立常数表
    int numPoint=0;//标示符表现在的位置（当前长度）
    String[] numName=new String[1000];

    //指针，指向正准备扫描的地方
    int Textpoint=0;

    //设置缓冲区
    StringBuilder buffer=new StringBuilder();

    //设置存储键值对
    ArrayList<Unit> list=new ArrayList<>();

    //将文件中所有的换行全部去掉
    private void makeTextOnline() throws Exception {
        String str=buffer.toString();
        buffer=new StringBuilder();
        str=str.replace("\n"," ");
        str=str.replace("\r"," ");
        str=str.replace("\t"," ");
        buffer.append(str);
    }

    //先要预编译，去掉所有的注释
    private void filterText(){
        String s=buffer.toString();
        buffer=new StringBuilder();
//        System.out.println("清空buffer后:"+buffer.toString()+"\n");
        for(int i=0;i<s.length();i++) {
            if (s.charAt(i) == '/' && s.charAt(i + 1) == '/') {//出现了//就要一直找到换行符为止
                while (s.charAt(i) != '\n') {
                    i++;
                }
            }
            if (s.charAt(i) == '/' && s.charAt(i + 1) == '*') { //出现多行的注释
                i = i + 2;
                while (s.charAt(i) != '*' && s.charAt(i + 1) != '/') {
                    i++;
                    if (s.charAt(i) == '$'){
                        System.out.println("错误，没有 */");
                        System.exit(0);
                    }
                }
                i = i + 2;
            }

            buffer.append(s.charAt(i));
//            System.out.println("开始:\n"+buffer.toString()+"\nends\n");
        }
        buffer.append("\0");
    }

    //读入资源,将所有资源读入缓存区中
    private void readIntoText() throws Exception {
        String nowRead=null;
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(objectFile)));
        while ((nowRead=br.readLine())!=null){
            buffer.append(nowRead+"\n");
        }
        buffer.append("$");
    }

    public static void main(String[] args) throws Exception {
        LexicalAnalysis analysis = new LexicalAnalysis("C:\\Users\\王敏航\\Desktop\\abc.txt");

        //读入资源
        analysis.readIntoText();
        System.out.println("读入的原始资源:\n"+analysis.buffer.toString()+"\n");

        //先要预编译，去掉所有的注释
        analysis.filterText();
        System.out.println("去的注释:\n"+analysis.buffer.toString()+"\n");

        //将文件中所有的换行全部去掉
        analysis.makeTextOnline();
        System.out.println("去的掉换行符\n"+analysis.buffer.toString());

        //核心过滤
        analysis.Core();

        ArrayList<Unit> l=analysis.list;
        for(Unit u:l){
            System.out.println(u.key+" "+u.value);
        }

        //输出符号表
//        for(String s:analysis.numName){
//            System.out.println(s);
//        }
    }

    public void Core() throws Exception {

        String s = buffer.toString();
        int point=0; //指向当前走向的字符串下标

        while (true) {

            //要拼凑的字符串
            StringBuilder strToken=new StringBuilder();

            if(s.charAt(point)=='$'){//读到了最后
                break;//退出
            }

            while (s.charAt(point)==' '){//过滤空格
                point++;
            }

            if(IsLetter(s.charAt(point))){ //首字母是字母
                while (IsLetter(s.charAt(point)) || IsDigit(s.charAt(point))){ //数字或者字母
                    strToken.append(s.charAt(point));
                    point++;
                    if(s.charAt(point)=='$'){//读到了最后
                        break;//退出
                    }
                }

//                point--;//回一下计数器
                if(s.charAt(point)=='$'){//读到了最后
                    break;//退出
                }
                int code = Reserve(strToken.toString());
                if(code==0){ //不是关键字
                    //将这个字插入到标示符表
                    int i = InsertId(strToken.toString());

                    Unit unit = new Unit();
                    unit.key="$ID";
                    unit.value=i+"";
                    //将这个二元组加到Map中
                    list.add(unit);
                }else {
                    Unit unit = new Unit();
                    unit.key=code+"";
                    unit.value="-";
                    //将这个二元组加到Map中
                    list.add(unit);
                }
                //结束的时候
                strToken=new StringBuilder();
                while (s.charAt(point)==' '){
                    point++;
                }
            }else if(IsDigit(s.charAt(point))){
                while (IsDigit(s.charAt(point))){
                    //TODO 这里要处理读到末尾
                    strToken.append(s.charAt(point));
                    point++;
                }
//                point--;
                int i=InsertConst(strToken.toString());
                Unit unit = new Unit();
                unit.key="$INT";
                unit.value=i+"";
                //将这个二元组加到Map中
                list.add(unit);
                strToken=new StringBuilder();
                while (s.charAt(point)==' '){
                    point++;
                }
            }else if(s.charAt(point)=='='){
                Unit unit = new Unit();
                unit.key="$ASSIGN";
                unit.value="-";
                //将这个二元组加到Map中
                list.add(unit);
                point++;
                while (s.charAt(point)==' '){
                    point++;
                }
            }else if(s.charAt(point)=='+'){
                Unit unit = new Unit();
                unit.key="$PLUS";
                unit.value="-";
                //将这个二元组加到Map中
                list.add(unit);
                point++;
                while (s.charAt(point)==' '){
                    point++;
                }
            }else if(s.charAt(point)=='*'){
                Unit unit = new Unit();
                unit.key="$MULTIP";
                unit.value="-";
                //将这个二元组加到Map中
                list.add(unit);
                point++;
                while (s.charAt(point)==' '){
                    point++;
                }
            }else if(s.charAt(point)==';'){
                Unit unit = new Unit();
                unit.key="$SEMICOLON";
                unit.value="-";
                //将这个二元组加到Map中
                list.add(unit);
                point++;
                while (s.charAt(point)==' '){
                    point++;
                }
            }else if(s.charAt(point)=='('){
                Unit unit = new Unit();
                unit.key="$LPAR";
                unit.value="-";
                //将这个二元组加到Map中
                list.add(unit);
                point++;
                while (s.charAt(point)==' '){
                    point++;
                }
            }else if(s.charAt(point)==')'){
                Unit unit = new Unit();
                unit.key="$RPAR";
                unit.value="-";
                //将这个二元组加到Map中
                list.add(unit);
                point++;
                while (s.charAt(point)==' '){
                    point++;
                }
            }else if(s.charAt(point)=='|'){
                Unit unit = new Unit();
                unit.key="$LBRACE";
                unit.value="-";
                //将这个二元组加到Map中
                list.add(unit);
                point++;
                while (s.charAt(point)==' '){
                    point++;
                }
            }else if(s.charAt(point)=='|'){
                Unit unit = new Unit();
                unit.key="$RBRACE";
                unit.value="-";
                //将这个二元组加到Map中
                list.add(unit);
                point++;
                while (s.charAt(point)==' '){
                    point++;
                }
            }else if(s.charAt(point)=='#'){
                Unit unit = new Unit();
                unit.key="$BEGIN#";
                unit.value="-";
                //将这个二元组加到Map中
                list.add(unit);
                point++;
                while (s.charAt(point)==' '){
                    point++;
                }
            }else if(s.charAt(point)=='<'){
                Unit unit = new Unit();
                unit.key="$FLAG<";
                unit.value="-";
                //将这个二元组加到Map中
                list.add(unit);
                point++;
                while (s.charAt(point)==' '){
                    point++;
                }
            }else if(s.charAt(point)=='>'){
                Unit unit = new Unit();
                unit.key="$FLAG>";
                unit.value="-";
                //将这个二元组加到Map中
                list.add(unit);
                point++;
                while (s.charAt(point)==' '){
                    point++;
                }
            }else if(s.charAt(point)=='.'){
                Unit unit = new Unit();
                unit.key="$spot.";
                unit.value="-";
                //将这个二元组加到Map中
                list.add(unit);
                point++;
                while (s.charAt(point)==' '){
                    point++;
                }
            }else if(s.charAt(point)=='{'){
                Unit unit = new Unit();
                unit.key="$FUCBEGIN{";
                unit.value="-";
                //将这个二元组加到Map中
                list.add(unit);
                point++;
                while (s.charAt(point)==' '){
                    point++;
                }
            }else if(s.charAt(point)=='}'){
                Unit unit = new Unit();
                unit.key="$FUCEND}";
                unit.value="-";
                //将这个二元组加到Map中
                list.add(unit);
                point++;
                while (s.charAt(point)==' '){
                    point++;
                }
            }else if(s.charAt(point)=='"'){
                Unit unit = new Unit();
                unit.key="$FLAG\"";
                unit.value="-";
                //将这个二元组加到Map中
                list.add(unit);
                point++;
                while (s.charAt(point)==' '){
                    point++;
                }
            }else{
                System.out.println("错误");
                throw new Exception();
            }
        }
    }

    private int InsertConst(String s) {
        int i = Integer.parseInt(s);
        String s1 = Integer.toBinaryString(i);
        numName[numPoint]=s1;
        int f=numPoint;
        numPoint++;
        return f;
    }

    //将字符串插入到标示符中
    private int InsertId(String s) {
        int t=variablePoint;
//        System.out.println(variablePoint);
        variableName[variablePoint]=s;
        variablePoint++;
        return t;
    }

    //判断是否为数字
    private boolean IsDigit(char c) {
        if(c>=48 && c<=57){
            return true;
        }else {
            return false;
        }
    }

    //判断是否为字母
    public boolean IsLetter(char c){
        if(c>=65 && c<=106){ //大写字母
            return true;
        }else if(c>=97 && c<=122){ //小写字母
            return true;
        }else {
            return false;
        }
    }

    //将字符串查找看是不是保留字表 是保留字返回它的编码
    public int Reserve(String str){
//        reserveWord;
        for(int i=0;i<reserveWord.length;i++){
            if(str.equals(reserveWord[i])){
                return i;
            }
        }
        return 0;
    }
}
