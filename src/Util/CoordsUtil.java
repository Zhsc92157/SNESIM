package Util;

public class CoordsUtil {
    /**
     * 一维坐标转换为三维坐标
     * @param oneDIndex 一维坐标索引
     * @param dimX 三维矩阵的X维
     * @param dimY 三维矩阵的Y维
     * @return result 数组 0 x 1 y 2 z
     */
    public static int[] oneD2ThreeD(int oneDIndex, int dimX, int dimY){
        int z = oneDIndex/(dimX*dimY);
        int y = (oneDIndex - z*dimX*dimY)/dimX;
        int x = oneDIndex - dimX * (y+dimY*z);
        return new int[] {x, y, z};
    }

}
