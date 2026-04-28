package org.test.parking.repository;

import java.util.List;
import java.util.Optional;

import org.test.parking.domain.space.Lot;

public interface LotRepository {

     /**
     * Persists a parking lot.
     *
     * @param lot Lot to save.
     * 
     * @return Saved lot instance.
     *
     * @throws IllegalArgumentException if lot is null.
     */
     Lot save(Lot lot);

     /**
      * Finds a parking lot by its identifier.
      *
      * @param id Lot identifier.
      * 
      * @return Optional containing {@link Lot} if found.
      */
     Optional<Lot> findById(String id);
 
     /**
      * Finds a parking lot by its name.
      *
      * @param name Lot name.
      * 
      * @return Optional containing {@link Lot} if found.
      */
     Optional<Lot> findByName(String name);
 
     /**
      * Deletes a parking lot by its identifier.
      *
      * @param id Lot identifier.
      * 
      * @return Optional containing {@link Lot} if deleted, empty otherwise.
      * 
      * Notes:
      * - Operation is idempotent (no error if lot does not exist).
      */
     Optional<Lot> delete(String id);
 
     /**
      * Retrieves all parking lots.
      *
      * @return List of lots.
      */
     List<Lot> findAll();
}
