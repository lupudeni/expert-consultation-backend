package ro.code4.expertconsultation.organization.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationFilter {

    private OrganizationCategory category;

    private String name;
}
