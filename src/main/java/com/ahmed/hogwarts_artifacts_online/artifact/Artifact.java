package com.ahmed.hogwarts_artifacts_online.artifact;

import com.ahmed.hogwarts_artifacts_online.wizard.Wizard;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Artifact {
    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    private String description;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "wizard-id")
    private Wizard wizard;
}
