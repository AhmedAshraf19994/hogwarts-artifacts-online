package com.ahmed.hogwarts_artifacts_online.system;

import com.ahmed.hogwarts_artifacts_online.artifact.Artifact;
import com.ahmed.hogwarts_artifacts_online.artifact.ArtifactRepository;
import com.ahmed.hogwarts_artifacts_online.user.Role;
import com.ahmed.hogwarts_artifacts_online.user.User;
import com.ahmed.hogwarts_artifacts_online.user.UserRepository;
import com.ahmed.hogwarts_artifacts_online.user.UserService;
import com.ahmed.hogwarts_artifacts_online.user.dto.CreateUserDto;
import com.ahmed.hogwarts_artifacts_online.wizard.Wizard;
import com.ahmed.hogwarts_artifacts_online.wizard.WizardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Profile("dev")
public class DBtTestInitializer implements CommandLineRunner {

    private final WizardRepository wizardRepository;
    private final ArtifactRepository artifactRepository;
    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {

        Wizard wizardOne = Wizard.builder().name("Harry Potter").build();
        Wizard wizardTwo = Wizard.builder().name("Hermione Granger").build();
        Wizard wizardThree = Wizard.builder().name("Albus Dumbledore").build();

        Artifact artifactOne = Artifact.builder().name("Resurrection Stone")
                .description("the Resurrection Stone had the power to bring back lost loved ones.")
                .imageUrl("imageUrl").build();
        Artifact artifactTwo = Artifact.builder().name("Cloak of Invisibility")
                .description("magical garment that renders the wearer unseen.")
                .imageUrl("imageUrl").build();
        Artifact artifactThree = Artifact.builder().name("Philosopher's Stone")
                .description("transmutation (metal to gold) and rejuvenation (immortality).")
                .imageUrl("imageUrl").build();
        Artifact artifactFour = Artifact.builder().name("Mirror of Erised")
                .description("It reflects the viewer's deepest, most desperate desire.")
                .imageUrl("imageUrl").build();
        Artifact artifactFive = Artifact.builder().name("Knight Bus")
                .description("purple bus that can squeeze through narrow spaces and defies physics.")
                .imageUrl("imageUrl").build();
        Artifact artifactSix = Artifact.builder().name("Time-Turner")
                .description("sparkling hourglass on a long gold chain, often featuring inner rings that rotate.")
                .imageUrl("imageUrl").build();

        CreateUserDto userA = new CreateUserDto("Ahmed","12345",Role.ADMIN);
        CreateUserDto userB = new CreateUserDto("John","56789",Role.USER);
        CreateUserDto userC = new CreateUserDto("Mike","faaffssf",Role.USER);

        wizardOne.addArtifact(artifactOne);
        wizardOne.addArtifact(artifactTwo);

        wizardTwo.addArtifact(artifactThree);
        wizardTwo.addArtifact(artifactFour);

        wizardThree.addArtifact(artifactSix);

        wizardRepository.save(wizardOne);
        wizardRepository.save(wizardTwo);
        wizardRepository.save(wizardThree);

        artifactRepository.save(artifactFive);

        userService.saveUser(userA);
        userService.saveUser(userB);
        userService.saveUser(userC);

    }
}
