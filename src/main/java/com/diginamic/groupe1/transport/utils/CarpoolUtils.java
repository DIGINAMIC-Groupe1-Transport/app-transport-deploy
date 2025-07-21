package com.diginamic.groupe1.transport.utils;

import com.diginamic.groupe1.transport.entity.Carpool;
import org.springframework.stereotype.Service;

@Service
public class CarpoolUtils {

    public int calculateAvailableSeats(Carpool carpool){
        return carpool.getInitialAvailableSeats() - carpool.getParticipants().size();
    }

    public int calculateRemainingSeats(Carpool carpool){
        return carpool.getParticipants().size();
    }
}
