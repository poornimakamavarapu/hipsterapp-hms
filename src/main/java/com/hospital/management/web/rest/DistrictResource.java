package com.hospital.management.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.hospital.management.domain.District;

import com.hospital.management.repository.DistrictRepository;
import com.hospital.management.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing District.
 */
@RestController
@RequestMapping("/api")
public class DistrictResource {

    private final Logger log = LoggerFactory.getLogger(DistrictResource.class);
        
    @Inject
    private DistrictRepository districtRepository;

    /**
     * POST  /districts : Create a new district.
     *
     * @param district the district to create
     * @return the ResponseEntity with status 201 (Created) and with body the new district, or with status 400 (Bad Request) if the district has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/districts")
    @Timed
    public ResponseEntity<District> createDistrict(@RequestBody District district) throws URISyntaxException {
        log.debug("REST request to save District : {}", district);
        if (district.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("district", "idexists", "A new district cannot already have an ID")).body(null);
        }
        District result = districtRepository.save(district);
        return ResponseEntity.created(new URI("/api/districts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("district", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /districts : Updates an existing district.
     *
     * @param district the district to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated district,
     * or with status 400 (Bad Request) if the district is not valid,
     * or with status 500 (Internal Server Error) if the district couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/districts")
    @Timed
    public ResponseEntity<District> updateDistrict(@RequestBody District district) throws URISyntaxException {
        log.debug("REST request to update District : {}", district);
        if (district.getId() == null) {
            return createDistrict(district);
        }
        District result = districtRepository.save(district);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("district", district.getId().toString()))
            .body(result);
    }

    /**
     * GET  /districts : get all the districts.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of districts in body
     */
    @GetMapping("/districts")
    @Timed
    public List<District> getAllDistricts() {
        log.debug("REST request to get all Districts");
        List<District> districts = districtRepository.findAll();
        return districts;
    }

    /**
     * GET  /districts/:id : get the "id" district.
     *
     * @param id the id of the district to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the district, or with status 404 (Not Found)
     */
    @GetMapping("/districts/{id}")
    @Timed
    public ResponseEntity<District> getDistrict(@PathVariable Long id) {
        log.debug("REST request to get District : {}", id);
        District district = districtRepository.findOne(id);
        return Optional.ofNullable(district)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /districts/:id : delete the "id" district.
     *
     * @param id the id of the district to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/districts/{id}")
    @Timed
    public ResponseEntity<Void> deleteDistrict(@PathVariable Long id) {
        log.debug("REST request to delete District : {}", id);
        districtRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("district", id.toString())).build();
    }

}
