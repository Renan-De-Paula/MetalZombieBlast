import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class HomeStorageRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // 1. Storage Variables
        String vars = "public int maxBackpackCapacity = 20;";
        String newVars = vars + "\n    public int chestWood = 0, chestIron = 0, chestLead = 0, chestNails = 0, chestScrews = 0;\n" +
            "    public int maxChestCapacity = 50;";
        code = code.replace(vars, newVars);
        
        // 2. HUD for Chest
        String hudEnd = "graphics2D.drawString(\"MOCHILA: \" + currentCapacity + \" / \" + this.maxBackpackCapacity, 15, 100);";
        String newHudEnd = hudEnd + "\n" +
            "        int chestCurrent = this.chestWood + this.chestIron + this.chestLead + this.chestNails + this.chestScrews;\n" +
            "        graphics2D.drawString(\"BAU DA CASA: \" + chestCurrent + \" / \" + this.maxChestCapacity + \" (Aperte E no centro da casa para depositar)\", 15, 120);";
        code = code.replace(hudEnd, newHudEnd);
        
        // 3. Deposit interaction 'E' near the core (center of house, which acts as the chest)
        // Previous door toggle:
        // if (blArray[69]) { ... for (Door d : doors) if (...) d.toggle(); }
        String eKey = "for (Door d : this.doors) {\n" +
            "                if (Math.hypot((d.x+d.width/2.0) - this.x, (d.y+d.height/2.0) - this.y) < 80) d.toggle();\n" +
            "            }";
        String newEKey = eKey + "\n" +
            "            if (Math.hypot(this.houseCore.x - this.x, this.houseCore.y - this.y) < 100) {\n" +
            "                // Deposit all\n" +
            "                int totalToDeposit = ZombieGame.this.wood + ZombieGame.this.iron + ZombieGame.this.lead + ZombieGame.this.nails + ZombieGame.this.screws;\n" +
            "                int chestCurrent = ZombieGame.this.chestWood + ZombieGame.this.chestIron + ZombieGame.this.chestLead + ZombieGame.this.chestNails + ZombieGame.this.chestScrews;\n" +
            "                if (chestCurrent + totalToDeposit <= ZombieGame.this.maxChestCapacity) {\n" +
            "                    ZombieGame.this.chestWood += ZombieGame.this.wood; ZombieGame.this.wood = 0;\n" +
            "                    ZombieGame.this.chestIron += ZombieGame.this.iron; ZombieGame.this.iron = 0;\n" +
            "                    ZombieGame.this.chestLead += ZombieGame.this.lead; ZombieGame.this.lead = 0;\n" +
            "                    ZombieGame.this.chestNails += ZombieGame.this.nails; ZombieGame.this.nails = 0;\n" +
            "                    ZombieGame.this.chestScrews += ZombieGame.this.screws; ZombieGame.this.screws = 0;\n" +
            "                }\n" +
            "            }";
        code = code.replace(eKey, newEKey);

        Files.write(f.toPath(), code.getBytes());
    }
}
