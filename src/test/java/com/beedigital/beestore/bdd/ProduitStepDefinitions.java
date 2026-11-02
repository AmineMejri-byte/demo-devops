package com.beedigital.beestore.bdd;

import com.beedigital.beestore.dto.ProduitRequestDTO;
import com.beedigital.beestore.dto.ProduitResponseDTO;
import com.beedigital.beestore.service.ProduitService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class ProduitStepDefinitions {

    @Autowired
    private ProduitService produitService;

    private ProduitRequestDTO requeteEnCours;
    private ProduitResponseDTO produitCree;

    @Given("a product named {string} with a price of {double} and a stock of {int}")
    public void unProduitAvecCesCaracteristiques(String nom, Double prix, Integer stock) {
        requeteEnCours = new ProduitRequestDTO();
        requeteEnCours.setNom(nom);
        requeteEnCours.setPrix(prix);
        requeteEnCours.setStock(stock);
    }

    @When("I submit the product creation")
    public void jeSoumetsLaCreationDuProduit() {
        produitCree = produitService.creerProduit(requeteEnCours);
    }

    @Then("the product is created successfully")
    public void leProduitEstCreeAvecSucces() {
        assertThat(produitCree.getId()).isNotNull();
    }

    @Then("the created product has the name {string}")
    public void leProduitCreeAPourNom(String nomAttendu) {
        assertThat(produitCree.getNom()).isEqualTo(nomAttendu);
    }
}