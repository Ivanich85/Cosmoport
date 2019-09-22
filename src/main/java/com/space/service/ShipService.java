package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ShipService {

    List<Ship> getShips(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed,
                        Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize,
                        Double minRating, Double maxRating, ShipOrder order, Integer pageNumber, Integer pageSize);

    Integer getShipsCount(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed,
                        Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize,
                        Double minRating, Double maxRating);

    Ship getShip(Integer id);

    ResponseEntity<Ship> createShip(Ship ship);

    ResponseEntity<Ship> updateShip(Integer id, Ship ship);

    void deleteShip(Integer id);
}
