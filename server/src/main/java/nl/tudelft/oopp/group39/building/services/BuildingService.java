package nl.tudelft.oopp.group39.building.services;

import java.util.List;
import java.util.Map;
import nl.tudelft.oopp.group39.building.dao.BuildingDao;
import nl.tudelft.oopp.group39.building.entities.Building;
import nl.tudelft.oopp.group39.building.exceptions.BuildingNotFoundException;
import nl.tudelft.oopp.group39.building.repositories.BuildingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BuildingService {
    @Autowired
    private BuildingRepository buildingRepository;
    @Autowired
    private BuildingDao buildingDao;

    /**
     * List all buildings.
     *
     * @return a list of buildings
     */
    public List<Building> listBuildings(Map<String,String> params) {
        return buildingDao.buildingFilter(params);
    }

    /**
     * Reads the building inside the database using its id.
     *
     * @param id the id of the Building
     * @return the Building that was found.
     * @throws BuildingNotFoundException when no building is found.
     */
    public Building readBuilding(Long id) throws BuildingNotFoundException {
        return buildingRepository.findById(id)
            .orElseThrow(() -> new BuildingNotFoundException(id));
    }

    /**
     * Delete a building.
     */
    public Building deleteBuilding(Long id) throws BuildingNotFoundException {
        try {
            Building rf = readBuilding(id);
            buildingRepository.delete(readBuilding(id));
            return rf;
        } catch (BuildingNotFoundException e) {
            throw new BuildingNotFoundException(id);
        }
    }

    /**
     * Create a building.
     *
     * @return the created building
     */
    public Building createBuilding(Building newBuilding) {
        return buildingRepository.save(newBuilding);
    }

    /**
     * Update a building.
     *
     * @return the updated booking
     */
    public Building updateBuilding(Long id, Building newBuilding) throws BuildingNotFoundException {
        return buildingRepository.findById(id)
            .map(building -> {
                newBuilding.setId(id);
                building = newBuilding;
                return buildingRepository.save(building);
            }).orElseThrow(() -> new BuildingNotFoundException(id));
    }
}
