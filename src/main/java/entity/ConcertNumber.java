package entity;

import java.util.ArrayList;
import java.util.List;

public class ConcertNumber {
    private final String id;
    private final String type;
    private final String scene;
    private final String group;
    private final String music;
    private final int number;
    private final List<Dancer> dancers;

    private ConcertNumber(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.scene = builder.scene;
        this.group = builder.group;
        this.music = builder.music;
        this.number = builder.number;
        this.dancers = builder.dancers;
    }

    public String getId() { return id; }
    public String getType() { return type; }
    public String getScene() { return scene; }
    public String getGroup() { return group; }
    public String getMusic() { return music; }
    public int getNumber() { return number; }
    public List<Dancer> getDancers() { return dancers; }

    @Override
    public String toString() {
        return String.format("Виступ #%d [ID: %s, Тип: %s, Сцена: %s] - Танцівників: %d",
                number, id, type, scene, dancers.size());
    }

    public static class Builder {
        private String id;
        private String type;
        private String scene;
        private String group;
        private String music;
        private int number;
        private List<Dancer> dancers = new ArrayList<>();

        public Builder id(String id) { this.id = id; return this; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder scene(String scene) { this.scene = scene; return this; }
        public Builder group(String group) { this.group = group; return this; }
        public Builder music(String music) { this.music = music; return this; }
        public Builder number(int number) { this.number = number; return this; }
        public Builder addDancer(Dancer dancer) { this.dancers.add(dancer); return this; }

        public ConcertNumber build() {
            return new ConcertNumber(this);
        }
    }
}