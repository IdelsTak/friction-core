package com.github.idelstak.friction.core.readmodel;

import java.util.*;

public interface ObservationDetailStore {

    ObservationDetailStore save(ObservationDetail detail);

    List<ObservationDetail> byFriction(String frictionId);

    ObservationDetailStore clear();
}
