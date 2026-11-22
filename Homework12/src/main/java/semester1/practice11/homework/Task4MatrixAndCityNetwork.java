package semester1.practice11.homework;

import java.util.*;

public class Task4MatrixAndCityNetwork {
    public static void main(String[] args) {
        ArrayList<String> cities = new ArrayList<>(List.of("Київ", "Львів", "Одеса", "Дніпро"));
        int[][] distances = {
                {0, 540, 480, 450},
                {540, 0, 790, 0},
                {480, 790, 0, 460},
                {450, 0, 460, 0}
        };
        RouteDispatcher dispatcher = new RouteDispatcher(cities, distances, new HashSet<>(List.of("Одеса")));
        System.out.println("Сусіди Києва: " + dispatcher.getDirectNeighbors("Київ"));
        System.out.println("Сусіди Львова: " + dispatcher.getDirectNeighbors("Львів"));
        System.out.println("Спільні: " + dispatcher.getCommonNeighbors("Київ", "Львів"));
        System.out.println("Тільки з Києва: " + dispatcher.getExclusiveNeighbors("Київ", "Львів"));

        dispatcher.addOrder(new TripOrder("ORD-1", "Київ", "Львів", new Train()));
        dispatcher.addOrder(new TripOrder("ORD-1", "Київ", "Львів", new Bus()));
        dispatcher.addOrder(new TripOrder("ORD-2", "Київ", "Одеса", new Bus()));
        dispatcher.addOrder(new TripOrder("ORD-3", "Львів", "Дніпро", new DeliveryTruck()));
        dispatcher.addOrder(new TripOrder("ORD-4", "Київ", "Дніпро", new DeliveryTruck()));
        System.out.println("Видалено заявок: " + dispatcher.filterInvalidOrders());
        dispatcher.printTrips();
    }
}

abstract class Transport {
    private final String name;
    private final double speedKmH;

    Transport(String name, double speedKmH) {
        this.name = name;
        this.speedKmH = speedKmH;
    }

    public String getName() { return name; }

    public double calculateTimeHours(int distanceKm) {
        return distanceKm / speedKmH;
    }

    public abstract double calculateCostUah(int distanceKm);
}

class Bus extends Transport {
    Bus() {
        super("Автобус", 80);
    }

    public double calculateCostUah(int distanceKm) {
        return distanceKm * 2.5;
    }
}

class Train extends Transport {
    Train() {
        super("Потяг", 100);
    }

    public double calculateCostUah(int distanceKm) {
        return distanceKm * 1.8 + 150;
    }
}

class DeliveryTruck extends Transport {
    DeliveryTruck() {
        super("Вантажівка", 70);
    }

    public double calculateCostUah(int distanceKm) {
        return distanceKm * 4 + 300;
    }
}

class TripOrder {
    private final String orderId;
    private final String fromCity;
    private final String toCity;
    private final Transport transport;

    TripOrder(String orderId, String fromCity, String toCity, Transport transport) {
        this.orderId = orderId;
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.transport = transport;
    }

    public String getOrderId() { return orderId; }
    public String getFromCity() { return fromCity; }
    public String getToCity() { return toCity; }
    public Transport getTransport() { return transport; }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TripOrder)) return false;
        TripOrder item = (TripOrder) other;
        return orderId.equals(item.orderId);
    }

    @Override
    public int hashCode() {
        return orderId.hashCode();
    }
}

class RouteDispatcher {
    private final ArrayList<String> cities;
    private final int[][] distances;
    private final HashSet<String> closedCities;
    private final ArrayList<TripOrder> orders = new ArrayList<>();

    RouteDispatcher(ArrayList<String> cities, int[][] distances, HashSet<String> closedCities) {
        this.cities = new ArrayList<>(cities);
        this.closedCities = new HashSet<>(closedCities);
        this.distances = new int[distances.length][];
        for (int i = 0; i < distances.length; i++) {
            this.distances[i] = distances[i].clone();
        }
    }

    public void addOrder(TripOrder order) { orders.add(order); }

    public int getDistance(String from, String to) {
        int i = cities.indexOf(from);
        int j = cities.indexOf(to);
        if (i < 0 || j < 0) return 0;
        return distances[i][j];
    }

    public HashSet<String> getDirectNeighbors(String city) {
        HashSet<String> neighbors = new HashSet<>();
        int row = cities.indexOf(city);
        if (row < 0) return neighbors;
        for (int i = 0; i < cities.size(); i++) {
            if (distances[row][i] > 0) neighbors.add(cities.get(i));
        }
        return neighbors;
    }

    public HashSet<String> getCommonNeighbors(String a, String b) {
        HashSet<String> result = getDirectNeighbors(a);
        result.retainAll(getDirectNeighbors(b));
        return result;
    }

    public HashSet<String> getExclusiveNeighbors(String a, String b) {
        HashSet<String> result = getDirectNeighbors(a);
        result.removeAll(getDirectNeighbors(b));
        return result;
    }

    public int filterInvalidOrders() {
        HashSet<String> seenIds = new HashSet<>();
        Iterator<TripOrder> it = orders.iterator();
        int removed = 0;
        while (it.hasNext()) {
            TripOrder order = it.next();
            String from = order.getFromCity();
            String to = order.getToCity();
            String reason;
            if (!seenIds.add(order.getOrderId())) {
                reason = "повторний номер";
            } else if (closedCities.contains(from) || closedCities.contains(to)) {
                reason = "місто закрите";
            } else if (getDistance(from, to) <= 0) {
                reason = "немає прямої дороги";
            } else {
                continue;
            }
            System.out.println(order.getOrderId() + ": " + reason);
            it.remove();
            removed++;
        }
        return removed;
    }

    public void printTrips() {
        int totalKm = 0;
        double totalCost = 0;
        for (TripOrder order : orders) {
            int km = getDistance(order.getFromCity(), order.getToCity());
            Transport transport = order.getTransport();
            double cost = transport.calculateCostUah(km);
            System.out.printf("%s: %s — %s, %s, %d км, %.2f год, %.2f грн%n",
                    order.getOrderId(), order.getFromCity(), order.getToCity(), transport.getName(),
                    km, transport.calculateTimeHours(km), cost);
            totalKm += km;
            totalCost += cost;
        }
        System.out.printf("Схвалено: %d. Разом: %d км, %.2f грн%n", orders.size(), totalKm, totalCost);
    }
}
