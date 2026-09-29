import java.util.ArrayList;
import java.util.List;

public class HouseMap {
    public enum Kind {
        WALL, DOOR, WINDOW, STAIR_UP, STAIR_DOWN, WORKBENCH, SUPPLY, PLAYER_SPAWN, ROOM_MARKER
    }

    public enum Orientation {
        HORIZONTAL, VERTICAL
    }

    public static class Cell {
        public int x, y;
        public Cell(int x, int y) { this.x = x; this.y = y; }
        public boolean equals(Cell c) { return this.x == c.x && this.y == c.y; }
    }

    public static class Placement {
        public String id;
        public Kind kind;
        public Cell cell;
        public int width, height;
        public Orientation orientation;
        public int tier;
        public String roomId;

        public Placement(String id, Kind kind, Cell cell, int width, int height) {
            this.id = id; this.kind = kind; this.cell = cell; this.width = width; this.height = height;
        }
    }

    public static class Room {
        public String id;
        public int minX, minY, maxX, maxY;
        public boolean isCentralDefenseRoom;

        public Room(String id, int minX, int minY, int maxX, int maxY, boolean central) {
            this.id = id; this.minX = minX; this.minY = minY; this.maxX = maxX; this.maxY = maxY; this.isCentralDefenseRoom = central;
        }
    }

    public static final int MAP_WIDTH = 40;
    public static final int MAP_HEIGHT = 24;

    public static final Room[] ROOMS = {
        new Room("central", 12, 7, 27, 17, true),
        new Room("bedroom", 4, 3, 11, 8, false),
        new Room("bathroom", 12, 3, 17, 6, false),
        new Room("kitchen", 19, 3, 29, 6, false),
        new Room("storage", 4, 10, 11, 17, false),
        new Room("workshop", 29, 8, 36, 15, false),
        new Room("medical", 4, 19, 11, 22, false),
        new Room("stairs", 29, 18, 36, 22, false)
    };

    public static int getLimit(Kind kind) {
        switch (kind) {
            case DOOR: return 4;
            case WINDOW: return 12;
            case STAIR_UP: return 1;
            case STAIR_DOWN: return 1;
            case WORKBENCH: return 1;
            case SUPPLY: return 16;
            default: return -1; // Sem limite
        }
    }

    public static List<Cell> occupiedCells(Placement item) {
        List<Cell> cells = new ArrayList<>();
        for (int dx = 0; dx < item.width; dx++) {
            for (int dy = 0; dy < item.height; dy++) {
                cells.add(new Cell(item.cell.x + dx, item.cell.y + dy));
            }
        }
        return cells;
    }

    public static boolean overlaps(Placement a, Placement b) {
        List<Cell> cellsA = occupiedCells(a);
        List<Cell> cellsB = occupiedCells(b);
        for (Cell ca : cellsA) {
            for (Cell cb : cellsB) {
                if (ca.equals(cb)) return true;
            }
        }
        return false;
    }

    public static boolean insideMap(Placement item) {
        for (Cell c : occupiedCells(item)) {
            if (c.x < 0 || c.y < 0 || c.x >= MAP_WIDTH || c.y >= MAP_HEIGHT) return false;
        }
        return true;
    }

    public static Room roomAt(Cell cell) {
        for (Room r : ROOMS) {
            if (cell.x >= r.minX && cell.x <= r.maxX && cell.y >= r.minY && cell.y <= r.maxY) {
                return r;
            }
        }
        return null;
    }

    public static int countOf(List<Placement> existing, Kind kind) {
        int count = 0;
        for (Placement p : existing) if (p.kind == kind) count++;
        return count;
    }

    public static boolean touchesWall(Placement item, List<Placement> existing) {
        List<Cell> cells = occupiedCells(item);
        for (Placement other : existing) {
            if (other.kind == Kind.WALL) {
                List<Cell> wallCells = occupiedCells(other);
                for (Cell c : cells) {
                    for (Cell wc : wallCells) {
                        if (Math.abs(c.x - wc.x) + Math.abs(c.y - wc.y) == 1) return true;
                    }
                }
            }
        }
        return false;
    }

    public static class ValidationResult {
        public boolean ok;
        public String reason;
        public ValidationResult(boolean ok, String reason) { this.ok = ok; this.reason = reason; }
    }

    public static ValidationResult canPlace(Placement candidate, List<Placement> existing) {
        if (!insideMap(candidate)) return new ValidationResult(false, "O item ultrapassa os limites do mapa.");
        
        for (Placement item : existing) {
            if (overlaps(candidate, item)) return new ValidationResult(false, "O espaco ja esta ocupado.");
        }

        int limit = getLimit(candidate.kind);
        if (limit != -1 && countOf(existing, candidate.kind) >= limit) {
            return new ValidationResult(false, "Limite atingido para " + candidate.kind + ": " + limit);
        }

        if ((candidate.kind == Kind.DOOR || candidate.kind == Kind.WINDOW) && !touchesWall(candidate, existing)) {
            return new ValidationResult(false, "Portas e janelas precisam tocar uma parede.");
        }

        if (candidate.kind == Kind.WALL || candidate.kind == Kind.DOOR || candidate.kind == Kind.WINDOW) {
            boolean horizontal = candidate.orientation == Orientation.HORIZONTAL;
            boolean validOrientation = horizontal ? candidate.width >= candidate.height : candidate.height >= candidate.width;
            if (!validOrientation) return new ValidationResult(false, "A orientacao nao combina com as dimensoes do tile.");
        }

        if (candidate.kind == Kind.WORKBENCH) {
            Room room = roomAt(candidate.cell);
            if (room == null || !room.id.equals("workshop")) return new ValidationResult(false, "A bancada so pode ser colocada na oficina.");
            if (countOf(existing, Kind.WORKBENCH) > 0) return new ValidationResult(false, "A casa ja possui uma bancada.");
        }

        if (candidate.kind == Kind.SUPPLY && roomAt(candidate.cell) == null) {
            return new ValidationResult(false, "Suprimentos precisam ficar dentro de um comodo.");
        }

        if (candidate.kind == Kind.STAIR_UP || candidate.kind == Kind.STAIR_DOWN) {
            Room room = roomAt(candidate.cell);
            if (room == null || !room.id.equals("stairs")) return new ValidationResult(false, "A escada so pode ficar na sala de acesso vertical.");
            for (Placement item : existing) {
                if ((item.kind == Kind.DOOR || item.kind == Kind.WINDOW) && overlaps(candidate, item)) {
                    return new ValidationResult(false, "A escada nao pode bloquear uma porta ou janela.");
                }
            }
        }

        if (candidate.kind == Kind.PLAYER_SPAWN) {
            Room room = roomAt(candidate.cell);
            if (room == null || !room.isCentralDefenseRoom) return new ValidationResult(false, "O ponto inicial precisa ficar na sala central.");
        }

        return new ValidationResult(true, "OK");
    }

    public static List<Placement> tryPlace(Placement candidate, List<Placement> existing) {
        ValidationResult validation = canPlace(candidate, existing);
        if (!validation.ok) {
            System.out.println("[BUILD BLOCKED] " + validation.reason);
            return existing;
        }
        System.out.println("[BUILD OK] " + candidate.kind + " colocado em " + candidate.cell.x + "," + candidate.cell.y);
        existing.add(candidate);
        return existing;
    }
}
