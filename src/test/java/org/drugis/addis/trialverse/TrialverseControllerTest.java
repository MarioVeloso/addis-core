package org.drugis.addis.trialverse;


import org.drugis.addis.config.TestConfig;
import org.drugis.addis.security.Account;
import org.drugis.addis.security.repository.AccountRepository;
import org.drugis.addis.trialverse.model.Namespace;
import org.drugis.addis.trialverse.model.SemanticIntervention;
import org.drugis.addis.trialverse.model.SemanticOutcome;
import org.drugis.addis.trialverse.model.Study;
import org.drugis.addis.trialverse.repository.TrialverseRepository;
import org.drugis.addis.trialverse.service.TriplestoreService;
import org.drugis.addis.util.WebConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by connor on 2/12/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@WebAppConfiguration
public class TrialverseControllerTest {

  private MockMvc mockMvc;

  @Inject
  private AccountRepository accountRepository;

  @Inject
  private WebApplicationContext webApplicationContext;

  @Inject
  private TrialverseRepository trialverseRepository;

  @Inject
  private TriplestoreService triplestoreService;

  private Principal user;

  private Account gert = new Account(3, "gert", "Gert", "van Valkenhoef");

  @Before
  public void setUp() {
    reset(accountRepository);
    reset(trialverseRepository);
    reset(triplestoreService);
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    user = mock(Principal.class);
    when(user.getName()).thenReturn("gert");
    when(accountRepository.findAccountByUsername("gert")).thenReturn(gert);
  }

  @Test
  public void testGetNamespaces() throws Exception {
    Namespace namespace1 = new Namespace(1L, "a", "descra");
    Namespace namespace2 = new Namespace(2L, "b", "descrb");
    Collection<Namespace> namespaceCollection = Arrays.asList(namespace1, namespace2);
    when(trialverseRepository.query()).thenReturn(namespaceCollection);
    mockMvc.perform(get("/namespaces").principal(user))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name", is("a")));
    verify(trialverseRepository).query();
  }

  @Test
  public void testGetNamespaceById() throws Exception {
    Namespace namespace1 = new Namespace(1L, "a", "descrea");
    when(trialverseRepository.get(1L)).thenReturn(namespace1);
    mockMvc.perform(get("/namespaces/1").principal(user))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.name", is("a")));
    verify(trialverseRepository).get(1L);
  }

  @Test
  public void testQuerySemanticOutcomes() throws Exception {
    Long namespaceId = 1L;
    SemanticOutcome testOutCome = new SemanticOutcome("http://test/com", "test label");
    when(triplestoreService.getOutcomes(namespaceId)).thenReturn(Arrays.asList(testOutCome));
    mockMvc.perform(get("/namespaces/" + namespaceId + "/outcomes").principal(user))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$[0].uri", is(testOutCome.getUri())));
    verify(triplestoreService).getOutcomes(namespaceId);
  }

  @Test
  public void testQuerySemanticInterventions() throws Exception {
    Long namespaceId = 1L;
    SemanticIntervention testIntervention = new SemanticIntervention("http://test/com", "test label");
    when(triplestoreService.getInterventions(namespaceId)).thenReturn(Arrays.asList(testIntervention));
    mockMvc.perform(get("/namespaces/" + namespaceId + "/interventions").principal(user))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$[0].uri", is(testIntervention.getUri())));
    verify(triplestoreService).getInterventions(namespaceId);
  }

  @Test
  public void testQuerySemanticStudies() throws Exception {
    Long namespaceId = 1L;
    Study study = new Study(1L, "name", "this is a title");
    when(trialverseRepository.queryStudies(namespaceId)).thenReturn(Arrays.asList(study));
    mockMvc.perform(get("/namespaces/" + namespaceId + "/studies").principal(user))
      .andExpect(status().isOk())
      .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$[0].id", is(1)))
      .andExpect(jsonPath("$[0].name", is(study.getName())))
      .andExpect(jsonPath("$[0].title", is(study.getTitle())));
    verify(trialverseRepository).queryStudies(namespaceId);
  }

  @Test
  public void testUnauthorisedGetSemanticOutcomesFails() throws Exception {
    Principal haxor = mock(Principal.class);
    when(haxor.getName()).thenReturn("who?");
    mockMvc.perform(get("/namespaces/1/outcomes").principal(haxor))
            .andExpect(redirectedUrl("/error/403"));
  }

}
