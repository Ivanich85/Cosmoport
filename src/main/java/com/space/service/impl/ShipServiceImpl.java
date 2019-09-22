package com.space.service.impl;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ShipServiceImpl implements ShipService {

    @Autowired
    private ShipRepository shipRepository;

    @Override
    public List<Ship> getShips(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed,
                               Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize,
                               Double minRating, Double maxRating, ShipOrder order, Integer pageNumber, Integer pageSize) {
        String sortParam = order != null ? order.getFieldName() : "id";
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortParam));
        Date dateAfter = after == null ? null : new Date(after);
        Date dateBefore = before == null ? null : new Date(before);

        return shipRepository.getShips(name, planet, shipType, dateAfter, dateBefore, isUsed, minSpeed, maxSpeed, minCrewSize,
                maxCrewSize, minRating, maxRating, pageable);
    }

    @Override
    public Integer getShipsCount(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed,
                             Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize,
                             Double minRating, Double maxRating) {
        Date dateAfter = after == null ? null : new Date(after);
        Date dateBefore = before == null ? null : new Date(before);
        return shipRepository.getShips(name, planet, shipType, dateAfter, dateBefore, isUsed, minSpeed, maxSpeed, minCrewSize,
                maxCrewSize, minRating, maxRating, null).size();
    }

    @Override
    public Ship getShip(Integer id) {
        if (!isIdValid(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Optional<Ship> ship = shipRepository.findById(id);
        if (!ship.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ship.get();
    }

    @Override
    public ResponseEntity<Ship> createShip(Ship ship) {
        if (!isShipDataValid(ship)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Date prodDate = ship.getProdDate();
        Boolean isUsed = ship.isUsed();
        Double speed = ship.getSpeed();

        Ship newShip = new Ship();
        newShip.setName(ship.getName());
        newShip.setPlanet(ship.getPlanet());
        newShip.setShipType(ship.getShipType());
        newShip.setProdDate(prodDate);
        newShip.setUsed(isUsed);
        newShip.setSpeed(speed);
        newShip.setCrewSize(ship.getCrewSize());
        newShip.setRating(calculateRating(speed, isUsed, prodDate.getTime()));
        newShip = shipRepository.save(newShip);
        return new ResponseEntity<>(newShip, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Ship> updateShip(Integer id, Ship ship) {
        Ship updateShip = null;
        if (isIdValid(id)) {
            updateShip = getShip(id);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (ship.isEmptyShip()) {
            return new ResponseEntity<>(updateShip, HttpStatus.OK);
        }
        if (!fieldIsNull(ship.getRating())) updateShip.setRating(ship.getRating());
        if (!fieldIsNull(ship.getCrewSize())) updateShip.setCrewSize(ship.getCrewSize());
        if (!fieldIsNull(ship.getSpeed())) updateShip.setSpeed(ship.getSpeed());
        if (!fieldIsNull(ship.isUsed())) updateShip.setUsed(ship.isUsed());
        if (!fieldIsNull(ship.getShipType())) updateShip.setShipType(ship.getShipType());
        if (!fieldIsNull(ship.getPlanet())) updateShip.setPlanet(ship.getPlanet());
        if (!fieldIsNull(ship.getName())) updateShip.setName(ship.getName());
        if (!fieldIsNull(ship.getProdDate())) updateShip.setProdDate(ship.getProdDate());
        updateShip.setRating(calculateRating(updateShip.getSpeed(), updateShip.isUsed(), updateShip.getProdDate().getTime()));

        if (!isShipDataValid(updateShip)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        updateShip = shipRepository.save(updateShip);
        return new ResponseEntity<>(updateShip, HttpStatus.OK);
    }

    @Override
    public void deleteShip(Integer id) {
        Ship deletedShip = getShip(id);
        shipRepository.delete(deletedShip);
    }

    private boolean fieldIsNull(Object object) {
        return object == null;
    }

    private Boolean isShipDataValid(Ship ship) {
        if (ship == null) {
            return false;
        }
        String name = ship.getName();
        String planet = ship.getPlanet();
        ShipType shipType = ship.getShipType();
        Date prodDate = ship.getProdDate();
        Double speed = ship.getSpeed();
        Integer crewSize = ship.getCrewSize();

        Boolean isNameValid = name != null && !name.trim().equals("") && name.length() < 51;
        Boolean isPlanetValid = planet != null && !planet.trim().equals("") && planet.length() < 51;
        Boolean isShipTypeValid = shipType != null;
        Boolean isProdDateValid = false;
        if (prodDate != null && prodDate.getTime() > -1L) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(prodDate.getTime()));
            Integer prodYear = calendar.get(Calendar.YEAR);
            if (prodYear >= 2800 && prodYear <= 3019) {
                isProdDateValid = true;
            }
        }
        speed = speed != null ? new BigDecimal(speed).setScale(2, RoundingMode.UP).doubleValue() : 0.0;
        Boolean isSpeedValid = speed >=0.01 && speed <= 0.99;
        Boolean isCrewSizeValid = crewSize != null && crewSize >= 1 && crewSize <= 9999;

        return isNameValid && isPlanetValid && isShipTypeValid && isProdDateValid && isSpeedValid && isCrewSizeValid;
    }

    private Double calculateRating(Double speed, Boolean isUsed, Long prodDate) {
        Double coef = isUsed ? 0.5 : 1;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(prodDate));
        Double rating = 80 * speed * coef / (3019 - calendar.get(Calendar.YEAR) + 1);
        return new BigDecimal(rating).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
    }

    private boolean isIdValid(Integer id) {
        try {
            Integer.parseInt(id.toString());
        } catch (Exception e) {
            return false;
        }
        if (id == null || id < 1) {
            return false;
        }
        return true;
    }
}
