package org.test.parking.repository.inmemory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.test.parking.domain.session.ParkingSession;
import org.test.parking.domain.session.SessionStatus;
import org.test.parking.repository.SessionRepository;

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
    public List<ParkingSession> findAllActive() {
        List<ParkingSession> res = new ArrayList<>();
        for (ParkingSession s : store.values()) {
            if (s.getStatus() == SessionStatus.ACTIVE) res.add(s);
        }
        return res;
    }
}
