
package com.beedigital.beestore.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProduitRequestDTOTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    private ProduitRequestDTO creerDTOValide() {
        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("Clavier");
        dto.setDescription("Clavier sans fil");
        dto.setPrix(120.0);
        dto.setStock(10);
        dto.setCategorie("Informatique");
        return dto;
    }

    @Test
    void dtoValideNeDoitPasAvoirDeViolations() {
        ProduitRequestDTO dto = creerDTOValide();

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void nomVideDoitEtreRefuse() {
        ProduitRequestDTO dto = creerDTOValide();
        dto.setNom("");

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void nomTropCourtDoitEtreRefuse() {
        ProduitRequestDTO dto = creerDTOValide();
        dto.setNom("A");

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void prixNegatifDoitEtreRefuse() {
        ProduitRequestDTO dto = creerDTOValide();
        dto.setPrix(-10.0);

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void prixAbsentDoitEtreRefuse() {
        ProduitRequestDTO dto = creerDTOValide();
        dto.setPrix(null);

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void stockNegatifDoitEtreRefuse() {
        ProduitRequestDTO dto = creerDTOValide();
        dto.setStock(-1);

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void stockZeroDoitEtreAccepte() {
        ProduitRequestDTO dto = creerDTOValide();
        dto.setStock(0);

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void descriptionTropLongueDoitEtreRefusee() {
        ProduitRequestDTO dto = creerDTOValide();
        dto.setDescription("a".repeat(501));

        assertFalse(validator.validate(dto).isEmpty());
    }
}
