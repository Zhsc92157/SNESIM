import Util.CoordsUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class SNESIM {

    //list of available faces in the template
    Vector<Coords3D> templateFaces = new Vector<>();

    /**
     * Construct template faces and sort them around template center
     * @param sizeX template size X
     * @param sizeY template size Y
     * @param sizeZ template size Z
     */
    public void constructTemplateFaces(int sizeX, int sizeY, int sizeZ){
        int templateCenterX = (int)Math.floor((double)sizeX / 2.0);
        int templateCenterY = (int)Math.floor((double)sizeY / 2.0);
        int templateCenterZ = (int)Math.floor((double)sizeZ / 2.0);
        int totalTemplateIndices = sizeX*sizeY*sizeZ;
        int totalTemplates = 0;

        //create a template path
        int [] templatePath = initTempPath(sizeX,sizeY,sizeZ);
        Arrays.sort(templatePath);

        //init the faces with the center face (0,0,0)
        templateFaces.clear();
        templateFaces.add(new Coords3D());
        int offsetX, offsetY, offsetZ;
        for (int i = 0; i < totalTemplateIndices; i++) {
            int[] coords3d = CoordsUtil.oneD2ThreeD(templatePath[i],sizeX,sizeY);
            offsetX = coords3d[0] - templateCenterX;
            offsetY = coords3d[1] - templateCenterY;
            offsetZ = coords3d[2] - templateCenterZ;
            //ignore the center point
            if (offsetX!=0 || offsetY!=0 || offsetZ!=0)
                templateFaces.add(new Coords3D(offsetX,offsetY,offsetZ));
        }
    }

    /**
     * 初始化一个模板路径
     * @param sizeX 模板大小x
     * @param sizeY 模板大小y
     * @param sizeZ 模板大小z
     * @return path 模板路径
     */
    private int [] initTempPath(int sizeX, int sizeY, int sizeZ) {
        int [] path = new int[sizeX*sizeY*sizeZ];
        int cnt = 0;
        for (int z = 0;z<sizeZ;z++){
            for (int y = 0;y<sizeY;y++){
                for (int x = 0;x<sizeX;x++){
                    path[cnt] = cnt++;
                }
            }
        }
        return path;
    }

    /**
     * 返回cpdf值 累计计算条件概率分布值获得节点的值
     * @param conditionalPoints 条件节点的列表
     * @param x 当前节点的坐标x
     * @param y 当前节点坐标y
     * @param z 当前节点坐标z
     * @return 计算得出的cpdf值
     */
    public double getCPDF(Map<Double,Integer> conditionalPoints, int x, int y, int z){
        double foundValue = Double.NaN;
        // 找到所有的条件概率值, 并且找到发生当前数据事件的次数
        int totalCounter = 0;
        for (Map.Entry<Double,Integer> entry: conditionalPoints.entrySet()){
            totalCounter += entry.getValue();
        }
        // 从训练图像中获取到条件概率  条件数据值的数量÷totalCounter
        Map<Double, Double> probabilitiesFromTI = new HashMap<>();
        for (Map.Entry<Double,Integer> entry:conditionalPoints.entrySet()){
            probabilitiesFromTI.put(entry.getKey(),(double)(entry.getValue()/totalCounter));
        }

        //综合考虑之后得出当前节点的概率值
        Map<Double,Double> probabilitiesCombined = new HashMap<>();

        //todo 在获取条件数据时需要考虑其本身的概率

        double cumulateValue = 0;
        for (Map.Entry<Double,Double> entry: probabilitiesFromTI.entrySet()){
            cumulateValue += entry.getValue();
            probabilitiesCombined.put((double)cumulateValue,entry.getKey());
        }

        double random = Math.random()/(0x7fff);

        for (Map.Entry<Double,Double> entry: probabilitiesCombined.entrySet()){
            if (entry.getKey() >= random){
                foundValue = entry.getValue();
                break;
            }
        }
        return foundValue;
    }

}
