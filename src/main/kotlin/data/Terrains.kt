package data

import core.Terrain

val terrains = listOf(
    Terrain("Grass", "Grass", "27df62", "A park of green and well kept paths.", flat = 8, water = 2),
    Terrain("Sand", "Sand", "27df62","Flat sands", flat = 10),
    Terrain("Wall", "None","000000","An unpassable wall."),
    Terrain("Door", "None","000000","An entry or exit.", 5, 5, 5),
    Terrain("Dirt", "Dirt","9b7b5e","Hard packed dirt.", flat = 10),
    Terrain("Bush", "Bush","29dc4e","Weeds and brackets all intertwined", bumpy = 4, tightQuarters = 6),
    Terrain(
        "Forest",
        "Forest",
        "24da5c",
        "Gnarled roots coalesce into shooting trunks and splay into woven branches.",
        flat = 3,
        bumpy = 3,
        tightQuarters = 4
    ),
    Terrain("Boulder", "None","000000", "A large, impassable rock."),
    Terrain("Water", "None", "000000","Deep water."),
).associateBy { it.name }