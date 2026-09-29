import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class HouseRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        String oldBuild = "private void buildHouse() {\n" +
            "        int n = 40;\n" +
            "        int n2 = 150;\n" +
            "        int n3 = 150;\n" +
            "        int n4 = 500;\n" +
            "        int n5 = 300;\n" +
            "        this.solidWalls.add(new SolidWall(this, n2, n3, 150, n));\n" +
            "        this.windows.add(new Window(this, n2 + 150, n3, 200, n, true));\n" +
            "        this.solidWalls.add(new SolidWall(this, n2 + 350, n3, 150, n));\n" +
            "        this.solidWalls.add(new SolidWall(this, n2, n3 + n5 - n, 150, n));\n" +
            "        this.windows.add(new Window(this, n2 + 150, n3 + n5 - n, 200, n, true));\n" +
            "        this.solidWalls.add(new SolidWall(this, n2 + 350, n3 + n5 - n, 150, n));\n" +
            "        this.solidWalls.add(new SolidWall(this, n2, n3 + n, n, 80));\n" +
            "        this.windows.add(new Window(this, n2, n3 + n + 80, n, 60, false));\n" +
            "        this.solidWalls.add(new SolidWall(this, n2, n3 + n + 140, n, n5 - n * 2 - 140));\n" +
            "        this.solidWalls.add(new SolidWall(this, n2 + n4 - n, n3 + n, n, 80));\n" +
            "        this.windows.add(new Window(this, n2 + n4 - n, n3 + n + 80, n, 60, false));\n" +
            "        this.solidWalls.add(new SolidWall(this, n2 + n4 - n, n3 + n + 140, n, n5 - n * 2 - 140));\n" +
            "    }";

        String newBuild = "private void buildHouse() {\n" +
            "        this.solidWalls.add(new SolidWall(this, 150, 150, 210, 40));\n" +
            "        this.doors.add(new Door(360, 150, 80, 40, true));\n" +
            "        this.solidWalls.add(new SolidWall(this, 440, 150, 210, 40));\n" +
            "        \n" +
            "        this.solidWalls.add(new SolidWall(this, 150, 410, 210, 40));\n" +
            "        this.doors.add(new Door(360, 410, 80, 40, true));\n" +
            "        this.solidWalls.add(new SolidWall(this, 440, 410, 210, 40));\n" +
            "        \n" +
            "        this.solidWalls.add(new SolidWall(this, 150, 190, 40, 40));\n" +
            "        this.windows.add(new Window(this, 150, 230, 40, 60, false));\n" +
            "        this.solidWalls.add(new SolidWall(this, 150, 290, 40, 20));\n" +
            "        this.windows.add(new Window(this, 150, 310, 40, 60, false));\n" +
            "        this.solidWalls.add(new SolidWall(this, 150, 370, 40, 40));\n" +
            "        \n" +
            "        this.solidWalls.add(new SolidWall(this, 610, 190, 40, 40));\n" +
            "        this.windows.add(new Window(this, 610, 230, 40, 60, false));\n" +
            "        this.solidWalls.add(new SolidWall(this, 610, 290, 40, 20));\n" +
            "        this.windows.add(new Window(this, 610, 310, 40, 60, false));\n" +
            "        this.solidWalls.add(new SolidWall(this, 610, 370, 40, 40));\n" +
            "    }";

        code = code.replace(oldBuild, newBuild);

        Files.write(f.toPath(), code.getBytes());
    }
}
