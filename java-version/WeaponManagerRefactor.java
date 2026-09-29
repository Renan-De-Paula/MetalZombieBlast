import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class WeaponManagerRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // 1. Start with no weapon
        String initWeapon = "this.player.weapon1 = this.comuns[0];";
        String newInitWeapon = "this.player.weapon1 = null;";
        code = code.replace(initWeapon, newInitWeapon);
        
        // 2. Drop class modification
        String dropClass = "class Drop {\n" +
            "        double x;\n" +
            "        double y;\n" +
            "        int type;\n" +
            "        int value;";
        String newDropClass = dropClass + "\n        Weapon weaponDrop = null;";
        code = code.replace(dropClass, newDropClass);
        
        // Weapon Drop Constructor
        String dropInit = "Drop(ZombieGame zombieGame, double d, double d2, int n, int n2) {\n" +
            "            this.x = d;\n" +
            "            this.y = d2;\n" +
            "            this.type = n;\n" +
            "            this.value = n2;\n" +
            "        }";
        String newDropInit = dropInit + "\n" +
            "        Drop(ZombieGame zombieGame, double d, double d2, Weapon w) {\n" +
            "            this.x = d;\n" +
            "            this.y = d2;\n" +
            "            this.type = 18;\n" +
            "            this.value = 1;\n" +
            "            this.weaponDrop = w;\n" +
            "        }";
        code = code.replace(dropInit, newDropInit);
        
        // Drop drawing for Weapon (type 18)
        String drawDrop = "} else if (this.type == 2) {";
        String newDrawDrop = "} else if (this.type == 18) {\n" +
            "                graphics2D.setColor(this.weaponDrop.color);\n" +
            "                graphics2D.fillRect((int)this.x - 6, (int)this.y - 6, 12, 12);\n" +
            "                graphics2D.setColor(java.awt.Color.WHITE);\n" +
            "                graphics2D.drawString(this.weaponDrop.name, (int)this.x - 15, (int)this.y - 15);\n" +
            "            } else if (this.type == 2) {";
        code = code.replace(drawDrop, newDrawDrop);
        
        // 3. Drop Pickup Logic
        String pickupLogic = "if (iteratorD.type == 0) {\n" +
            "                    this.collectExp(iteratorD.value);\n" +
            "                } else if (iteratorD.type == 1) {\n" +
            "                    this.coins += iteratorD.value;\n" +
            "                } else if (iteratorD.type == 2) {\n" +
            "                    this.materials += iteratorD.value;\n" +
            "                }";
        String newPickupLogic = pickupLogic + "\n" +
            "                else if (iteratorD.type >= 8 && iteratorD.type <= 13) {\n" +
            "                    this.globalAmmo[iteratorD.type - 8] += iteratorD.value;\n" +
            "                } else if (iteratorD.type == 18) {\n" +
            "                    this.equipWeapon(iteratorD.weaponDrop);\n" +
            "                } else {\n" +
            "                    int cap = this.wood + this.nails + this.iron + this.screws + this.lead + this.food + this.medkit + this.crystals + this.fuelCans;\n" +
            "                    if (cap + iteratorD.value > this.maxBackpackCapacity) continue; // Backpack full!\n" +
            "                    if (iteratorD.type == 3) this.wood += iteratorD.value;\n" +
            "                    else if (iteratorD.type == 4) this.nails += iteratorD.value;\n" +
            "                    else if (iteratorD.type == 5) this.iron += iteratorD.value;\n" +
            "                    else if (iteratorD.type == 6) this.screws += iteratorD.value;\n" +
            "                    else if (iteratorD.type == 7) this.lead += iteratorD.value;\n" +
            "                    else if (iteratorD.type == 14) this.food += iteratorD.value;\n" +
            "                    else if (iteratorD.type == 15) this.medkit += iteratorD.value;\n" +
            "                    else if (iteratorD.type == 16) this.crystals += iteratorD.value;\n" +
            "                    else if (iteratorD.type == 17) this.fuelCans += iteratorD.value;\n" +
            "                }";
        code = code.replace(pickupLogic, newPickupLogic);
        
        // 4. Initial Weapon Drops in initGameObjects
        String initDrops = "this.drops = new ArrayList();";
        String newInitDrops = initDrops + "\n" +
            "          this.drops.add(new Drop(this, 400.0, 350.0, this.comuns[1])); // Revolver\n" +
            "          this.drops.add(new Drop(this, 450.0, 350.0, this.comuns[3])); // Uzi\n" +
            "          this.drops.add(new Drop(this, 350.0, 350.0, new Weapon(this, \"Faca de Combate\", \"Especial\", 1, 300L, 1, 0.0, java.awt.Color.LIGHT_GRAY, 100.0)));";
        code = code.replace(initDrops, newInitDrops);
        
        // 5. Player draw - avoid null weapon crash
        String activeWeapon = "Weapon activeWeapon = this.activeSlot == 1 ? this.weapon1 : this.weapon2;";
        String newActiveWeapon = "Weapon activeWeapon = this.activeSlot == 1 ? this.weapon1 : this.weapon2;\n" +
            "              if (activeWeapon == null) return;"; // Skip drawing if no weapon!
        code = code.replace(activeWeapon, newActiveWeapon);
        
        // Update check for null
        String updateNull = "Weapon wActive = this.activeSlot == 1 ? this.weapon1 : this.weapon2;\n" +
            "            double wWeight = (wActive != null) ? wActive.weight : 1.0;";
        String newUpdateNull = "Weapon wActive = this.activeSlot == 1 ? this.weapon1 : this.weapon2;\n" +
            "            if (wActive == null && ZombieGame.this.isMousePressed) ZombieGame.this.isMousePressed = false;\n" +
            "            double wWeight = (wActive != null) ? wActive.weight : 1.0;";
        code = code.replace(updateNull, newUpdateNull);

        Files.write(f.toPath(), code.getBytes());
    }
}
