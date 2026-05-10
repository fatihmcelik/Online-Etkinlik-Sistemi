package com.etkinlik.online_etkinlik.service;

import com.etkinlik.online_etkinlik.model.Location;
import com.etkinlik.online_etkinlik.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Location getById(Long id) {
        return locationRepository.findById(id).orElseThrow(() -> new RuntimeException("Mekan bulunamadı"));
    }

    @Transactional
    public void save(Location location) {
        locationRepository.save(location);
    }

    @Transactional
    public void delete(Long id) {
        try {
            locationRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Bu mekanda kayıtlı etkinlikler olduğu için silinemez!");
        }
    }
}