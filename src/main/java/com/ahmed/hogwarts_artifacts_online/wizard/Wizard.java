package com.ahmed.hogwarts_artifacts_online.wizard;

import com.ahmed.hogwarts_artifacts_online.artifact.Artifact;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity

public class Wizard {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "wizard")
    private List<Artifact> artifacts = new  ArrayList<>();

    public void addArtifact(Artifact artifact) {
        if(this.artifacts == null) {
            this.artifacts = new ArrayList<>();
        }
        artifact.setWizard(this);
        this.artifacts.add(artifact);
    }

    public int getNumberOfArtifacts() {
        return artifacts.size();
    }

    public void removeAllArtifacts() {
        this.artifacts.stream().forEach(artifact -> artifact.setWizard(null));
        this.artifacts = new ArrayList<>();
    }
}
