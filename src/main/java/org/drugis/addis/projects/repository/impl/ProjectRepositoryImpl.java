package org.drugis.addis.projects.repository.impl;

import org.drugis.addis.exception.ResourceDoesNotExistException;
import org.drugis.addis.projects.Project;
import org.drugis.addis.projects.ProjectCommand;
import org.drugis.addis.projects.repository.ProjectRepository;
import org.drugis.addis.security.Account;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collection;

/**
 * Created by daan on 2/20/14.
 */
@Repository
public class ProjectRepositoryImpl implements ProjectRepository {

  @Qualifier("emAddisCore")
  @PersistenceContext(unitName = "addisCore")
  EntityManager em;

  @Override
  public Collection<Project> query() {
    return em.createQuery("from Project").getResultList();
  }

  @Override
  public Project get(Integer projectId) throws ResourceDoesNotExistException {
    Project project = em.find(Project.class, projectId);
    if (project == null) {
      throw new ResourceDoesNotExistException();
    }
    return project;
  }

  @Override
  public Collection<Project> queryByOwnerId(Integer ownerId) {
    TypedQuery<Project> query = em.createQuery("from Project p where p.owner.id = :ownerId", Project.class);
    query.setParameter("ownerId", ownerId);
    return query.getResultList();
  }

  @Override
  public Project create(Account user, ProjectCommand command) {
    Project project = new Project(user, command.getName(), command.getDescription(), command.getNamespaceUid(), command.getDatasetVersion());
    em.persist(project);
    return project;
  }

}
