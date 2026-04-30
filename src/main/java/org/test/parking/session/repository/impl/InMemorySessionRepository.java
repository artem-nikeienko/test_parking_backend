package org.test.parking.session.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.test.parking.session.domain.ParkingSession;
import org.test.parking.session.repository.SessionRepository;

@Repository
public class InMemorySessionRepository implements SessionRepository {

    private final Map<String, ParkingSession> store = new ConcurrentHashMap<>();

    @Override
    public ParkingSession save(ParkingSession session) {
        store.put(session.getId(), session);
        return session;
    }

    @Override
    public Optional<ParkingSession> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<ParkingSession> findActiveByPlate(String plate) {
        return store.values().stream()
            .filter(s -> s.getVehicle().getLicensePlate().equalsIgnoreCase(plate))
            .filter(s -> s.isActive())
            .findFirst();
    }

    @Override
    public List<ParkingSession> findAllActive(String lotId) {
        List<ParkingSession> res = new ArrayList<>();
        store.values().stream()
            .filter(ParkingSession::isActive)
            .filter(s -> s.getSlotAssignment().getLotId().equals(lotId))
            .forEach(res::add);
        return res;
    }
}
