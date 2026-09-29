import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class InventoryRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // 1. Add isInventoryOpen flag
        String isMapOpenVar = "public boolean isMapOpen = false;";
        String isMapOpenNew = isMapOpenVar + "\n    public boolean isInventoryOpen = false;";
        code = code.replace(isMapOpenVar, isMapOpenNew);
        
        // 2. Add key bindings
        String keyPress = "if (keyEvent.getKeyCode() < 256) {\n                        this.keys[keyEvent.getKeyCode()] = true;\n                    }";
        String newKeyPress = keyPress + "\n" +
            "                    if (keyEvent.getKeyCode() == 77) { this.isMapOpen = !this.isMapOpen; }\n" +
            "                    if (keyEvent.getKeyCode() == 9) { this.isInventoryOpen = !this.isInventoryOpen; }";
        code = code.replace(keyPress, newKeyPress);
        
        // 3. Add drawInventory method
        String drawInvMethod = "private void drawInventory(java.awt.Graphics2D g) {\n" +
            "        int w = 600, h = 500;\n" +
            "        int x = (800 - w) / 2;\n" +
            "        int y = (600 - h) / 2;\n" +
            "        g.setColor(new java.awt.Color(40, 40, 40, 230));\n" +
            "        g.fillRect(x, y, w, h);\n" +
            "        g.setColor(java.awt.Color.WHITE);\n" +
            "        g.setFont(new java.awt.Font(\"Arial\", 1, 12));\n" +
            "        g.drawString(\"CURRENCIES\", x + 20, y + 30);\n" +
            "        g.setFont(new java.awt.Font(\"Arial\", 0, 12));\n" +
            "        g.drawString(\"Munição Global: PISTOLA[\"+globalAmmo[0]+\"] FUZIL[\"+globalAmmo[1]+\"] PESADA[\"+globalAmmo[2]+\"] SHOTGUN[\"+globalAmmo[3]+\"] EXPLOSIVO[\"+globalAmmo[4]+\"]\", x + 20, y + 50);\n" +
            "        g.setFont(new java.awt.Font(\"Arial\", 1, 12));\n" +
            "        int currentCapacity = this.wood + this.nails + this.iron + this.screws + this.lead + this.food + this.medkit + this.crystals + this.fuelCans;\n" +
            "        g.drawString(\"RESOURCES (Peso: \" + currentCapacity + \" / \" + this.maxBackpackCapacity + \")\", x + 20, y + 90);\n" +
            "        int startX = x + 20;\n" +
            "        int startY = y + 110;\n" +
            "        int slotSize = 50;\n" +
            "        int padding = 10;\n" +
            "        String[] resNames = {\"Wood\", \"Nails\", \"Iron\", \"Screws\", \"Lead\", \"Food\", \"Medkit\", \"Fuel\", \"Crystals\"};\n" +
            "        int[] resCounts = {wood, nails, iron, screws, lead, food, medkit, fuelCans, crystals};\n" +
            "        java.awt.Color[] resColors = {new java.awt.Color(139, 69, 19), java.awt.Color.GRAY, java.awt.Color.DARK_GRAY, java.awt.Color.LIGHT_GRAY, java.awt.Color.BLACK, java.awt.Color.GREEN, java.awt.Color.WHITE, java.awt.Color.RED, java.awt.Color.MAGENTA};\n" +
            "        int col = 0, row = 0;\n" +
            "        for (int i = 0; i < 14; i++) {\n" +
            "            int cx = startX + col * (slotSize + padding);\n" +
            "            int cy = startY + row * (slotSize + padding);\n" +
            "            g.setColor(new java.awt.Color(60, 60, 60));\n" +
            "            g.fillRect(cx, cy, slotSize, slotSize);\n" +
            "            g.setColor(new java.awt.Color(100, 100, 100));\n" +
            "            g.drawRect(cx, cy, slotSize, slotSize);\n" +
            "            if (i < resCounts.length && resCounts[i] > 0) {\n" +
            "                g.setColor(resColors[i]);\n" +
            "                g.fillRect(cx + 10, cy + 10, slotSize - 20, slotSize - 20);\n" +
            "                g.setColor(java.awt.Color.WHITE);\n" +
            "                g.setFont(new java.awt.Font(\"Arial\", 0, 9));\n" +
            "                g.drawString(resNames[i], cx + 2, cy + 10);\n" +
            "                g.setFont(new java.awt.Font(\"Arial\", 1, 12));\n" +
            "                g.drawString(String.valueOf(resCounts[i]), cx + slotSize - 15, cy + slotSize - 5);\n" +
            "            }\n" +
            "            col++; if (col == 7) { col = 0; row++; }\n" +
            "        }\n" +
            "        g.setColor(java.awt.Color.WHITE);\n" +
            "        g.setFont(new java.awt.Font(\"Arial\", 1, 12));\n" +
            "        g.drawString(\"LOADOUT\", x + 20, y + 260);\n" +
            "        Weapon[] weapons = {player.weapon1, player.weapon2};\n" +
            "        for(int i = 0; i < 5; i++) {\n" +
            "            int cx = x + 20 + i * (slotSize + padding + 10);\n" +
            "            int cy = y + 280;\n" +
            "            g.setColor(new java.awt.Color(60, 60, 60));\n" +
            "            g.fillRect(cx, cy, slotSize + 10, slotSize + 10);\n" +
            "            if (i < 2 && weapons[i] != null) {\n" +
            "                java.awt.Color rC = weapons[i].rarity.equals(\"Lendaria\") ? new java.awt.Color(255, 140, 0) :\n" +
            "                                   weapons[i].rarity.equals(\"Epica\") ? new java.awt.Color(138, 43, 226) :\n" +
            "                                   weapons[i].rarity.equals(\"Rara\") ? new java.awt.Color(0, 191, 255) : new java.awt.Color(50, 205, 50);\n" +
            "                g.setColor(rC);\n" +
            "                g.fillRect(cx, cy, slotSize + 10, slotSize + 10);\n" +
            "                g.setColor(java.awt.Color.WHITE);\n" +
            "                g.fillRect(cx + 15, cy + 15, 30, 30);\n" +
            "                g.setColor(java.awt.Color.BLACK);\n" +
            "                g.setFont(new java.awt.Font(\"Arial\", 1, 10));\n" +
            "                g.drawString(weapons[i].name.substring(0, Math.min(6, weapons[i].name.length())), cx + 2, cy + 12);\n" +
            "                if (weapons[i].ammoType >= 0) {\n" +
            "                    g.setColor(java.awt.Color.WHITE);\n" +
            "                    g.setFont(new java.awt.Font(\"Arial\", 1, 14));\n" +
            "                    g.drawString(String.valueOf(globalAmmo[weapons[i].ammoType]), cx + 35, cy + 55);\n" +
            "                }\n" +
            "            }\n" +
            "            g.setColor(new java.awt.Color(100, 100, 100));\n" +
            "            g.drawRect(cx, cy, slotSize + 10, slotSize + 10);\n" +
            "        }\n" +
            "        g.setColor(new java.awt.Color(30, 30, 30));\n" +
            "        g.fillRect(x + 20, y + 380, w - 40, 70);\n" +
            "        Weapon aW = player.activeSlot == 1 ? player.weapon1 : player.weapon2;\n" +
            "        if (aW != null) {\n" +
            "            g.setColor(java.awt.Color.WHITE);\n" +
            "            g.setFont(new java.awt.Font(\"Arial\", 1, 16));\n" +
            "            g.drawString(aW.name.toUpperCase(), x + 30, y + 400);\n" +
            "            g.setFont(new java.awt.Font(\"Arial\", 1, 12));\n" +
            "            java.awt.Color rC = aW.rarity.equals(\"Lendaria\") ? new java.awt.Color(255, 140, 0) :\n" +
            "                               aW.rarity.equals(\"Epica\") ? new java.awt.Color(138, 43, 226) :\n" +
            "                               aW.rarity.equals(\"Rara\") ? new java.awt.Color(0, 191, 255) : new java.awt.Color(50, 205, 50);\n" +
            "            g.setColor(rC);\n" +
            "            g.drawString(aW.rarity.toUpperCase(), x + 30, y + 430);\n" +
            "            g.setColor(java.awt.Color.GRAY);\n" +
            "            g.drawString(\"Dano: \" + aW.damage + \" | Range: \" + aW.maxRange, x + 150, y + 430);\n" +
            "        }\n" +
            "        g.setColor(java.awt.Color.WHITE);\n" +
            "        g.setFont(new java.awt.Font(\"Arial\", 1, 12));\n" +
            "        g.drawString(\"[TAB] FECHAR\", x + w - 120, y + h - 15);\n" +
            "    }\n";
        String mapDrawHook = "private void drawMap(java.awt.Graphics2D g, int sx, int sy, int size, double scale, boolean isCircular) {";
        code = code.replace(mapDrawHook, drawInvMethod + "\n    " + mapDrawHook);
        
        // 4. Inject drawing logic into paintComponent
        String drawHook = "if (this.isMapOpen) {\n                this.drawMap(graphics2D, 150, 50, 500, 0.03, false);\n            } else {\n                this.drawMap(graphics2D, 620, 20, 160, 0.1, true);\n            }";
        String newDrawHook = drawHook + "\n            if (this.isInventoryOpen) {\n                this.drawInventory(graphics2D);\n            }";
        code = code.replace(drawHook, newDrawHook);

        Files.write(f.toPath(), code.getBytes());
    }
}
