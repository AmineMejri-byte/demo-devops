
package com.beedigital.beestore.service;

import com.beedigital.beestore.dto.ProduitRequestDTO;
import com.beedigital.beestore.dto.ProduitResponseDTO;
import com.beedigital.beestore.entity.Produit;
import com.beedigital.beestore.exception.ProduitNotFoundException;
import com.beedigital.beestore.repository.ProduitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProduitServiceImplTest {

    @Mock
    private ProduitRepository produitRepository;

    @InjectMocks
    private ProduitServiceImpl produitService;

    private ProduitRequestDTO creerDTO() {
        ProduitRequestDTO dto = new ProduitRequestDTO();
        dto.setNom("Clavier");
        dto.setDescription("Clavier mécanique");
        dto.setPrix(120.0);
        dto.setStock(5);
        dto.setCategorie("Informatique");
        dto.setActif(true);
        return dto;
    }

    private Produit creerProduit(Long id) {
        return Produit.builder()
                .id(id)
                .nom("Clavier")
                .description("Clavier mécanique")
                .prix(120.0)
                .stock(5)
                .categorie("Informatique")
                .actif(true)
                .build();
    }

    @Test
    void creerProduit_retourneProduitCree() {
        ProduitRequestDTO dto = creerDTO();
        Produit saved = creerProduit(1L);

        when(produitRepository.save(any(Produit.class))).thenReturn(saved);

        ProduitResponseDTO resultat = produitService.creerProduit(dto);

        assertThat(resultat.getId()).isEqualTo(1L);
        assertThat(resultat.getNom()).isEqualTo("Clavier");
        assertThat(resultat.getPrix()).isEqualTo(120.0);
        verify(produitRepository).save(any(Produit.class));
    }

    @Test
    void creerProduit_actifNull_utiliseTrue() {
        ProduitRequestDTO dto = creerDTO();
        dto.setActif(null);

        when(produitRepository.save(any(Produit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProduitResponseDTO resultat = produitService.creerProduit(dto);

        assertThat(resultat.getActif()).isTrue();
    }

    @Test
    void getTousProduits_retourneListe() {
        when(produitRepository.findAll())
                .thenReturn(List.of(creerProduit(1L), creerProduit(2L)));

        List<ProduitResponseDTO> resultat = produitService.getTousProduits();

        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).getNom()).isEqualTo("Clavier");
        verify(produitRepository).findAll();
    }

    @Test
    void getTousProduits_listeVide() {
        when(produitRepository.findAll()).thenReturn(List.of());

        List<ProduitResponseDTO> resultat = produitService.getTousProduits();

        assertThat(resultat).isEmpty();
    }

    @Test
    void getProduitById_produitExistant() {
        when(produitRepository.findById(1L))
                .thenReturn(Optional.of(creerProduit(1L)));

        ProduitResponseDTO resultat = produitService.getProduitById(1L);

        assertThat(resultat.getId()).isEqualTo(1L);
        assertThat(resultat.getNom()).isEqualTo("Clavier");
    }

    @Test
    void getProduitById_produitAbsent() {
        when(produitRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ProduitNotFoundException.class,
                () -> produitService.getProduitById(99L)
        );
    }

    @Test
    void modifierProduit_produitExistant() {
        Produit existant = creerProduit(1L);
        ProduitRequestDTO dto = creerDTO();
        dto.setNom("Souris");
        dto.setPrix(60.0);

        when(produitRepository.findById(1L)).thenReturn(Optional.of(existant));
        when(produitRepository.save(any(Produit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProduitResponseDTO resultat = produitService.modifierProduit(1L, dto);

        assertThat(resultat.getNom()).isEqualTo("Souris");
        assertThat(resultat.getPrix()).isEqualTo(60.0);
        verify(produitRepository).save(existant);
    }

    @Test
    void modifierProduit_actifNull_conserveValeurExistante() {
        Produit existant = creerProduit(1L);
        existant.setActif(false);

        ProduitRequestDTO dto = creerDTO();
        dto.setActif(null);

        when(produitRepository.findById(1L)).thenReturn(Optional.of(existant));
        when(produitRepository.save(any(Produit.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProduitResponseDTO resultat = produitService.modifierProduit(1L, dto);

        assertThat(resultat.getActif()).isFalse();
    }

    @Test
    void modifierProduit_produitAbsent() {
        when(produitRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ProduitNotFoundException.class,
                () -> produitService.modifierProduit(99L, creerDTO())
        );

        verify(produitRepository, never()).save(any(Produit.class));
    }

    @Test
    void supprimerProduit_produitExistant() {
        when(produitRepository.existsById(1L)).thenReturn(true);

        produitService.supprimerProduit(1L);

        verify(produitRepository).deleteById(1L);
    }

    @Test
    void supprimerProduit_produitAbsent() {
        when(produitRepository.existsById(99L)).thenReturn(false);

        assertThrows(
                ProduitNotFoundException.class,
                () -> produitService.supprimerProduit(99L)
        );

        verify(produitRepository, never()).deleteById(any());
    }

    @Test
    void rechercherParCategorie_retourneResultats() {
        when(produitRepository.findByCategorieIgnoreCase("Informatique"))
                .thenReturn(List.of(creerProduit(1L)));

        List<ProduitResponseDTO> resultat =
                produitService.rechercherParCategorie("Informatique");

        assertThat(resultat).hasSize(1);
        verify(produitRepository).findByCategorieIgnoreCase("Informatique");
    }

    @Test
    void rechercherParCategorie_aucunResultat() {
        when(produitRepository.findByCategorieIgnoreCase("Inconnue"))
                .thenReturn(List.of());

        assertThat(produitService.rechercherParCategorie("Inconnue")).isEmpty();
    }

    @Test
    void rechercherParNom_retourneResultats() {
        when(produitRepository.findByNomContainingIgnoreCase("clav"))
                .thenReturn(List.of(creerProduit(1L)));

        List<ProduitResponseDTO> resultat =
                produitService.rechercherParNom("clav");

        assertThat(resultat).hasSize(1);
        verify(produitRepository).findByNomContainingIgnoreCase("clav");
    }

    @Test
    void rechercherParNom_aucunResultat() {
        when(produitRepository.findByNomContainingIgnoreCase("inconnu"))
                .thenReturn(List.of());

        assertThat(produitService.rechercherParNom("inconnu")).isEmpty();
    }

    @Test
    void getProduitsEnRuptureDeStock_retourneResultats() {
        Produit produitRupture = creerProduit(1L);
        produitRupture.setStock(0);

        when(produitRepository.findByStock(0))
                .thenReturn(List.of(produitRupture));

        List<ProduitResponseDTO> resultat =
                produitService.getProduitsEnRuptureDeStock();

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getStock()).isZero();
        verify(produitRepository).findByStock(0);
    }

    @Test
    void getProduitsEnRuptureDeStock_aucunResultat() {
        when(produitRepository.findByStock(0)).thenReturn(List.of());

        assertThat(produitService.getProduitsEnRuptureDeStock()).isEmpty();
    }
}
