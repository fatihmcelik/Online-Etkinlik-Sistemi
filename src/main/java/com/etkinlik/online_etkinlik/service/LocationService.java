package com.etkinlik.online_etkinlik.service;

import com.etkinlik.online_etkinlik.model.Location;
import com.etkinlik.online_etkinlik.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {
    
    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public List<Location> getAll() {
        return locationRepository.findAll();
    }
}