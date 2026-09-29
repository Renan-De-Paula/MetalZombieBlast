import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class InteractRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // Find Player.update to add 'E' interactions
        String target = "this.isMoving = (d3 != 0.0 || d4 != 0.0);";
        String newTarget = target + "\n" +
            "            if (blArray[69]) {\n" +
            "                blArray[69] = false;\n" +
            "                // 1. Doors\n" +
            "                for (Door doorItem : doors) {\n" +
            "                    if (Math.hypot(doorItem.x+doorItem.width/2 - this.x, doorItem.y+doorItem.height/2 - this.y) < 80) {\n" +
            "                        doorItem.toggle();\n" +
            "                    }\n" +
            "                }\n" +
            "                // 2. Chests\n" +
            "                for (LootChest c : ZombieGame.this.chests) {\n" +
            "                    if (c.state == 0 && Math.hypot((c.x+30) - this.x, (c.y+30) - this.y) < 80) {\n" +
            "                        c.state = 1;\n" +
            "                        c.openTime = System.currentTimeMillis();\n" +
            "                    }\n" +
            "                }\n" +
            "                // 3. HomeStorage\n" +
            "                if (Math.hypot(ZombieGame.this.houseCore.x - this.x, ZombieGame.this.houseCore.y - this.y) < 80) {\n" +
            "                    ZombieGame.this.chestWood += ZombieGame.this.wood; ZombieGame.this.wood = 0;\n" +
            "                    ZombieGame.this.chestNails += ZombieGame.this.nails; ZombieGame.this.nails = 0;\n" +
            "                    ZombieGame.this.chestIron += ZombieGame.this.iron; ZombieGame.this.iron = 0;\n" +
            "                    ZombieGame.this.chestScrews += ZombieGame.this.screws; ZombieGame.this.screws = 0;\n" +
            "                    ZombieGame.this.chestLead += ZombieGame.this.lead; ZombieGame.this.lead = 0;\n" +
            "                }\n" +
            "            }";
        code = code.replace(target, newTarget);
        
        Files.write(f.toPath(), code.getBytes());
    }
}
