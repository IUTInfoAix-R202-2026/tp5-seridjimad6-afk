package fr.univ_amu.iut.exercice7;

import static org.assertj.core.api.Assertions.assertThat;

import fr.univ_amu.iut.exercice4.Site;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

/**
 * Test du capstone, côté vue (TestFX) : on vérifie que le tableau affiche les sites persistés et
 * qu'ajouter un site via le formulaire ajoute une ligne. L'application est câblée par Guice sur une
 * base SQLite temporaire (cf. {@link PersistanceModule}).
 */
@ExtendWith(ApplicationExtension.class)
class SitesControllerTest {

  @Start
  void start(Stage stage) throws Exception {
    stage.setScene(null);
    new SitesApp().start(stage);
  }

  @SuppressWarnings("unchecked")
  private TableView<Site> table(FxRobot robot) {
    return robot.lookup("#tableSites").queryAs(TableView.class);
  }

  @Test
  void le_tableau_affiche_les_sites_persistes(FxRobot robot) {
    assertThat(table(robot).getItems())
        .as("la base de démonstration contient au moins le site du fil rouge")
        .isNotEmpty();
  }

  @Test
  void ajouter_un_site_via_le_formulaire_ajoute_une_ligne(FxRobot robot) {
    int avant = table(robot).getItems().size();

    TextField numero = robot.lookup("#champNumero").queryAs(TextField.class);
    TextField nom = robot.lookup("#champNom").queryAs(TextField.class);
    @SuppressWarnings("unchecked")
    ChoiceBox<String> protocole = robot.lookup("#choiceProtocole").queryAs(ChoiceBox.class);
    Button ajouter = robot.lookup("Ajouter").queryButton();

    robot.interact(
        () -> {
          numero.setText("752204");
          nom.setText("ZAC Nord");
          protocole.setValue("PointFixeStandard");
        });
    robot.interact(ajouter::fire);

    assertThat(table(robot).getItems()).hasSize(avant + 1);
  }

  @Test
  void les_colonnes_affichent_les_donnees_du_site(FxRobot robot) {
    TableView<Site> t = table(robot);
    Site premier = t.getItems().get(0);

    assertThat(t.getColumns().get(0).getCellData(0))
        .as("colonne Carré")
        .isEqualTo(premier.numeroCarre());
    assertThat(t.getColumns().get(1).getCellData(0))
        .as("colonne Nom")
        .isEqualTo(premier.nomConvivial());
    assertThat(t.getColumns().get(2).getCellData(0))
        .as("colonne Protocole")
        .isEqualTo(premier.protocole());
  }

  @Test
  void le_resume_affiche_le_nombre_de_sites(FxRobot robot) {
    Label resume = robot.lookup("#labelResume").queryAs(Label.class);
    assertThat(resume.getText())
        .as("labelResume doit être lié au ViewModel")
        .contains("site(s) suivi(s)");
  }

  @Test
  void la_liste_des_protocoles_propose_les_deux_valeurs(FxRobot robot) {
    @SuppressWarnings("unchecked")
    ChoiceBox<String> protocole = robot.lookup("#choiceProtocole").queryAs(ChoiceBox.class);
    assertThat(protocole.getItems()).containsExactly("PointFixeStandard", "PointFixeRecherche");
  }

  @Test
  void le_bouton_supprimer_est_desactive_sans_selection_puis_actif(FxRobot robot) {
    Button supprimer = robot.lookup("#boutonSupprimer").queryAs(Button.class);
    assertThat(supprimer.isDisable()).as("désactivé sans sélection").isTrue();

    robot.interact(() -> table(robot).getSelectionModel().selectFirst());

    assertThat(supprimer.isDisable()).as("actif après sélection").isFalse();
  }
}
