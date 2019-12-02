package syntaxanalysis;

//分析表
public class Form {

    public String[][] form=new String[30][30];

    public String[] column=new String[30]; int j=0; //列

    public String[] line=new String[30]; int i=0; //行

    //初始化两个都为0

    //向其中添加非终结符号
    public void addNotV(String s){
        int flag = findNotV(s);
        if(flag==-1) {
            line[i++] = s;
        }
    }

    //向其中添加终结符号
    public void addV(String s){
        int flag = findV(s);
        if(flag==-1) {
            column[j++] = s;
        }
    }

    //向指定的位置添加表达式
    public void addP(String NotV,String V,String s){ //向指定的非终结和终结符添加 指定的数字
        //找行
        int line1 = findNotV(NotV);
        //找列
        int comlue = findV(V);

        form[line1][comlue]=s;//TODO 如果为多个的话需要分隔符分割开来
    }

    //找非终结符号，并返回它的行数 返回-1为错
    private int findNotV(String s){
        for(int t=0;t<i;t++){
            if(s.equals(line[t])){
                return t;
            }
        }
        return -1;
    }

    //找终结符号，返回它的行数，返回为-1为错
    private int findV(String s){
        for(int t=0;t<j;t++){
            if(s.equals(column[t])){
                return t;
            }
        }
        return -1;
    }


    //将产生式在添加到表中
    public void addToForm(String L,String R){
        //左半部分一定是非终结
        addNotV(L);
        //右半部分需要拆开
        for (int z=0;z<R.length();z++){
            char c = R.charAt(z);
            //是终结符号
            if(c>='A'&& c <='Z'){
                addNotV(c+"");
            }else if(c=='*'){ //忽略空串
                continue;
            }else {
                addV(c+"");
            }
        }
    }
}
