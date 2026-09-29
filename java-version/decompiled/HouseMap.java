/*
 * Decompiled with CFR 0.152.
 */
import java.util.ArrayList;
import java.util.List;

public class HouseMap {
    public static final int MAP_WIDTH = 40;
    public static final int MAP_HEIGHT = 24;
    public static final Room[] ROOMS = new Room[]{new Room("central", 12, 7, 27, 17, true), new Room("bedroom", 4, 3, 11, 8, false), new Room("bathroom", 12, 3, 17, 6, false), new Room("kitchen", 19, 3, 29, 6, false), new Room("storage", 4, 10, 11, 17, false), new Room("workshop", 29, 8, 36, 15, false), new Room("medical", 4, 19, 11, 22, false), new Room("stairs", 29, 18, 36, 22, false)};

    public static int getLimit(Kind kind) {
        switch (kind.ordinal()) {
            case 1: {
                return 4;
            }
            case 2: {
                return 12;
            }
            case 3: {
                return 1;
            }
            case 4: {
                return 1;
            }
            case 5: {
                return 1;
            }
            case 6: {
                return 16;
            }
        }
        return -1;
    }

    public static List<Cell> occupiedCells(Placement placement) {
        ArrayList<Cell> arrayList = new ArrayList<Cell>();
        for (int i = 0; i < placement.width; ++i) {
            for (int j = 0; j < placement.height; ++j) {
                arrayList.add(new Cell(placement.cell.x + i, placement.cell.y + j));
            }
        }
        return arrayList;
    }

    public static boolean overlaps(Placement placement, Placement placement2) {
        List<Cell> list = HouseMap.occupiedCells(placement);
        List<Cell> list2 = HouseMap.occupiedCells(placement2);
        for (Cell cell : list) {
            for (Cell cell2 : list2) {
                if (!cell.equals(cell2)) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean insideMap(Placement placement) {
        for (Cell cell : HouseMap.occupiedCells(placement)) {
            if (cell.x >= 0 && cell.y >= 0 && cell.x < 40 && cell.y < 24) continue;
            return false;
        }
        return true;
    }

    public static Room roomAt(Cell cell) {
        for (Room room : ROOMS) {
            if (cell.x < room.minX || cell.x > room.maxX || cell.y < room.minY || cell.y > room.maxY) continue;
            return room;
        }
        return null;
    }

    public static int countOf(List<Placement> list, Kind kind) {
        int n = 0;
        for (Placement placement : list) {
            if (placement.kind != kind) continue;
            ++n;
        }
        return n;
    }

    public static boolean touchesWall(Placement placement, List<Placement> list) {
        List<Cell> list2 = HouseMap.occupiedCells(placement);
        for (Placement placement2 : list) {
            if (placement2.kind != Kind.WALL) continue;
            List<Cell> list3 = HouseMap.occupiedCells(placement2);
            for (Cell cell : list2) {
                for (Cell cell2 : list3) {
                    if (Math.abs(cell.x - cell2.x) + Math.abs(cell.y - cell2.y) != 1) continue;
                    return true;
                }
            }
        }
        return false;
    }

    public static ValidationResult canPlace(Placement placement, List<Placement> list) {
        Room room;
        if (!HouseMap.insideMap(placement)) {
            return new ValidationResult(false, "O item ultrapassa os limites do mapa.");
        }
        for (Placement placement2 : list) {
            if (!HouseMap.overlaps(placement, placement2)) continue;
            return new ValidationResult(false, "O espaco ja esta ocupado.");
        }
        int n = HouseMap.getLimit(placement.kind);
        if (n != -1 && HouseMap.countOf(list, placement.kind) >= n) {
            return new ValidationResult(false, "Limite atingido para " + String.valueOf((Object)placement.kind) + ": " + n);
        }
        if (!(placement.kind != Kind.DOOR && placement.kind != Kind.WINDOW || HouseMap.touchesWall(placement, list))) {
            return new ValidationResult(false, "Portas e janelas precisam tocar uma parede.");
        }
        if (placement.kind == Kind.WALL || placement.kind == Kind.DOOR || placement.kind == Kind.WINDOW) {
            boolean bl;
            boolean bl2;
            boolean bl3 = bl2 = placement.orientation == Orientation.HORIZONTAL;
            boolean bl4 = bl2 ? placement.width >= placement.height : (bl = placement.height >= placement.width);
            if (!bl) {
                return new ValidationResult(false, "A orientacao nao combina com as dimensoes do tile.");
            }
        }
        if (placement.kind == Kind.WORKBENCH) {
            Room room2 = HouseMap.roomAt(placement.cell);
            if (room2 == null || !room2.id.equals("workshop")) {
                return new ValidationResult(false, "A bancada so pode ser colocada na oficina.");
            }
            if (HouseMap.countOf(list, Kind.WORKBENCH) > 0) {
                return new ValidationResult(false, "A casa ja possui uma bancada.");
            }
        }
        if (placement.kind == Kind.SUPPLY && HouseMap.roomAt(placement.cell) == null) {
            return new ValidationResult(false, "Suprimentos precisam ficar dentro de um comodo.");
        }
        if (placement.kind == Kind.STAIR_UP || placement.kind == Kind.STAIR_DOWN) {
            Room room3 = HouseMap.roomAt(placement.cell);
            if (room3 == null || !room3.id.equals("stairs")) {
                return new ValidationResult(false, "A escada so pode ficar na sala de acesso vertical.");
            }
            for (Placement placement3 : list) {
                if (placement3.kind != Kind.DOOR && placement3.kind != Kind.WINDOW || !HouseMap.overlaps(placement, placement3)) continue;
                return new ValidationResult(false, "A escada nao pode bloquear uma porta ou janela.");
            }
        }
        if (!(placement.kind != Kind.PLAYER_SPAWN || (room = HouseMap.roomAt(placement.cell)) != null && room.isCentralDefenseRoom)) {
            return new ValidationResult(false, "O ponto inicial precisa ficar na sala central.");
        }
        return new ValidationResult(true, "OK");
    }

    public static List<Placement> tryPlace(Placement placement, List<Placement> list) {
        ValidationResult validationResult = HouseMap.canPlace(placement, list);
        if (!validationResult.ok) {
            System.out.println("[BUILD BLOCKED] " + validationResult.reason);
            return list;
        }
        System.out.println("[BUILD OK] " + String.valueOf((Object)placement.kind) + " colocado em " + placement.cell.x + "," + placement.cell.y);
        list.add(placement);
        return list;
    }

    public static enum Kind {
        WALL,
        DOOR,
        WINDOW,
        STAIR_UP,
        STAIR_DOWN,
        WORKBENCH,
        SUPPLY,
        PLAYER_SPAWN,
        ROOM_MARKER;

    }

    public static class Placement {
        public String id;
        public Kind kind;
        public Cell cell;
        public int width;
        public int height;
        public Orientation orientation;
        public int tier;
        public String roomId;

        public Placement(String string, Kind kind, Cell cell, int n, int n2) {
            this.id = string;
            this.kind = kind;
            this.cell = cell;
            this.width = n;
            this.height = n2;
        }
    }

    public static class Cell {
        public int x;
        public int y;

        public Cell(int n, int n2) {
            this.x = n;
            this.y = n2;
        }

        public boolean equals(Cell cell) {
            return this.x == cell.x && this.y == cell.y;
        }
    }

    public static class Room {
        public String id;
        public int minX;
        public int minY;
        public int maxX;
        public int maxY;
        public boolean isCentralDefenseRoom;

        public Room(String string, int n, int n2, int n3, int n4, boolean bl) {
            this.id = string;
            this.minX = n;
            this.minY = n2;
            this.maxX = n3;
            this.maxY = n4;
            this.isCentralDefenseRoom = bl;
        }
    }

    public static class ValidationResult {
        public boolean ok;
        public String reason;

        public ValidationResult(boolean bl, String string) {
            this.ok = bl;
            this.reason = string;
        }
    }

    public static enum Orientation {
        HORIZONTAL,
        VERTICAL;

    }
}

