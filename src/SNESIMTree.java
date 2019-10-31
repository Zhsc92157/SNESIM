import Entity.Coords3D;
import Entity.Variable;

import java.util.*;

public class SNESIMTree {

    //SNESIM算法
    SNESIM snesim = new SNESIM();

    public class TreeNode{
        //当前节点的值
        double value;
        //格网层数
        int level;
        //计数器
        int counter;
        //存储孩子节点
        ArrayList<TreeNode> children;
    }

    /**
     * SNESIM 模拟算法主函数
     * @param simGridIndexX 节点在模拟格网中的x索引
     * @param simGridIndexY 节点在模拟格网中的y索引
     * @param simGridIndexZ 节点在模拟格网中的z索引
     * @param level 当前格网的level
     * @return 当前节点的值
     */
    public double simulate(int simGridIndexX, int simGridIndexY, int simGridIndexZ, int level){

        double foundValue = Variable.sg[simGridIndexZ][simGridIndexY][simGridIndexX];
        //若值为NAN则开始模拟
        if (Variable.sg[simGridIndexZ][simGridIndexY][simGridIndexX] == Double.NaN){
            int offset = (int)Math.pow(2,level);
            int sgX, sgY, sgZ;
            int deltaX, deltaY, deltaZ;
            foundValue = Double.NaN;
            int maxConditionalPoints = -1, conditionPointsUsedCnt = 0;
            //基于邻居节点构建一个数据模板
            ArrayList<Double> aPartialTemplate = new ArrayList<>();
            //获取条件数据
            ArrayList<Coords3D> templateFaces = snesim.getTemplateFaces();
            for (int i = 0; i < templateFaces.size(); i++) {
                deltaX = offset* templateFaces.get(i).getX();
                deltaY = offset* templateFaces.get(i).getY();
                deltaZ = offset* templateFaces.get(i).getZ();
                sgX = simGridIndexX + deltaX;
                sgY = simGridIndexY + deltaY;
                sgZ = simGridIndexZ + deltaZ;
                if (!(sgX < 0 || sgX >= Variable.simGridSizeX) &&
                        !(sgY < 0 || sgY >= Variable.simGridSizeY) &&
                        !(sgZ < 0 || sgZ >= Variable.simGridSizeZ)){
                    if (Variable.sg[sgZ][sgY][sgX] != Double.NaN){
                        aPartialTemplate.add(Variable.sg[sgZ][sgY][sgX]);
                    }else{
                        aPartialTemplate.add(Double.NaN);
                    }
                }else{
                    aPartialTemplate.add(Double.NaN);
                }
            }

            //遍历搜索树并且获取到当前模板的值
            ArrayList<TreeNode> currentTreeNode = new ArrayList<>();
            List<ArrayList<TreeNode>> nodesToCheck = new ArrayList<>();
            Map<Double, Integer> conditionalPoints = new HashMap<>();
            int sumCounters = 0;
            int currentLevel = 0, maxLevel = 0;

            //针对根树上所有可能的值
            for (int i = 0; i < Variable.searchTree.size(); i++) {
                conditionPointsUsedCnt = 0;
                maxLevel = 0;
                sumCounters = Variable.searchTree.get(i).counter;
                //在第一层格网初始化孩子节点
                nodesToCheck.add(Variable.searchTree.get(i).children);
                while(nodesToCheck.size() > 0){
                    //自顶向下循环遍历所有节点
                    currentTreeNode = nodesToCheck.get(nodesToCheck.size()-1);
                    nodesToCheck.remove(nodesToCheck.size()-1);
                    for (int j = 0; j < currentTreeNode.size(); j++){
                        if ((aPartialTemplate.get(currentTreeNode.get(j).level - 1)) == Double.NaN ){
                            //如果模板值未定义则转向孩子节点
                            nodesToCheck.add(0,currentTreeNode.get(j).children);
                        }else if (currentTreeNode.get(j).value == aPartialTemplate.get(currentTreeNode.get(j).level-1));{
                            currentLevel = currentTreeNode.get(j).level;
                            //找到模板后转到高一层level的节点
                            if (currentLevel > maxLevel){
                                maxLevel = currentLevel;
                                //在最大的level重新计数
                                sumCounters = currentTreeNode.get(j).counter;
                                conditionPointsUsedCnt++;
                            }else if (currentLevel == maxLevel){
                                //将这个计数器的值加到总计数
                                sumCounters += currentTreeNode.get(j).counter;
                            }

                            //如果当前节点计数器足够大或者使用的条件点数小于给定的限制，则仅继续到子节点
                            if (currentTreeNode.get(j).counter > Variable.MIN_NODE_COUNT &&
                                    (conditionPointsUsedCnt < Variable.MAX_COND_DATA || Variable.MAX_COND_DATA == -1)){
                                //将孩子节点加入需要检查的节点列表
                                nodesToCheck.add(0,currentTreeNode.get(j).children);
                            }
                        }
                    }
                }
                //完成查找值后作合
                if (conditionPointsUsedCnt > maxConditionalPoints){
                    conditionalPoints.clear();
                    conditionalPoints.put(Variable.searchTree.get(i).value, sumCounters);
                    maxConditionalPoints = conditionPointsUsedCnt;
                }else if (conditionPointsUsedCnt == maxConditionalPoints){
                    conditionalPoints.put(Variable.searchTree.get(i).value, sumCounters);
                }
            }

            foundValue = snesim.getCPDF(conditionalPoints,simGridIndexX,simGridIndexY,simGridIndexZ);

        }
        return foundValue;
    }

    /**
     * 针对level层网格进行初始化
     * @param level 当前格网的level
     */
    public void initStartSimulationEachMultipleGrid(int level){

    }



}
