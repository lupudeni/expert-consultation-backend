package ro.code4.expertconsultation.document.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import ro.code4.expertconsultation.document.model.DocumentFilter;
import ro.code4.expertconsultation.document.model.persistence.QDocument;

import java.util.Objects;

public class DocumentPredicateFactory {

    private DocumentPredicateFactory() {
    }

    public static Predicate byFilter(final DocumentFilter filter) {
        final QDocument document = QDocument.document;
        final BooleanBuilder whereFilter = new BooleanBuilder();

        Objects.requireNonNull(filter.getState());
        Objects.requireNonNull(filter.getTitle());
        Objects.requireNonNull(filter.getType());

        whereFilter.and(document.type.eq(filter.getType()));
        whereFilter.and(document.state.eq(filter.getState()));
        whereFilter.and(document.title.likeIgnoreCase("%" + filter.getTitle() + "%"));

        return whereFilter;
    }

}
