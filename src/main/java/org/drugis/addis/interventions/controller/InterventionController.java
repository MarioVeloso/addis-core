package org.drugis.addis.interventions.controller;

import org.drugis.addis.base.AbstractAddisCoreController;
import org.drugis.addis.exception.MethodNotAllowedException;
import org.drugis.addis.exception.ResourceDoesNotExistException;
import org.drugis.addis.interventions.Intervention;
import org.drugis.addis.interventions.InterventionCommand;
import org.drugis.addis.interventions.repository.InterventionRepository;
import org.drugis.addis.security.Account;
import org.drugis.addis.security.repository.AccountRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Collection;

/**
 * Created by daan on 3/5/14.
 */
@Controller
@Transactional("ptmAddisCore")
public class InterventionController extends AbstractAddisCoreController {

  @Inject
  private AccountRepository accountRepository;
  @Inject
  private InterventionRepository interventionRepository;


  @RequestMapping(value = "/projects/{projectId}/interventions", method = RequestMethod.GET)
  @ResponseBody
  public Collection<Intervention> query(Principal currentUser, @PathVariable Integer projectId) throws MethodNotAllowedException, ResourceDoesNotExistException {
    Account user = accountRepository.findAccountByUsername(currentUser.getName());
    if (user != null) {
      return interventionRepository.query(projectId);
    } else {
      throw new MethodNotAllowedException();
    }
  }

  @RequestMapping(value = "/projects/{projectId}/interventions/{interventionId}", method = RequestMethod.GET)
  @ResponseBody
  public Intervention get(Principal currentUser, @PathVariable Integer projectId, @PathVariable Integer interventionId) throws MethodNotAllowedException, ResourceDoesNotExistException {
    Account user = accountRepository.findAccountByUsername(currentUser.getName());
    if (user != null) {
      return interventionRepository.get(projectId, interventionId);
    } else {
      throw new MethodNotAllowedException();
    }
  }

  @RequestMapping(value = "/projects/{projectId}/interventions", method = RequestMethod.POST)
  @ResponseBody
  public Intervention create(HttpServletRequest request, HttpServletResponse response, Principal currentUser, @PathVariable Integer projectId, @RequestBody InterventionCommand interventionCommand) throws MethodNotAllowedException, ResourceDoesNotExistException {
    Account user = accountRepository.findAccountByUsername(currentUser.getName());
    if (user != null) {
      Intervention intervention = interventionRepository.create(user, interventionCommand);
      response.setStatus(HttpServletResponse.SC_CREATED);
      response.setHeader("Location", request.getRequestURL() + "/");
      return intervention;
    } else {
      throw new MethodNotAllowedException();
    }
  }

}
