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
@Table(name="artifacts")
public class Artifact {
    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    private String description;

    @Column(name="image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "wizard_id")
    private Wizard wizard;
}
