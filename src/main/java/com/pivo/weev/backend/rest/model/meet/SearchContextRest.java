package com.pivo.weev.backend.rest.model.meet;

import static java.util.Objects.nonNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchContextRest {

    private VisibilityCriteriaRest visibilityCriteria;
    private Long authorId;

    public boolean hasAuthorId() {
        return nonNull(authorId);
    }

    public static SearchContextRest published() {
        SearchContextRest context = new SearchContextRest();
        context.setVisibilityCriteria(VisibilityCriteriaRest.published());
        return context;
    }

    public static SearchContextRest published(Long authorId) {
        SearchContextRest context = published();
        context.setAuthorId(authorId);
        return context;
    }

    public static SearchContextRest onModeration() {
        SearchContextRest context = new SearchContextRest();
        context.setVisibilityCriteria(VisibilityCriteriaRest.onModeration());
        return context;
    }

    public static SearchContextRest onModeration(Long authorId) {
        SearchContextRest context = onModeration();
        context.setAuthorId(authorId);
        return context;
    }

    public static SearchContextRest canceled(Long authorId) {
        SearchContextRest context = new SearchContextRest();
        context.setVisibilityCriteria(VisibilityCriteriaRest.canceled());
        context.setAuthorId(authorId);
        return context;
    }

    public static SearchContextRest declined(Long authorId) {
        SearchContextRest context = new SearchContextRest();
        context.setVisibilityCriteria(VisibilityCriteriaRest.declined());
        context.setAuthorId(authorId);
        return context;
    }

    @Getter
    @Setter
    public static class VisibilityCriteriaRest {

        private boolean published;
        private boolean onModeration;
        private boolean canceled;
        private boolean declined;

        public static VisibilityCriteriaRest published() {
            VisibilityCriteriaRest criteria = new VisibilityCriteriaRest();
            criteria.setPublished(true);
            return criteria;
        }

        public static VisibilityCriteriaRest onModeration() {
            VisibilityCriteriaRest criteria = new VisibilityCriteriaRest();
            criteria.setOnModeration(true);
            return criteria;
        }

        public static VisibilityCriteriaRest canceled() {
            VisibilityCriteriaRest criteria = new VisibilityCriteriaRest();
            criteria.setCanceled(true);
            return criteria;
        }

        public static VisibilityCriteriaRest declined() {
            VisibilityCriteriaRest criteria = new VisibilityCriteriaRest();
            criteria.setDeclined(true);
            return criteria;
        }
    }
}
