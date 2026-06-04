package fr.univ_amu.iut.exercice7;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.exercice2.BaseDeDonnees;
import fr.univ_amu.iut.exercice4.Site;
import fr.univ_amu.iut.exercice4.SiteDao;
import java.nio.file.Path;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test du capstone, côté ViewModel : la logique (chargement, ajout, suppression) se vérifie sans
 * interface graphique, sur une base SQLite temporaire. On contrôle aussi que les modifications sont
 * bien <b>persistées</b> (relues par un DAO indépendant).
 */
class SitesViewModelTest {

  @TempDir Path dossier;
  private DataSource source;
  private SitesViewModel vm;

  @BeforeEach
  void preparer() {
    source = BaseDeDonnees.surFichier(dossier.resolve("test.db").toString());
    BaseDeDonnees.initialiser(source);
    vm = new SitesViewModel(new SiteDao(source));
  }

  @Test
  void au_demarrage_les_sites_sont_charges_depuis_la_base() {
    assertThat(vm.sitesProperty()).extracting(Site::numeroCarre).containsExactly("640380");
  }

  @Test
  void le_resume_reflete_le_nombre_de_sites() {
    assertThat(vm.resumeProperty().get()).isEqualTo("1 site(s) suivi(s)");
  }

  @Test
  void ajouter_persiste_le_site_et_l_ajoute_a_la_liste() {
    Site nouveau = new Site("752204", "ZAC Nord", "PointFixeRecherche", null, "2026-05-01");

    vm.ajouterCommand(nouveau);

    assertThat(vm.sitesProperty()).contains(nouveau);
    // Persistance : un DAO indépendant doit voir le site en base.
    assertThat(new SiteDao(source).getByNumeroCarre("752204")).isPresent();
  }

  @Test
  void supprimer_retire_le_site_de_la_liste_et_de_la_base() {
    // On supprime un site sans données rattachées (le site seedé 640380 a un point d'écoute,
    // et l'intégrité référentielle empêcherait sa suppression).
    Site jetable = new Site("999999", "Site jetable", "PointFixeStandard", null, "2026-05-02");
    vm.ajouterCommand(jetable);

    vm.supprimerCommand(jetable);

    assertThat(vm.sitesProperty()).doesNotContain(jetable);
    assertThat(new SiteDao(source).getByNumeroCarre("999999")).isEmpty();
  }
}
