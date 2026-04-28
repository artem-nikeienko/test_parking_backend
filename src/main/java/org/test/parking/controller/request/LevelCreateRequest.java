package org.test.parking.controller.request;

import lombok.Data;

@Data
public class LevelCreateRequest {
    
    //ASSUMPTION: I assume that level may be underground, so it can have negative number. For example, -1 for underground level, 0 for ground level, 1 for first floor, etc.
    private int number;
}
