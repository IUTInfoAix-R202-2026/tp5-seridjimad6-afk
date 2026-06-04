package fr.univ_amu.iut.exercice7;

import com.google.inject.Inject;
import fr.univ_amu.iut.exercice4.Site;
import fr.univ_amu.iut.exercice4.SiteDao;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * ViewModel du capstone : la liste des sites VigieChiro, persistée en base.
 *
 * <p>C'est le point de jonction des TP4 et TP5 : un ViewModel MVVM (comme au TP4) dont la source de
 * données n'est plus en mémoire mais un {@link SiteDao} (TP5), <b>injecté par Guice</b>. La liste
 * observable est chargée depuis la base ; ajouter ou supprimer un site écrit en base ET met à jour
 * la liste, donc la {@code TableView} se rafraîchit toute seule.
 *
 * <p>Le ViewModel reste testable sans interface : on lui passe un {@link SiteDao} branché sur une
 * base de test (voir {@code SitesViewModelTest}).
 */
public class SitesViewModel {

  private final SiteDao dao;
  private final ObservableList<Site> sites = FXCollections.observableArrayList();
  private final StringProperty resume = new SimpleStringProperty();

  @Inject
  public SitesViewModel(SiteDao dao) {
    this.dao = dao;

    sites.setAll(dao.findAll());
    resume.bind(
        Bindings.createStringBinding(
            () -> sites.size() + " site(s) suivi(s)", Bindings.size(sites)));
  }

  public ObservableList<Site> sitesProperty() {
    return sites;
  }

  public ReadOnlyStringProperty resumeProperty() {
    return resume;
  }

  /** Persiste un nouveau site puis l'ajoute à la liste observable. */
  public void ajouterCommand(Site site) {
    dao.insert(site);
    sites.add(site);
  }

  /** Supprime un site de la base puis de la liste observable. */
  public void supprimerCommand(Site site) {
    dao.delete(site.numeroCarre());
    sites.remove(site);
  }
}
