package syntaxanalysis;

//产生式 非巴克斯范式
public class Production {

    //左部符号
    private String left;

    //是否为开始符号
    private boolean isBegin;

    //右部符号
    private String right;

    public Production(){
        isBegin=false;
    }

    public Production(String left, boolean isBegin, String right) {
        this.left = left;
        this.isBegin = isBegin;
        this.right = right;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public boolean isBegin() {
        return isBegin;
    }

    public void setBegin(boolean begin) {
        isBegin = begin;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return left+"->"+right;
    }

}
