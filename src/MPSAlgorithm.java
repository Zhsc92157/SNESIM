import Entity.Coords3D;
import Entity.Variable;
import Util.CoordsUtil;

import javax.print.attribute.standard.RequestingUserName;
import java.io.IOException;
import java.sql.Time;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import static Entity.Variable.showSimulationGrids;

public class MPSAlgorithm {

    //模拟路径
    List<Integer> simulationPath = new ArrayList<>();


    /**
     * 开始SNESIM模拟
     */
    public void startSimulation() {

        System.out.println("___________________________________________");
        System.out.println("_________  中  国  地  质  大  学  _________");
        System.out.println("______China University of GeoSciences______");
        System.out.println("_________2019/09           2019/11_________");
        System.out.println("_________MPS                SNESIM_________");

        //todo 获取输出路径
        String outputFile = "output.txt";

        //开始模拟
        double totalSecs = 0;
        Clock endNode, beginRealization, endRealization;
        double elapsedRealizationSecs, elapsedNodeSecs;
        int nodeEstimatedSeconds, seconds, hours, minutes, lastProgress = 0;
        //确定多重格网附近最近的硬数据值
        List<Coords3D> allocatedNodesFromHardData = new ArrayList<>();
        List<Coords3D> nodeToPutBack = new ArrayList<>();
        int nodeCnt = 0, totalNodes = 0;
        int sg1DIdx, offset;

        for (int n = 0; n < Variable.REALIZATION_NUMBERS; n++) {
            beginRealization = Clock.systemDefaultZone();

            //多重格网
            for (int level = Variable.TOTAL_GRIDS_LEVEL; level >= 0; level--) {
                //初始化每层格网的搜索树
                SNESIMTree.initStartSimulationEachMultipleGrid(level);
                //对每层格网由粗到精
                offset = (int) Math.pow(2, level);
                //对每层格网内的模拟路径初始化
                simulationPath.clear();

                nodeCnt = 0;
                totalNodes = (Variable.simGridSizeX / offset) * (Variable.simGridSizeY / offset) * (Variable.simGridSizeZ / offset);
                for (int z = 0; z < Variable.simGridSizeZ; z+=offset) {
                    for (int y = 0; y < Variable.simGridSizeY; y+=offset) {
                        for (int x = 0; x < Variable.simGridSizeX; x+=offset) {
                            //将三维坐标转换为一维便于初始化路径
                            sg1DIdx = CoordsUtil.threeD2OneD(x, y, z, Variable.simGridSizeX, Variable.simGridSizeY);
                            simulationPath.add(sg1DIdx);
                        }
                    }
                }
                //展示模拟结果
                //对随机模拟路径上的每个值
                int progressionCnt = 0;
                totalNodes = simulationPath.size();

                for (int i = 0; i < simulationPath.size(); i++) {
                    //获取到当前节点的三维坐标
                    int[] threeDCoords = CoordsUtil.oneD2ThreeD(simulationPath.get(i), Variable.simGridSizeX, Variable.simGridSizeY);
                    int sg_idxX = threeDCoords[0];
                    int sg_idxY = threeDCoords[1];
                    int sg_idxZ = threeDCoords[2];
                    //如果当前节点值为NaN，则模拟出一个值
                    if (Double.isNaN(Variable.sg[sg_idxZ][sg_idxY][sg_idxX])){
                        Variable.sg[sg_idxZ][sg_idxY][sg_idxX] = SNESIMTree.simulate(sg_idxX, sg_idxY, sg_idxZ, level);
                    }
                }
            }

            showSimulationGrids();

        }

    }

}
