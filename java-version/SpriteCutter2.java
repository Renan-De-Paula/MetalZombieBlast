import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.*;

public class SpriteCutter2 {
    public static void main(String[] args) {
        try {
            processSheet("img/zumbis_comuns_sprite_sheet_topdown.png", "zombie_basic", 5, 3);
            processSheet("img/zumbis_medios_sprite_sheet_topdown.png", "zombie_runner", 5, 3);
            processSheet("img/zumbis_superiores_sprite_sheet_topdown.png", "zombie_superior", 5, 2);
            processSheet("img/zumbis_bosses_sprite_sheet_topdown.png", "zombie_boss", 5, 2);
            processSheet("img/zumbi_superboss_sprite_sheet_topdown.png", "zombie_superboss", 5, 1);
            
            System.out.println("Corte v2 finalizado!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processSheet(String path, String prefix, int cols, int rows) throws Exception {
        File f = new File(path);
        if (!f.exists()) {
            System.out.println("Faltando: " + path);
            return;
        }
        BufferedImage sheet = ImageIO.read(f);
        int cw = sheet.getWidth() / cols;
        int ch = sheet.getHeight() / rows;
        
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                BufferedImage frame = extractLargestIsland(sheet, c * cw, r * ch, cw, ch);
                ImageIO.write(frame, "png", new File("img/" + prefix + "_v" + r + "_f" + c + ".png"));
            }
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
                        int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}, {1,1}, {-1,-1}, {1,-1}, {-1,1}};
                        for (int[] d : dirs) {
                            int nx = pt[0] + d[0];
                            int ny = pt[1] + d[1];
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
        
        if (islands.isEmpty()) return img.getSubimage(ox, oy, w, h);
        
        List<int[]> largest = islands.get(0);
        for (List<int[]> island : islands) if (island.size() > largest.size()) largest = island;
        
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
