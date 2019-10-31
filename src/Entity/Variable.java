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
    public int templateSizeX = 7;
    public int templateSizeY = 7;
    public int templateSizeZ = 1;
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
    //Training file
    public static String TRAINING_FILE = "ti_cb_4x4_40_40_1.dat";
    //simulation grid
    public static double[][][] sg;
    //hard data grid
    public static double[][][] hdg;
    //temp grid 1
    public static double[][][] tg1;
    //temp grid 2
    public static double[][][] tg2;

    //搜索树
    public static ArrayList<SNESIMTree.TreeNode> searchTree = new ArrayList<>();


    /**
     * 读取训练图像
     * @throws IOException
     */
    public void readTrainingFile() throws IOException {
        int x = 0;
        int y = 0;
        int z = 0;
        System.out.print("读取训练图像");
        File file  = new File(TRAINING_FILE);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String strLine = null;
        int lineCount = 0;
        List<Double> value = new ArrayList<>();
        while(null != (strLine = bufferedReader.readLine())){
            if (lineCount != 0){
                strLine = strLine.replace(" ","");
            }else{
                String s[] = strLine.split(" ");
                x = Integer.parseInt(s[0]);
                y = Integer.parseInt(s[1]);
                z = Integer.parseInt(s[2]);
                sg = new double[x][y][z];
            }
            if (lineCount >= 3){
                value.add(Double.parseDouble(strLine));
            }
            lineCount++;
        }
        int cnt = 0;
        for (int i = 0; i < z; i++) {
            for (int j = 0; j < x; j++){
                for (int k = 0; k < y; k++){
                    sg[i][k][j] = value.get(cnt);
                    cnt++;
                }
            }
        }
        System.out.println("读取图像完成");
    }



}
