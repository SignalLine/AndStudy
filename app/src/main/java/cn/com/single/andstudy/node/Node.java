package cn.com.single.andstudy.node;

import java.util.ArrayList;
import java.util.List;

/**
 * @author li
 *         Create on 2018/6/25.
 * @Description
 */

public class Node {

    private int id;
    /**
     * 根节点pid = 0
     */
    private int pId = 0;
    private String name;
    /**
     * 树的层级
     */
    private int level;

    private boolean isExpand = false;
    /**
     * 图标
     */
    private int icon;
    /**
     * 父节点
     */
    private Node parent;
    /**
     * 子节点
     */
    private List<Node> children = new ArrayList<>();

    public Node(int id, int pId, String name) {
        this.id = id;
        this.pId = pId;
        this.name = name;
    }

    public Node() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getpId() {
        return pId;
    }

    public void setpId(int pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 通过父节点得到当前节点的层级
     * @return
     */
    public int getLevel() {
        return parent == null ? 0 : parent.getLevel() + 1;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;

        if(!expand){
            for (Node node : children) {
                node.setExpand(false);
            }
        }
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    /**
     * 是否是根节点
     * @return
     */
    public boolean isRoot(){
        return parent == null;
    }

    public boolean isParentExpand(){
        if(parent == null){
            return false;
        }

        return parent.isExpand();
    }

    /**
     * 是否叶子节点
     * @return
     */
    public boolean isLeaf(){
        return children.size() == 0;
    }



    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", pId=" + pId +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", isExpand=" + isExpand +
                ", icon=" + icon +
                ", parent=" + parent +
                ", children=" + children +
                '}';
    }
}
