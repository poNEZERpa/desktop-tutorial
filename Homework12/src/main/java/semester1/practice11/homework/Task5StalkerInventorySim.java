package semester1.practice11.homework;

import java.util.*;

public class Task5StalkerInventorySim {
    public static void main(String[] args) {
        Stalker skif = new Stalker();
        Artifact[] finds = {
                new Artifact("ART-1", "Медуза", 0.4, -2, 15),
                new Artifact("ART-2", "Душа", 0.5, 3.5, 25),
                new Artifact("ART-1", "Медуза", 0.4, -2, 15),
                new Artifact("ART-3", "Компас", 0.6, 2.5, 40)
        };
        for (Artifact artifact : finds) {
            if (skif.equipArtifact(artifact)) {
                System.out.println("На поясі: " + artifact.getName());
            } else {
                System.out.println("Відхилено: " + artifact.getName());
            }
        }

        Medkit firstAid = new Medkit("M-1", "Звичайна аптечка", 0.3, 1);
        skif.pack(firstAid);
        skif.pack(new Medkit("M-2", "Армійська аптечка", 0.4, 2));
        skif.pack(new Detector("D-1", "Детектор Відгук", 0.5, 0));
        skif.pack(new Detector("D-2", "Детектор Велес", 0.7, 85));
        firstAid.use();
        System.out.println("Видалено предметів: " + skif.cleanBackpack());
        skif.printEquipment();

        skif.visit("Залісся");
        skif.visit("Кордон");
        skif.visit("Звалище");
        skif.visit("Янтар");
        HashSet<String> ally = new HashSet<>(List.of("Залісся", "Янтар", "Росток", "Прип'ять"));
        System.out.println("Локації Скіфа: " + skif.getVisitedLocationsCopy());
        System.out.println("Локації союзника: " + ally);
        System.out.println("Спільні: " + skif.getCommonLocations(ally));
        System.out.println("Нові для Скіфа: " + skif.getNewLocationsFromAlly(ally));
    }
}

abstract class ZoneItem {
    private final String id;
    private final String name;
    private final double weightKg;

    ZoneItem(String id, String name, double weightKg) {
        if (!(weightKg > 0)) throw new IllegalArgumentException("Вага має бути додатною");
        this.id = id;
        this.name = name;
        this.weightKg = weightKg;
    }

    public String getName() { return name; }
    public double getWeightKg() { return weightKg; }
    public abstract String getItemType();
    public abstract boolean isUsable();

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ZoneItem)) return false;
        ZoneItem item = (ZoneItem) other;
        return id.equals(item.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

class Artifact extends ZoneItem {
    private final double radiation;
    private final int protection;

    Artifact(String id, String name, double weight, double radiation, int protection) {
        super(id, name, weight);
        this.radiation = radiation;
        this.protection = protection;
    }

    public String getItemType() { return "Артефакт"; }
    public boolean isUsable() { return true; }
    public double getRadiation() { return radiation; }
    public int getProtection() { return protection; }
}

class Medkit extends ZoneItem {
    private int usesLeft;

    Medkit(String id, String name, double weight, int usesLeft) {
        super(id, name, weight);
        this.usesLeft = usesLeft;
    }

    public String getItemType() { return "Аптечка"; }
    public boolean isUsable() { return usesLeft > 0; }

    public boolean use() {
        if (!isUsable()) return false;
        usesLeft--;
        return true;
    }
}

class Detector extends ZoneItem {
    private final int batteryPercent;

    Detector(String id, String name, double weight, int batteryPercent) {
        super(id, name, weight);
        if (batteryPercent < 0 || batteryPercent > 100) {
            throw new IllegalArgumentException("Заряд має бути від 0 до 100");
        }
        this.batteryPercent = batteryPercent;
    }

    public String getItemType() { return "Детектор"; }
    public boolean isUsable() { return batteryPercent > 0; }
}

class Stalker {
    private final HashSet<Artifact> artifactBelt = new HashSet<>();
    private final ArrayList<ZoneItem> backpack = new ArrayList<>();
    private final HashSet<String> visitedLocations = new HashSet<>();

    public boolean equipArtifact(Artifact artifact) {
        if (artifact == null || artifactBelt.size() >= 4) return false;
        return artifactBelt.add(artifact);
    }

    public void pack(ZoneItem item) { backpack.add(item); }

    public int cleanBackpack() {
        Iterator<ZoneItem> it = backpack.iterator();
        int removed = 0;
        while (it.hasNext()) {
            ZoneItem item = it.next();
            if (!item.isUsable()) {
                System.out.println("Прибрано: " + item.getName());
                it.remove();
                removed++;
            }
        }
        return removed;
    }

    public double getTotalWeightKg() {
        double weight = 0;
        for (Artifact artifact : artifactBelt) weight += artifact.getWeightKg();
        for (ZoneItem item : backpack) weight += item.getWeightKg();
        return weight;
    }

    public double getNetRadiation() {
        double total = 0;
        for (Artifact artifact : artifactBelt) total += artifact.getRadiation();
        return total;
    }

    public int getTotalProtection() {
        int total = 0;
        for (Artifact artifact : artifactBelt) total += artifact.getProtection();
        return total;
    }

    public void printEquipment() {
        System.out.printf("Артефактів: %d, захист: %d%%, радіація: %+.2f мЗв/хв%n",
                artifactBelt.size(), getTotalProtection(), getNetRadiation());
        System.out.println("У рюкзаку залишилося: " + backpack.size());
        for (ZoneItem item : backpack) {
            System.out.println(item.getItemType() + ": " + item.getName());
        }
        System.out.printf("Загальна вага: %.2f кг%n", getTotalWeightKg());
    }

    public void visit(String location) { visitedLocations.add(location); }

    public HashSet<String> getVisitedLocationsCopy() {
        return new HashSet<>(visitedLocations);
    }

    public HashSet<String> getCommonLocations(HashSet<String> ally) {
        HashSet<String> common = getVisitedLocationsCopy();
        common.retainAll(ally);
        return common;
    }

    public HashSet<String> getNewLocationsFromAlly(HashSet<String> ally) {
        HashSet<String> fresh = new HashSet<>(ally);
        fresh.removeAll(visitedLocations);
        return fresh;
    }
}
