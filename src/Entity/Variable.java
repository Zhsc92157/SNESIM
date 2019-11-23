package Entity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置变量文件
 */
public class Variable {

    public Variable() throws IOException {
        readTrainingFile();
        hdg = sg;
        tg1 = sg;
        tg2 = sg;
    }

    // Number of realizations
    public static int REALIZATION_NUMBERS = 1;
    //random seed
    public static int RANDOM_SEED = 1;
    //number of multiple grids(start from 0)
    public static int TOTAL_GRIDS_LEVEL = 3;
    //minimum node count (if not set any limit = 0)
    public static int MIN_NODE_COUNT = 0;
    //maximum number conditional data(0 for all)
    public static int MAX_COND_DATA = -1;
    //simulation template size
    public static int templateSizeX = 7;
    public static int templateSizeY = 7;
    public static int templateSizeZ = 1;
    //simulation grid size
    public static int simGridSizeX = 80;
    public static int simGridSizeY = 80;
    public static int simGridSizeZ = 1;
    //Simulation Grid World min X
    public int simGridWorldMinX = 0;
    public int simGridWorldMinY = 0;
    public int simGridWorldMinZ = 0;
    //Simulation Grid cell size
    public int simGridCellSizeX = 1;
    public int simGridCellSizeY = 1;
    public int simGridCellSizeZ = 1;
    //size of training image
    public static int tiDimX;
    public static int tiDimY;
    public static int tiDimZ;
    //Training file
    public static String TRAINING_FILE = "ti_cb_4x4_40_40_1.dat";
    public static double[][][] ti;
    //simulation grid
    public static double[][][] sg = new double[simGridSizeZ][simGridSizeY][simGridSizeX];
    //hard data grid
    public static double[][][] hdg;
    //temp grid 1
    public static double[][][] tg1;
    //temp grid 2
    public static double[][][] tg2;

    //搜索树
    public static List<TreeNode> searchTree = new ArrayList<>();
    //保存模板相
    public static List<Coords3D> templateFaces = new ArrayList<>();

    /**
     * 读取训练图像
     * @throws IOException
     */
    public void readTrainingFile() throws IOException {
        int x = 0;
        int y = 0;
        int z = 0;
        System.out.println("读取训练图像");
        File file  = new File(TRAINING_FILE);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String strLine = null;
        int lineCount = 0;
        List<Double> value = new ArrayList<>();
        while(null != (strLine = bufferedReader.readLine())){
            if (lineCount != 0){
                strLine = strLine.replace(" ","");
            }else{
                String[] s = strLine.split(" ");
                x = Integer.parseInt(s[0]);
                y = Integer.parseInt(s[1]);
                z = Integer.parseInt(s[2]);
                tiDimX = x;
                tiDimY = y;
                tiDimZ = z;
                ti = new double[z][y][x];
            }
            if (lineCount >= 3){
                value.add(Double.parseDouble(strLine));
                System.out.println(Double.parseDouble(strLine));
            }
            lineCount++;
        }
        int cnt = 0;
        for (int i = 0; i < z; i++) {
            for (int j = 0; j < x; j++){
                for (int k = 0; k < y; k++){
                    ti[i][k][j] = value.get(cnt);
                    cnt++;
                }
            }
        }

        System.out.println("读取图像完成");

        hdg = sg;
        tg1 = sg;
        tg2 = sg;

    }

    /**
     * 显示模拟格网结果
     */
    public static void showSimulationGrids(){
        for (int z = 0; z < simGridSizeZ; z++) {
            System.out.println("Z: "+ (z + 1) + "/" + simGridSizeZ);
            for (int y = 0; y < simGridSizeY; y++) {
                for (int x = 0; x < simGridSizeX; x++) {
                    System.out.print(sg[z][y][x] + " ");
                }
                System.out.println();
            }
        }
    }

}
