package Entity;

import java.util.ArrayList;

public class TreeNode {

    //当前节点的值
    double value;
    //得到当前节点值的概率
    double probability;
    //格网层数
    int level;
    //计数器
    int counter;
    //存储孩子节点
    ArrayList<TreeNode> children = new ArrayList<>();

    /**
     * getter
     */
    public double getValue() {
        return value;
    }

    public double getProbability() {
        return probability;
    }

    public int getLevel() {
        return level;
    }

    public int getCounter() {
        return counter;
    }

    public ArrayList<TreeNode> getChildren() {
        return children;
    }

    /**
     * setter
     */
    public void setValue(double value) {
        this.value = value;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public void setChildren(ArrayList<TreeNode> children) {
        this.children = children;
    }
}
