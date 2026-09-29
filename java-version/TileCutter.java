import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.*;

public class TileCutter {
    public static void main(String[] args) {
        try {
            processSheet("img/parede_tiles_3_modelos_horizontal_vertical.png", "wall", 2, 3);
            processSheet("img/janela_tiles_3_modelos_horizontal_vertical.png", "window", 2, 3);
            processSheet("img/porta_tiles_3_modelos_horizontal_vertical.png", "door", 2, 3);
            
            System.out.println("Corte de tiles finalizado!");
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
                String orient = c == 0 ? "h" : "v";
                int tier = r + 1;
                ImageIO.write(frame, "png", new File("img/" + prefix + "_t" + tier + "_" + orient + ".png"));
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
