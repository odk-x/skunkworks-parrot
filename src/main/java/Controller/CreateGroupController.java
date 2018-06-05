package Controller;

import Model.Group;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateGroupController implements Initializable {
    public TextField name_field;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void createButtonClicked(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount()==1)
            if(!name_field.getText().trim().equals(""))
                createNewGroup(name_field.getText());
        
    }

    private void createNewGroup(String groupName) {
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("group");
        DatabaseReference pushedPostRef = groupRef.push();
        groupRef.child(pushedPostRef.getKey()).setValueAsync(new Group(pushedPostRef.getKey(),groupName));



    }
}
