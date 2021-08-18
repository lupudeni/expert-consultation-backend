package ro.code4.expertconsultation.document.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentFilter {

    private DocumentType type;

    private DocumentState state;

    private String title;
}
