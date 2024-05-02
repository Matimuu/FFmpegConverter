/**
 * @author Mendoza Perez Omar Enrique
 * @date 2024/04/23 15:38
 */
public enum Format {
    LIVE("LIVE", 1),
    PREVIA("PREVIA",2),
    CHAMPIONS("CHAMPIONS", 3),
    SHOW("SHOW", 4),
    TEST("TEST", 9);
    private String name;
    private int index;

    Format(String name, int index) {
        this.name = name;
        this.index = index;

    }
    public int getIndex() {
        return this.index;
    }
    public String getName() {
        return this.name;
    }
}
