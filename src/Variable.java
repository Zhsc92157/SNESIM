public class Variable {
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
    public int simGridSizeX = 80;
    public int simGridSizeY = 80;
    public int simGridSizeZ = 1;
    //Simulation Grid World min X
    public int simGridWorldMinX = 0;
    public int simGridWorldMinY = 0;
    public int simGridWorldMinZ = 0;
    //Simulation Grid cell size
    public int simGridCellSizeX = 1;
    public int simGridCellSizeY = 1;
    public int simGridCellSizeZ = 1;

    public static String TRAINING_FILE = "ti_cb_4x4_40_40_1.dat";

}
