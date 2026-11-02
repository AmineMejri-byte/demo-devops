
package com.beedigital.beestore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProduitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String produitJson(String nom, double prix, int stock) {
        return """
                {
                  "nom": "%s",
                  "prix": %s,
                  "stock": %d,
                  "categorie": "Informatique",
                  "description": "Produit de test",
                  "actif": true
                }
                """.formatted(nom, prix, stock);
    }

    @Test
    @DisplayName("POST - Créer un produit")
    void creerProduit() throws Exception {
        mockMvc.perform(post("/api/v1/produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produitJson("Clavier", 89.90, 25)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nom").value("Clavier"));
    }

    @Test
    @DisplayName("POST - Refuser un nom vide")
    void creerProduitNomVide() throws Exception {
        String json = """
                {
                  "nom": "",
                  "prix": 10.0,
                  "stock": 1
                }
                """;

        mockMvc.perform(post("/api/v1/produits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET - Récupérer tous les produits")
    void getTousProduits() throws Exception {
        mockMvc.perform(get("/api/v1/produits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET - Récupérer un produit existant")
    void getProduitById() throws Exception {
        String response = mockMvc.perform(
                        post("/api/v1/produits")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(produitJson("Souris", 19.90, 20)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/v1/produits/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Souris"));
    }

    @Test
    @DisplayName("GET - Produit inexistant")
    void getProduitInexistant() throws Exception {
        mockMvc.perform(get("/api/v1/produits/{id}", 999999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT - Modifier un produit")
    void modifierProduit() throws Exception {
        String response = mockMvc.perform(
                        post("/api/v1/produits")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(produitJson("Clavier", 50.0, 10)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(put("/api/v1/produits/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produitJson("Clavier sans fil", 70.0, 15)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Clavier sans fil"));
    }

    @Test
    @DisplayName("PUT - Produit inexistant")
    void modifierProduitInexistant() throws Exception {
        mockMvc.perform(put("/api/v1/produits/{id}", 999999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(produitJson("Clavier", 50.0, 10)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE - Supprimer un produit")
    void supprimerProduit() throws Exception {
        String response = mockMvc.perform(
                        post("/api/v1/produits")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(produitJson("Casque", 100.0, 5)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/v1/produits/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/produits/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET - Recherche par catégorie")
    void rechercherParCategorie() throws Exception {
        mockMvc.perform(get("/api/v1/produits/search")
                        .param("categorie", "Informatique"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET - Recherche par nom")
    void rechercherParNom() throws Exception {
        mockMvc.perform(get("/api/v1/produits/search")
                        .param("nom", "Clavier"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET - Recherche sans filtre")
    void rechercherSansFiltre() throws Exception {
        mockMvc.perform(get("/api/v1/produits/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET - Produits en rupture de stock")
    void getProduitsEnRuptureDeStock() throws Exception {
        mockMvc.perform(get("/api/v1/produits/rupture-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
