import Entity.Coords3D;
import Entity.Variable;
import Entity.TreeNode;

import java.util.*;

public class SNESIMTree {

    //SNESIM算法
    static SNESIM snesim = new SNESIM();

    public void startSimulation(){
        MPSAlgorithm mpsAlgorithm = new MPSAlgorithm();
        mpsAlgorithm.startSimulation();
    }


    /**
     * SNESIM 模拟算法主函数
     * @param simGridIndexX 节点在模拟格网中的x索引
     * @param simGridIndexY 节点在模拟格网中的y索引
     * @param simGridIndexZ 节点在模拟格网中的z索引
     * @param level 当前格网的level
     * @return 当前节点的值
     */
    public static double simulate(int simGridIndexX, int simGridIndexY, int simGridIndexZ, int level){

        double foundValue = Variable.sg[simGridIndexZ][simGridIndexY][simGridIndexX];
        //若值为NAN则开始模拟
        if (Double.isNaN(Variable.sg[simGridIndexZ][simGridIndexY][simGridIndexX])){
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
                    if (!Double.isNaN(Variable.sg[sgZ][sgY][sgX])){
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
                sumCounters = Variable.searchTree.get(i).getCounter();
                //在第一层格网初始化孩子节点
                nodesToCheck.add(Variable.searchTree.get(i).getChildren());
                while(nodesToCheck.size() > 0){
                    //自顶向下循环遍历所有节点
                    currentTreeNode = nodesToCheck.get(nodesToCheck.size()-1);
                    nodesToCheck.remove(nodesToCheck.size()-1);
                    for (int j = 0; j < currentTreeNode.size(); j++){
                        if (Double.isNaN((aPartialTemplate.get(currentTreeNode.get(j).getLevel() - 1)))){
                            //如果模板值未定义则转向孩子节点
                            nodesToCheck.add(0,currentTreeNode.get(j).getChildren());
                        }else if (currentTreeNode.get(j).getValue() == aPartialTemplate.get(currentTreeNode.get(j).getLevel()-1));{
                            currentLevel = currentTreeNode.get(j).getLevel();
                            //找到模板后转到高一层level的节点
                            if (currentLevel > maxLevel){
                                maxLevel = currentLevel;
                                //在最大的level重新计数
                                sumCounters = currentTreeNode.get(j).getCounter();
                                conditionPointsUsedCnt++;
                            }else if (currentLevel == maxLevel){
                                //将这个计数器的值加到总计数
                                sumCounters += currentTreeNode.get(j).getCounter();
                            }

                            //如果当前节点计数器足够大或者使用的条件点数小于给定的限制，则仅继续到子节点
                            if (currentTreeNode.get(j).getCounter() > Variable.MIN_NODE_COUNT &&
                                    (conditionPointsUsedCnt < Variable.MAX_COND_DATA || Variable.MAX_COND_DATA == -1)){
                                //将孩子节点加入需要检查的节点列表
                                nodesToCheck.add(0,currentTreeNode.get(j).getChildren());
                            }
                        }
                    }
                }
                //完成查找值后作合
                if (conditionPointsUsedCnt > maxConditionalPoints){
                    conditionalPoints.clear();
                    conditionalPoints.put(Variable.searchTree.get(i).getValue(), sumCounters);
                    maxConditionalPoints = conditionPointsUsedCnt;
                }else if (conditionPointsUsedCnt == maxConditionalPoints){
                    conditionalPoints.put(Variable.searchTree.get(i).getValue(), sumCounters);
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
    public static void initStartSimulationEachMultipleGrid(int level){
        int totalLevel = Variable.TOTAL_GRIDS_LEVEL;
        //自适应模板大小，稍后使用
        //初始化最小大小，最小的最优模板是4*4*4的模板
        int minTemplateX, minTemplateY, minTemplateZ;
        minTemplateX = Math.min(4, Variable.templateSizeX);
        minTemplateY = Math.min(4, Variable.templateSizeY);
        minTemplateZ = Math.min(4, Variable.templateSizeZ);

        //当前模板
        int templateX = minTemplateX, templateY = minTemplateY, templateZ = minTemplateZ;
        //将模板大小调整到适合当前格网层，层数越低，模板越小
        templateX = (int) (Variable.templateSizeX - (totalLevel - level)*(Math.ceil(templateX-minTemplateX)/totalLevel));
        templateY = (int) (Variable.templateSizeY - (totalLevel - level)*(Math.ceil(templateY-minTemplateY)/totalLevel));
        templateZ = (int) (Variable.templateSizeZ - (totalLevel - level)*(Math.ceil(templateZ-minTemplateZ)/totalLevel));
        //构建模板结构
        snesim.constructTemplateFaces(templateX, templateY, templateZ);


        /**
         * 扫描训练图像并构建搜索树
         */
        //构建搜索树
        int offset = (int) Math.pow(2, level);

        System.out.println("level: " + level +" offset: " + offset);
        System.out.println("原始模板X大小: " + Variable.templateSizeX + " 调整后的模板X大小: " + templateX);
        System.out.println("原始模板Y大小: " + Variable.templateSizeY + " 调整后的模板Y大小: " + templateY);
        System.out.println("原始模板Z大小: " + Variable.templateSizeZ + " 调整后的模板Z大小: " + templateZ);

        int tiX, tiY, tiZ;
        int deltaX, deltaY, deltaZ;
        int nodeCnt = 0;
        boolean foundExistingValue = false;
        int foundIdx = 0;
        int totalNodes = Variable.tiDimX * Variable.tiDimY * Variable.tiDimZ;
        int lastProgress = 0;
        List<TreeNode> currentNode = Variable.searchTree;

        for (int z = 0; z < Variable.tiDimZ; z+=1){
            for (int y = 0; y < Variable.tiDimY; y++) {
                for (int x = 0; x < Variable.tiDimX; x++) {
                    //针对每一个像素
                    nodeCnt++;
                    //初始化当前节点到根节点
                    currentNode = Variable.searchTree;
                    for (int i = 0; i < Variable.templateFaces.size(); i++) {
                        //获取更深层的模板或者得到更高层的节点
                        deltaX = offset * Variable.templateFaces.get(i).getX();
                        deltaY = offset * Variable.templateFaces.get(i).getY();
                        deltaZ = offset * Variable.templateFaces.get(i).getZ();
                        tiX = x + deltaX;
                        tiY = y + deltaY;
                        tiZ = z + deltaZ;

                        foundExistingValue = false;
                        foundIdx = 0;
                        //检查是否越界或者值为nan
                        if ((tiX < 0 || tiX >= Variable.tiDimX)
                                || (tiY < 0 || tiY >= Variable.tiDimY)
                                || (tiZ < 0 || tiZ >= Variable.tiDimZ)
                                || Double.isNaN(Variable.ti[tiZ][tiY][tiX])){
                            break;
                        }else{
                            //在当前节点搜索TI单元的值
                            for (int j = 0; j < currentNode.size(); j++) {
                                if (Variable.ti[tiZ][tiY][tiX] == currentNode.get(j).getValue()){
                                    //当前值存在，当前节点的计数器++
                                    foundExistingValue = true;
                                    currentNode.get(j).setCounter(currentNode.get(j).getCounter()+1);
                                    foundIdx = j;
                                    break;
                                }
                            }
                            /**
                             * 如果当前值未找到则添加一个新的值（训练图像的当前值）
                             */
                            if (!foundExistingValue){
                                TreeNode node = new TreeNode();
                                node.setCounter(1);
                                node.setValue(Variable.ti[tiZ][tiY][tiX]);
                                node.setLevel(i);
                                currentNode.add(node);
                                foundIdx = (int) currentNode.size() - 1;
                            }
                            //将当前节点转换到孩子节点
                            currentNode = currentNode.get(foundIdx).getChildren();
                        }
                    }
                }
            }
        }

        System.out.println("搜索树构建完成");

    }



}
