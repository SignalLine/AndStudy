package cn.com.single.andstudy.node;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.com.single.andstudy.R;


/**
 * @author li
 *         Create on 2018/6/22.
 * @Description
 */

public class TreeHelper {

    private static Node sNodeIcon;

    /**
     * 将用户的数据转化为树形数据
     *
     * @param datas
     * @param <T>
     * @return
     */
    public static <T> List<Node> convertDatas2Nodes(List<T> datas) throws IllegalAccessException {

        List<Node> nodes = new ArrayList<>();
        Node node = null;
        for (T t : datas) {

            int id = -1;
            int pId = -1;
            String label = null;

            node = new Node();
            Class clazz = t.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                if (f.getAnnotation(TreeNodeId.class) != null) {

                    f.setAccessible(true);

                    TreeNodeId annotation = f.getAnnotation(TreeNodeId.class);
                    Class type = annotation.type();
                    if(type == Integer.class){
                        id = f.getInt(t);
                    }
                }

                if (f.getAnnotation(TreeNodePid.class) != null) {
                    f.setAccessible(true);
                    pId = f.getInt(t);
                }

                if (f.getAnnotation(TreeNodeLabel.class) != null) {
                    f.setAccessible(true);
                    label = (String) f.get(t);
                }
            }

            node.setId(id);
            node.setpId(pId);
            node.setName(label);

            nodes.add(node);
        }

        //设置关联关系
        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++) {
                Node m = nodes.get(j);
                if (m.getpId() == n.getId()) {
                    n.getChildren().add(m);
                    m.setParent(n);
                } else if (m.getId() == n.getpId()) {
                    m.getChildren().add(n);
                    n.setParent(m);
                }
            }
        }

        for (Node n : nodes) {
            setNodeIcon(n);
        }

        return nodes;

    }


    /**
     * 设置图标
     * @param nodeIcon
     */
    public static void setNodeIcon(Node nodeIcon) {
        if(nodeIcon.getChildren().size() > 0 && nodeIcon.isExpand()){
            nodeIcon.setIcon(R.drawable.tree_ex);
        }else if(nodeIcon.getChildren().size() > 0 && !nodeIcon.isExpand()){
            nodeIcon.setIcon(R.drawable.tree_ec);
        }else {
            nodeIcon.setIcon(-1);
        }
    }

    /**
     * 从所有节点中过滤出根节点
     * @param datas
     * @param <T>
     * @return
     * @throws IllegalAccessException
     */
    public static <T> List<Node> getSortedNodes(List<T> datas,int defaultExpandLevel) throws IllegalAccessException {
        
        List<Node> result = new ArrayList<>();
        
        List<Node> nodes = convertDatas2Nodes(datas);
        
        List<Node> rootNodes = getRootNodes(nodes);

        for (Node node : rootNodes) {
            addNode(result,node,defaultExpandLevel,1);
        }
        
        return result;
    }

    /**
     * 把一个节点的所有孩子节点都放入result
     * @param result
     * @param node
     * @param defaultExpandLevel
     * @param currentLevel
     */
    private static void addNode(List<Node> result, Node node
                    , int defaultExpandLevel, int currentLevel) {
        result.add(node);
        if(defaultExpandLevel >= currentLevel){
            node.setExpand(true);
        }
        if(node.isLeaf()){
            return;
        }
        for (int i = 0; i < node.getChildren().size(); i++) {
            addNode(result,node.getChildren().get(i),defaultExpandLevel,currentLevel + 1);
        }
    }

    /**
     * 过滤出可见的节点
     *
     * @param nodes
     * @return
     */
    public static List<Node> filterVisibleNodes(List<Node> nodes){
        List<Node> result = new ArrayList<>();

        for (Node node : nodes) {
            if (node.isRoot() || node.isParentExpand()) {
                setNodeIcon(node);
                result.add(node);
            }
        }

        return result;
    }

    private static List<Node> getRootNodes(List<Node> nodes) {
        List<Node> root = new ArrayList<>();
        for (Node node : nodes) {
            if (node.isRoot()) {
                root.add(node);
            }
        }
        return root;
    }
}
