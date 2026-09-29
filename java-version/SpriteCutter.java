import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.*;

public class SpriteCutter {
    public static void main(String[] args) {
        try {
            // Zumbis
            BufferedImage zumbiSheet = ImageIO.read(new File("img/zumbis_sprite_sheet_classes_movimentos_topdown.png"));
            int zCol = 5;
            int zRow = 11;
            int zW = zumbiSheet.getWidth() / zCol;
            int zH = zumbiSheet.getHeight() / zRow;

            String[] classes = {"zombie_basic", "zombie_runner", "zombie_boss", "zombie_superboss"};
            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 3; c++) {
                    BufferedImage frame = extractLargestIsland(zumbiSheet, c * zW, r * zH, zW, zH);
                    ImageIO.write(frame, "png", new File("img/" + classes[r] + "_" + c + ".png"));
                }
            }

            // Player
            BufferedImage playerSheet = ImageIO.read(new File("img/personagem_sprite_sheet_3_movimentos_por_arma.png"));
            int pCol = 6;
            int pRow = 6;
            int pW = playerSheet.getWidth() / pCol;
            int pH = playerSheet.getHeight() / pRow;
            
            BufferedImage p1 = extractLargestIsland(playerSheet, 0, 0, pW, pH);
            BufferedImage p2 = extractLargestIsland(playerSheet, pW, 0, pW, pH);
            ImageIO.write(p1, "png", new File("img/player_andando_arma_baixa.png"));
            ImageIO.write(p2, "png", new File("img/player_atirando_arma_levantada.png"));
            
            System.out.println("Sprites cortados inteligentemente com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage extractLargestIsland(BufferedImage img, int ox, int oy, int w, int h) {
        boolean[][] visited = new boolean[w][h];
        List<List<int[]>> islands = new ArrayList<>();
        
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (!visited[x][y] && isOpaque(img, ox + x, oy + y)) {
                    List<int[]> island = new ArrayList<>();
                    Queue<int[]> q = new LinkedList<>();
                    q.add(new int[]{x, y});
                    visited[x][y] = true;
                    
                    while (!q.isEmpty()) {
                        int[] pt = q.poll();
                        island.add(pt);
                        int px = pt[0];
                        int py = pt[1];
                        
                        int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {-1,-1}, {1,-1}, {-1,1}};
                        for (int[] d : dirs) {
                            int nx = px + d[0];
                            int ny = py + d[1];
                            if (nx >= 0 && nx < w && ny >= 0 && ny < h && !visited[nx][ny] && isOpaque(img, ox + nx, oy + ny)) {
                                visited[nx][ny] = true;
                                q.add(new int[]{nx, ny});
                            }
                        }
                    }
                    islands.add(island);
                }
            }
        }
        
        if (islands.isEmpty()) {
            return img.getSubimage(ox, oy, w, h); // Fallback
        }
        
        List<int[]> largest = islands.get(0);
        for (List<int[]> island : islands) {
            if (island.size() > largest.size()) {
                largest = island;
            }
        }
        
        int minX = w, maxX = 0, minY = h, maxY = 0;
        for (int[] pt : largest) {
            if (pt[0] < minX) minX = pt[0];
            if (pt[0] > maxX) maxX = pt[0];
            if (pt[1] < minY) minY = pt[1];
            if (pt[1] > maxY) maxY = pt[1];
        }
        
        int iw = maxX - minX + 1;
        int ih = maxY - minY + 1;
        if (iw <= 0 || ih <= 0) return img.getSubimage(ox, oy, w, h);
        
        return img.getSubimage(ox + minX, oy + minY, iw, ih);
    }
    
    private static boolean isOpaque(BufferedImage img, int x, int y) {
        int alpha = (img.getRGB(x, y) >> 24) & 0xff;
        return alpha > 10;
    }
}
