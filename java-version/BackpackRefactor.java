import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class BackpackRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // 1. Add Backpack variables
        String hudEnd = "graphics2D.drawString(\"Mad:\" + this.wood + \" Pregos:\" + this.nails + \" Ferro:\" + this.iron + \" Parf:\" + this.screws + \" Chum:\" + this.lead, 15, 80);";
        String newHudEnd = hudEnd + "\n" +
            "        int currentCapacity = this.wood + this.nails + this.iron + this.screws + this.lead;\n" +
            "        graphics2D.drawString(\"MOCHILA: \" + currentCapacity + \" / \" + this.maxBackpackCapacity, 15, 100);";
        code = code.replace(hudEnd, newHudEnd);
        
        String vars = "public int wood = 10, iron = 0, lead = 0, nails = 10, screws = 0, fuelCans = 0;";
        String newVars = vars + "\n    public int maxBackpackCapacity = 20;";
        code = code.replace(vars, newVars);
        
        // 2. Block pickup if full
        String oldPickup = "else if (iteratorD.type == 3) this.wood += iteratorD.value;\n" +
            "            else if (iteratorD.type == 4) this.nails += iteratorD.value;\n" +
            "            else if (iteratorD.type == 5) this.iron += iteratorD.value;\n" +
            "            else if (iteratorD.type == 6) this.screws += iteratorD.value;\n" +
            "            else if (iteratorD.type == 7) this.lead += iteratorD.value;\n" +
            "            else if (iteratorD.type >= 8 && iteratorD.type <= 13) {\n" +
            "                this.globalAmmo[iteratorD.type - 8] += iteratorD.value;\n" +
            "            }\n" +
            "            itD.remove();";
        String newPickup = "else if (iteratorD.type >= 3 && iteratorD.type <= 7) {\n" +
            "                int currentCapacity = this.wood + this.nails + this.iron + this.screws + this.lead;\n" +
            "                if (currentCapacity < this.maxBackpackCapacity) {\n" +
            "                    if (iteratorD.type == 3) this.wood += iteratorD.value;\n" +
            "                    else if (iteratorD.type == 4) this.nails += iteratorD.value;\n" +
            "                    else if (iteratorD.type == 5) this.iron += iteratorD.value;\n" +
            "                    else if (iteratorD.type == 6) this.screws += iteratorD.value;\n" +
            "                    else if (iteratorD.type == 7) this.lead += iteratorD.value;\n" +
            "                    itD.remove();\n" +
            "                }\n" +
            "            } else if (iteratorD.type >= 8 && iteratorD.type <= 13) {\n" +
            "                this.globalAmmo[iteratorD.type - 8] += iteratorD.value;\n" +
            "                itD.remove();\n" +
            "            } else {\n" +
            "                itD.remove();\n" +
            "            }";
        code = code.replace(oldPickup, newPickup);

        Files.write(f.toPath(), code.getBytes());
    }
}
