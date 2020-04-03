package nl.tudelft.oopp.group39.controllers.admin.room;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import nl.tudelft.oopp.group39.facility.model.Facility;
import nl.tudelft.oopp.group39.models.Building;
import nl.tudelft.oopp.group39.room.model.Room;
import nl.tudelft.oopp.group39.server.communication.ServerCommunication;

public class RoomViewController extends RoomListController {

    private Stage currentStage;
    private ObjectMapper mapper = new ObjectMapper();
    @FXML
    private Button backbtn;
    @FXML
    private TextField nameFieldView;
    @FXML
    private TextField descriptionFieldView;
    @FXML
    private TextField buildingFieldView;
    @FXML
    private TextField onlyStaffFieldView;
    @FXML
    private TextField capacityFieldView;
    @FXML
    private TextArea facilitiesBox;
    @FXML
    private MenuBar navBar;

    public void customInit() {
        this.currentStage = (Stage) backbtn.getScene().getWindow();
        setNavBar(navBar, currentStage);
    }
    /**
     * Initialize rooms data into their respective boxes to be used for updating.
     * @throws JsonProcessingException when there is a processing exception.
     */

    public void initData(Room room) throws JsonProcessingException {
        customInit();
        String nnnBuilding = ServerCommunication.getBuilding(room.getBuilding());
        System.out.println(nnnBuilding);
        ObjectNode body = (ObjectNode) mapper.readTree(nnnBuilding).get("body");
        nnnBuilding = mapper.writeValueAsString(body);
        Building building = mapper.readValue(nnnBuilding, Building.class);
        nameFieldView.setText(room.getName());
        nameFieldView.setDisable(true);
        buildingFieldView.setText(building.getName());
        buildingFieldView.setDisable(true);
        descriptionFieldView.setText(room.getDescription());
        descriptionFieldView.setDisable(true);
        onlyStaffFieldView.setText(getOnlyStaff(room));
        onlyStaffFieldView.setDisable(true);
        capacityFieldView.setText(Integer.toString(room.getCapacity()));
        capacityFieldView.setDisable(true);
        facilitiesBox.setText(getFacilitiesString(room));
        facilitiesBox.setDisable(true);
    }
    /**
     * Returns the rooms facilities as a string.
     */

    public String getFacilitiesString(Room room) throws JsonProcessingException {
        ArrayNode body = room.getFacilities();
        String facilities = mapper.writeValueAsString(body);
        Facility[] facilitiesList = mapper.readValue(facilities, Facility[].class);
        StringBuilder facilitiesString = new StringBuilder();
        for (Facility facility : facilitiesList) {
            facilitiesString.append(facility.getDescription()).append("\n");
        }
        return facilitiesString.toString();
    }

    /**
     * Goes back to main Room panel.
     * TODO -- why doesn't this work?
     */

    @FXML
    private void getBack() throws IOException {
        switchRoomView(currentStage);
    }

}
