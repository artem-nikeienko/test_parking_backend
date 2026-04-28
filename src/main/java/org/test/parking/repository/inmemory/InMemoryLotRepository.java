package org.test.parking.repository.inmemory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.test.parking.domain.space.Lot;
import org.test.parking.repository.LotRepository;

@Repository
public class InMemoryLotRepository implements LotRepository {

    private final Map<String, Lot> store = new ConcurrentHashMap<>();

    @Override
    public Lot save(Lot lot) {
        store.put(lot.getId(), lot);
        return lot;
    }

    @Override
    public Optional<Lot> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Lot> findByName(String name) {
        return store.values().stream()
                .filter(l -> l.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public Optional<Lot> delete(String id) {
        Lot deletedLot = store.remove(id);
        return Optional.ofNullable(deletedLot);
    }

    @Override
    public List<Lot> findAll() {
        return new ArrayList<>(store.values());
    }
}
