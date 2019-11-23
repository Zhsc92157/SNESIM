import Entity.Variable;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        Variable variable = new Variable();
        SNESIMTree snesimTree = new SNESIMTree();
        snesimTree.startSimulation();
    }
}
