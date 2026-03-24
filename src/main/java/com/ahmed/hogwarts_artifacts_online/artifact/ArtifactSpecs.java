package com.ahmed.hogwarts_artifacts_online.artifact;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

public class ArtifactSpecs {

    public static Specification<Artifact> hasId (Integer number) {
        return new Specification<Artifact>() {
            @Override
            public @Nullable Predicate toPredicate(Root<Artifact> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("id"),number);
            }
        };
    }

    public static Specification<Artifact> containsName (String name) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Artifact> containsDescription (String description) {
        return (root, query, criteriaBuilder) -> {
            return  criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + description.toLowerCase() + "%");
        };
    }

    public static  Specification<Artifact> hasWizard (String wizardName) {
        return (root, query, criteriaBuilder) -> {
            return  criteriaBuilder.equal(criteriaBuilder.lower(root.get("wizard").get("name")), wizardName.toLowerCase());
        };
    }
}
