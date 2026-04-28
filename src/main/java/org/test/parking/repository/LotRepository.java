package org.test.parking.repository;

import java.util.*;
import org.test.parking.domain.space.Lot;

public interface LotRepository {
    Lot save(Lot lot);
    Optional<Lot> findById(String id);
    Optional<Lot> findByName(String name);
    void delete(String id);
    List<Lot> findAll();
}
