package org.test.parking.domain.policy;

import org.test.parking.domain.model.space.Slot;
import org.test.parking.domain.model.vehicle.Vehicle;

public interface SlotAvailabilityPolicy {

    //ASSUMPTION: This policy may be extended to let some vehicles be able to park in occupied or unavailable slots, for example, if the slot is occupied by a motorcycle and the vehicle is another bike, it may be allowed to park there.
    boolean isAvailable(Slot slot, Vehicle vehicle);
}
