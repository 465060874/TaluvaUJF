package map;

class HexUnionFind {

    private final HexMap<Hex> map;

    HexUnionFind() {
        this.map = HexMap.create();
    }

    Hex find(Hex hex) {
        Hex parent = map.getOrDefault(hex, null);
        if (parent == null) {
            return hex;
        }

        Hex root = find(parent);
        map.put(hex, root);
        return root;
    }


    Hex union(Hex hex1, Hex hex2) {
        Hex root1 = find(hex1);
        Hex root2 = find(hex2);
        if (root1.equals(root2)) {
            return root1;
        }

        map.put(root1, root2);
        map.put(hex1, root2);
        return root2;
    }
}

