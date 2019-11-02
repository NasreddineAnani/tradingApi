package ca.ulaval.glo4002.trading.domain.profile;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.List;


@Embeddable
public class Profile {

    private String type;

    @ElementCollection
    private List<String> focusAreas;

    public Profile() {
        //for hibernate
    }

    public Profile(String type, List<String> focusAreas) {
        this.type = type;
        this.focusAreas = focusAreas;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getFocusAreas() {
        return this.focusAreas;
    }

    public void setFocusAreas(List<String> focusAreas) {
        this.focusAreas = focusAreas;
    }
}

