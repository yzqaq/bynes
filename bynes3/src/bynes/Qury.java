package bynes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Qury {
    private String nodeName = "";
    private int node;
    private int[][] tree;
    private Map<Integer, Integer> after;
    private List<Integer> afters;

    /**
     * 将问题装化
     * 
     * @param s
     */
    public Qury(String[] s, int[][] tree, List<String> nodes) {
        int length = s.length;
        this.tree = tree;
        after = new HashMap<>();
        afters = new ArrayList<>();
        nodeName = s[1];
        this.node = nodes.indexOf(nodeName);
        System.out.println(nodeName);
        if (length > 2) {
            for (int i = 3; i < length; i += 2) {

                if (s[i].equals("true")) {
                    after.put(nodes.indexOf(s[i - 1]), 1);
                    afters.add(nodes.indexOf(s[i - 1]));
                } else {
                    after.put(nodes.indexOf(s[i - 1]), 0);
                    afters.add(nodes.indexOf(s[i - 1]));
                }
            }
        }

    }

    /**
     * 
     * @param node
     *            节点
     * @param after
     *            节点后值
     * @param tree
     *            贝叶斯
     * @param nodes
     *            节点总集
     */
    public Qury(int node, Map<Integer, Integer> after, int[][] tree, List<String> nodes) {
        this.node = node;
        this.after = after;
        this.tree = tree;
        this.nodeName = nodes.get(node);
        List<Integer> afters = new ArrayList<>();
        for (Integer i : after.keySet()) {
            afters.add(i);
        }
        Collections.sort(afters);
        this.afters = afters;

    }

    /**
     * 若afters全部是nodeName的副节点，则返回true，否则返回false
     * 
     * @return
     */
    public boolean isRuled() {
        for (Integer i : afters) {
            if (tree[i][node] == 0) {
                return false;
            }
        }
        return true;
    }

    public String getNodeName() {
        return nodeName;
    }

    public int getNode() {
        return node;
    }

    public Map<Integer, Integer> getAfter() {
        return after;
    }

    public List<Integer> getAfters() {
        return afters;
    }

}
