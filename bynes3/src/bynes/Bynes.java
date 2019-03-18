package bynes;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Bynes {
    /*
     * 路径
     */
    private String dataFile = "./file/carnetwork.txt";
    private String requreFile = "./file/carqueries.txt";
    // private String dataFile = "./file/burglarnetwork.txt";
    // private String requreFile = "./file/burglarqueries.txt";
    /**
     * 按顺序从上到下的节点,要保证任意节点的任意祖先序号<=该节点序号
     */
    private List<String> nodes;
    /*
     * 网络的邻接矩阵
     */
    private int tree[][];
    /*
     * 初始的cpt表，Map<节点序号，节点的cpt表>
     */
    private Map<Integer, List<Double>> cpt;
    /*
     * 节点总数
     */
    private int nodeNum;

    public Bynes() {

    }

    /**
     * 从文件读入贝叶斯，和cpt，初始化各参数
     * 
     * @return
     */
    public boolean init() {
        nodes = new ArrayList<>();
        cpt = new HashMap<>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(dataFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int n = scanner.nextInt();
        nodeNum = n;
        tree = new int[n][n];
        scanner.nextLine();
        for (int i = 0; i < n; i++) {
            nodes.add(scanner.next());
        }
        scanner.nextLine();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tree[i][j] = scanner.nextInt();

            }
        }
        scanner.nextLine();
        scanner.nextLine();
        for (int i = 0; i < n; i++) {
            List<Double> list = new ArrayList<>();
            while (true) {
                if (!scanner.hasNextLine()) {
                    break;
                }
                String temp = scanner.nextLine();
                if (temp.equals("")) {
                    break;
                }
                String[] s = temp.split(" ");
                list.add(Double.valueOf(s[0]));
                list.add(Double.valueOf(s[1]));
            }
            cpt.put(i, list);
        }
        scanner.close();
        return true;

    }

    /**
     * 开始
     */
    public void start() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(requreFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (scanner.hasNextLine()) {

            String temp = scanner.nextLine();
            if (!temp.startsWith("P(")) {
                continue;
            }
            // s[1] 存的是节点
            String s[] = temp.split("P\\(|\\)| \\| |, |=");
            Qury qury = new Qury(s, tree, nodes);
            Double result = getResult(qury);
            result = (Double) (double) Math.round(result * 100000) / 100000;
            System.out.println("   " + result + "    " + (1.0 - result));
            ;
        }

    }

    /**
     * 得到问题的概率
     */
    public Double getResult(Qury qury) {
        Double result = getAndp(qury.getAfter());
        Map<Integer, Integer> newMap = new HashMap<>();
        newMap.put(qury.getNode(), 1);
        for (Integer key : qury.getAfter().keySet()) {
            newMap.put(key, qury.getAfter().get(key));
        }
        result = getAndp(newMap) / result;

        return result;

    }

    /**
     * @param list
     *            输如节点list
     * @param map
     *            对应的节点值
     * @return P(list[0],list[1],...list[n-1]),其中为list的长度
     */
    public Double getAndp(Map<Integer, Integer> map) {
        List<Integer> args = new ArrayList<>();
        List<Integer> uarg = new ArrayList<>();
        for (int i = 0; i < nodeNum; i++) {
            if (map.containsKey(i)) {
                args.add(map.get(i));
            } else {
                args.add(new Integer(0));
                uarg.add(i);
            }

        }
        return diedai(uarg, args);

    }

    /**
     * 实现getAndp的迭代功能
     * 
     * @param uarg
     * @param args
     * @return
     */
    private Double diedai(List<Integer> uarg, List<Integer> args) {
        if (uarg.size() == 0) {
            return getAllp(args);
        }
        int flag = uarg.get(0);
        List<Integer> list0 = new ArrayList<>();
        List<Integer> list1 = new ArrayList<>();
        List<Integer> uarg0 = new ArrayList<>();
        List<Integer> uarg1 = new ArrayList<>();
        // 去掉首个元素
        // 复制数组
        for (int i = 1; i < uarg.size(); i++) {
            uarg0.add(uarg.get(i));
            uarg1.add(uarg.get(i));
        }
        for (int i = 0; i < args.size(); i++) {
            list0.add(args.get(i));
            list1.add(args.get(i));
        }
        list0.set(flag, 0);
        list1.set(flag, 1);
        Double temp1 = diedai(uarg0, list0);
        Double temp2 = diedai(uarg1, list1);
        return temp1 + temp2;
        // return diedai(uarg0, list0) + diedai(uarg1, list1);
    }

    /**
     * 
     * @param a
     * @param b
     * @return true若a是b的父节点
     */
    public boolean isfather(int a, int b) {
        return tree[a][b] == 1 ? true : false;
    }

    /*
     * 返回节点a的父亲序号list，按序
     */
    private List<Integer> findFathers(int a) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < nodeNum; i++) {
            if (tree[i][a] == 1) {
                list.add(i);
            }
        }
        return list;
    }

    /**
     * 
     * @param args
     *            [n1,n2,n2,n4,...] 节点依次对应的值
     * @return bayes全概率
     */
    public Double getAllp(List<Integer> args) {
        Double result = (double) 1;
        for (int i = 0; i < nodeNum; i++) {
            // TODO
            int index = (~args.get(i) + 2);
            List<Integer> fathers = findFathers(i);
            int size = fathers.size();
            for (int j = 0; j < size; j++) {
                index += args.get(fathers.get(j)) << (size - j);
            }
            result *= cpt.get(i).get(index);
        }
        return result;

    }

    public static void main(String[] args) {
        Double x = 0.001 * 0.4 * 0.95 * 0.7 * 0.9 + 0.001 * 0.4 * 0.05 * 0.05 + 0.01 * 0.001 * 0.6 * 0.94 * 0.9 * 0.7
                + 0.001 * 0.6 * 0.06 * 0.05 * 0.01;
        double y = 0.1234567894;
        Double d = y;
        System.out.println(d);
        System.out.println(x);
    }
}
