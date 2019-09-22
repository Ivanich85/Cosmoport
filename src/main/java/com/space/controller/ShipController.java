package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value="/rest/ships")
public class ShipController {

    @Resource
    private ShipService shipService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Ship> getShips(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false)ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating,
            @RequestParam(value = "order", required = false) ShipOrder order,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize
    ) {
        return shipService.getShips(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize,
                maxCrewSize, minRating, maxRating, order, pageNumber, pageSize);
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public Integer getShipsCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false)ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating
    ) {
        return shipService.getShipsCount(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize,
                maxCrewSize, minRating, maxRating);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Ship getShip(@PathVariable Integer id) {
        return shipService.getShip(id);
    }

    @RequestMapping(method = RequestMethod.POST, consumes="application/json", headers="Accept=application/json")
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {
        return shipService.createShip(ship);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, consumes="application/json", headers="Accept=application/json")
    public ResponseEntity<Ship> updateShip(@PathVariable Integer id, @RequestBody Ship ship) {
        return shipService.updateShip(id, ship);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteShip(@PathVariable Integer id) {
        shipService.deleteShip(id);
    }
}
