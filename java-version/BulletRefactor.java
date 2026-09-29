import java.io.File;
import java.nio.file.Files;
import java.util.regex.*;

public class BulletRefactor {
    public static void main(String[] args) throws Exception {
        File f = new File("ZombieGame.java");
        String code = new String(Files.readAllBytes(f.toPath()));
        
        // 1. Add static image arrays to ZombieGame
        String imgVars = "public static java.awt.image.BufferedImage imgPlayerIdle;";
        String newImgVars = imgVars + "\n" +
            "    public static java.awt.image.BufferedImage[] imgAmmoLight = new java.awt.image.BufferedImage[3];\n" +
            "    public static java.awt.image.BufferedImage[] imgAmmoMedium = new java.awt.image.BufferedImage[3];\n" +
            "    public static java.awt.image.BufferedImage[] imgAmmoShotgun = new java.awt.image.BufferedImage[3];\n" +
            "    public static java.awt.image.BufferedImage[] imgAmmoRPG = new java.awt.image.BufferedImage[4];\n" +
            "    public static java.awt.image.BufferedImage[] imgAmmoGrenade = new java.awt.image.BufferedImage[4];";
        code = code.replace(imgVars, newImgVars);
        
        // 2. Modify Bullet class fields and constructor
        String bulletFields = "String type;\n        double maxRange;\n        long spawnTime;";
        String newBulletFields = bulletFields + "\n" +
            "        String weaponType;\n" +
            "        double angle;\n" +
            "        boolean isExploding = false;\n" +
            "        long explodeTime = 0L;";
        code = code.replace(bulletFields, newBulletFields);
        
        String bulletInit = "Bullet(ZombieGame zombieGame, double d, double d2, double d3, int n, double d4, String string, double d5) {\n" +
            "            this.startX = d;\n" +
            "            this.startY = d2;\n" +
            "            this.x = d;\n" +
            "            this.y = d2;\n" +
            "            this.damage = n;\n" +
            "            this.speed = d4;\n" +
            "            this.velX = Math.cos(d3) * d4;\n" +
            "            this.velY = Math.sin(d3) * d4;\n" +
            "            this.type = string;\n" +
            "            this.maxRange = d5;\n" +
            "            this.spawnTime = System.currentTimeMillis();\n" +
            "        }";
        String newBulletInit = "Bullet(ZombieGame zombieGame, double d, double d2, double d3, int n, double d4, String string, double d5, String weaponType, double angle) {\n" +
            "            this.startX = d;\n" +
            "            this.startY = d2;\n" +
            "            this.x = d;\n" +
            "            this.y = d2;\n" +
            "            this.damage = n;\n" +
            "            this.speed = d4;\n" +
            "            this.velX = Math.cos(d3) * d4;\n" +
            "            this.velY = Math.sin(d3) * d4;\n" +
            "            this.type = string;\n" +
            "            this.maxRange = d5;\n" +
            "            this.spawnTime = System.currentTimeMillis();\n" +
            "            this.weaponType = weaponType;\n" +
            "            this.angle = angle;\n" +
            "        }";
        code = code.replace(bulletInit, newBulletInit);
        
        // 3. Update MouseAttack
        String mAttack1 = "this.bullets.add(new Bullet(this, this.player.x, this.player.y, d + d3, weapon.damage + this.player.bonusDamage, weapon.projSpeed, \"fire\", 180.0));";
        String mAttackNew1 = "this.bullets.add(new Bullet(this, this.player.x, this.player.y, d + d3, weapon.damage + this.player.bonusDamage, weapon.projSpeed, \"fire\", 180.0, weapon.typeName, d + d3));";
        code = code.replace(mAttack1, mAttackNew1);
        
        String mAttack2 = "this.bullets.add(new Bullet(this, this.player.x, this.player.y, d + d4, weapon.damage + this.player.bonusDamage, weapon.projSpeed, \"normal\", d2));";
        String mAttackNew2 = "this.bullets.add(new Bullet(this, this.player.x, this.player.y, d + d4, weapon.damage + this.player.bonusDamage, weapon.projSpeed, \"normal\", d2, weapon.typeName, d + d4));";
        code = code.replace(mAttack2, mAttackNew2);
        
        // 4. Update Bullet logic
        String bulletUpdate = "void update() {\n" +
            "            if (this.type.equals(\"fire\")) {";
        String newBulletUpdate = "void update() {\n" +
            "            if (this.isExploding) return;\n" +
            "            if (this.type.equals(\"fire\")) {";
        code = code.replace(bulletUpdate, newBulletUpdate);
        
        String bulletExpired = "boolean isExpired() {\n" +
            "            if (this.type.equals(\"fire\")) {";
        String newBulletExpired = "boolean isExpired() {\n" +
            "            if (this.isExploding) return System.currentTimeMillis() - this.explodeTime > 300L;\n" +
            "            if (this.type.equals(\"fire\")) {";
        code = code.replace(bulletExpired, newBulletExpired);
        
        // 5. Update Bullet Draw
        String bulletDraw = "void draw(Graphics2D graphics2D) {\n" +
            "            if (this.type.equals(\"fire\")) {\n" +
            "                graphics2D.setColor(new Color(255, 69, 0, 150));\n" +
            "                graphics2D.fillOval((int)this.x - 20, (int)this.y - 20, 40, 40);\n" +
            "                graphics2D.setColor(new Color(255, 140, 0, 100));\n" +
            "                graphics2D.fillOval((int)this.x - 15, (int)this.y - 15, 30, 30);\n" +
            "                graphics2D.setColor(new Color(255, 255, 0, 200));\n" +
            "                graphics2D.fillOval((int)this.x - 5, (int)this.y - 5, 10, 10);\n" +
            "            } else {\n" +
            "                graphics2D.setColor(Color.YELLOW);\n" +
            "                graphics2D.fillOval((int)this.x - 5, (int)this.y - 5, 10, 10);\n" +
            "            }\n" +
            "        }";
        String newBulletDraw = "void draw(Graphics2D graphics2D) {\n" +
            "            if (this.isExploding) {\n" +
            "                if (this.weaponType.equals(\"rpg\") && imgAmmoRPG[3] != null) {\n" +
            "                    graphics2D.drawImage(imgAmmoRPG[3], (int)this.x - 40, (int)this.y - 40, 80, 80, null);\n" +
            "                } else if (this.weaponType.equals(\"lancagranadas\") && imgAmmoGrenade[3] != null) {\n" +
            "                    graphics2D.drawImage(imgAmmoGrenade[3], (int)this.x - 40, (int)this.y - 40, 80, 80, null);\n" +
            "                } else {\n" +
            "                    graphics2D.setColor(Color.ORANGE);\n" +
            "                    graphics2D.fillOval((int)this.x - 40, (int)this.y - 40, 80, 80);\n" +
            "                }\n" +
            "                return;\n" +
            "            }\n" +
            "            if (this.type.equals(\"fire\")) {\n" +
            "                graphics2D.setColor(new Color(255, 69, 0, 150));\n" +
            "                graphics2D.fillOval((int)this.x - 20, (int)this.y - 20, 40, 40);\n" +
            "                graphics2D.setColor(new Color(255, 140, 0, 100));\n" +
            "                graphics2D.fillOval((int)this.x - 15, (int)this.y - 15, 30, 30);\n" +
            "                graphics2D.setColor(new Color(255, 255, 0, 200));\n" +
            "                graphics2D.fillOval((int)this.x - 5, (int)this.y - 5, 10, 10);\n" +
            "                return;\n" +
            "            }\n" +
            "            java.awt.image.BufferedImage[] frames = null;\n" +
            "            if (this.weaponType != null) {\n" +
            "                if (this.weaponType.equals(\"glock\") || this.weaponType.equals(\"revolver\") || this.weaponType.equals(\"smg\") || this.weaponType.equals(\"miniuzi\")) frames = imgAmmoLight;\n" +
            "                else if (this.weaponType.equals(\"ak47\") || this.weaponType.equals(\"minigun\")) frames = imgAmmoMedium;\n" +
            "                else if (this.weaponType.equals(\"shotgun\")) frames = imgAmmoShotgun;\n" +
            "                else if (this.weaponType.equals(\"rpg\")) frames = imgAmmoRPG;\n" +
            "                else if (this.weaponType.equals(\"lancagranadas\")) frames = imgAmmoGrenade;\n" +
            "            }\n" +
            "            if (frames != null && frames[0] != null) {\n" +
            "                java.awt.geom.AffineTransform old = graphics2D.getTransform();\n" +
            "                graphics2D.translate(this.x, this.y);\n" +
            "                graphics2D.rotate(this.angle);\n" +
            "                graphics2D.drawImage(frames[0], -15, -15, 30, 30, null);\n" +
            "                graphics2D.setTransform(old);\n" +
            "            } else {\n" +
            "                graphics2D.setColor(Color.YELLOW);\n" +
            "                graphics2D.fillOval((int)this.x - 5, (int)this.y - 5, 10, 10);\n" +
            "            }\n" +
            "        }";
        code = code.replace(bulletDraw, newBulletDraw);

        // 6. AoE damage in Enemy Loop
        String eDamage = "if (!objE.getBounds().intersects(bullet.getBounds())) continue;\n" +
            "                  if (bullet.type.equals(\"fire\")) {\n" +
            "                      if (System.currentTimeMillis() - objE.lastFireDamageTime <= 300L) continue;\n" +
            "                      objE.life -= bullet.damage;\n" +
            "                      objE.lastFireDamageTime = System.currentTimeMillis();\n" +
            "                      ZombieGame.this.damageTexts.add(new DamageText(ZombieGame.this, bullet.x, bullet.y, bullet.damage));\n" +
            "                  } else {\n" +
            "                      objE.life -= bullet.damage;\n" +
            "                      ZombieGame.this.damageTexts.add(new DamageText(ZombieGame.this, bullet.x, bullet.y, bullet.damage));\n" +
            "                  }\n" +
            "                  if (objE.life <= 0) {\n" +
            "                      objE.dead = true;\n" +
            "                      objE.deathTime = System.currentTimeMillis();\n" +
            "                  }\n" +
            "                  if (!bullet.type.equals(\"fire\")) {\n" +
            "                      itB.remove();\n" +
            "                  }";
        String newEDamage = "if (bullet.isExploding) continue;\n" +
            "                  if (!objE.getBounds().intersects(bullet.getBounds())) continue;\n" +
            "                  if (bullet.weaponType != null && (bullet.weaponType.equals(\"rpg\") || bullet.weaponType.equals(\"lancagranadas\"))) {\n" +
            "                      bullet.isExploding = true;\n" +
            "                      bullet.explodeTime = System.currentTimeMillis();\n" +
            "                      for (Enemy aoeE : ZombieGame.this.enemies) {\n" +
            "                          if (!aoeE.dead && Math.hypot(aoeE.x - bullet.x, aoeE.y - bullet.y) < 150.0) {\n" +
            "                              aoeE.life -= bullet.damage;\n" +
            "                              ZombieGame.this.damageTexts.add(new DamageText(ZombieGame.this, aoeE.x, aoeE.y, bullet.damage));\n" +
            "                              if (aoeE.life <= 0) { aoeE.dead = true; aoeE.deathTime = System.currentTimeMillis(); }\n" +
            "                          }\n" +
            "                      }\n" +
            "                      continue;\n" +
            "                  }\n" +
            "                  if (bullet.type.equals(\"fire\")) {\n" +
            "                      if (System.currentTimeMillis() - objE.lastFireDamageTime <= 300L) continue;\n" +
            "                      objE.life -= bullet.damage;\n" +
            "                      objE.lastFireDamageTime = System.currentTimeMillis();\n" +
            "                      ZombieGame.this.damageTexts.add(new DamageText(ZombieGame.this, bullet.x, bullet.y, bullet.damage));\n" +
            "                  } else {\n" +
            "                      objE.life -= bullet.damage;\n" +
            "                      ZombieGame.this.damageTexts.add(new DamageText(ZombieGame.this, bullet.x, bullet.y, bullet.damage));\n" +
            "                  }\n" +
            "                  if (objE.life <= 0) {\n" +
            "                      objE.dead = true;\n" +
            "                      objE.deathTime = System.currentTimeMillis();\n" +
            "                  }\n" +
            "                  if (!bullet.type.equals(\"fire\")) {\n" +
            "                      itB.remove();\n" +
            "                  }";
        code = code.replace(eDamage, newEDamage);
        
        Files.write(f.toPath(), code.getBytes());
    }
}
