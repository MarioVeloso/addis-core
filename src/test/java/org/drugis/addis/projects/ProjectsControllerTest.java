package org.drugis.addis.projects;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.drugis.addis.TestUtils;
import org.drugis.addis.config.TestConfig;
import org.drugis.addis.projects.repository.ProjectRepository;
import org.drugis.addis.security.Account;
import org.drugis.addis.security.repository.AccountRepository;
import org.drugis.addis.trialverse.Trialverse;
import org.drugis.addis.util.WebConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by daan on 2/6/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {TestConfig.class})
@WebAppConfiguration
public class ProjectsControllerTest {

  private MockMvc mockMvc;

  @Inject
  private AccountRepository accountRepository;

  @Inject
  private ProjectRepository projectRepository;

  @Autowired
  private WebApplicationContext webApplicationContext;

  private Principal user;

  private Account john = new Account(1, "a", "john", "lennon"),
          paul = new Account(2, "a", "paul", "mc cartney"),
          gert = new Account(3, "gert", "Gert", "van Valkenhoef");


  @Before
  public void setUp() {
    reset(accountRepository);
    reset(projectRepository);
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    user = mock(Principal.class);
    when(user.getName()).thenReturn("gert");
    when(accountRepository.findAccountByUsername("gert")).thenReturn(gert);
  }


  @After
  public void tearDown() {
    verifyNoMoreInteractions(accountRepository, projectRepository);
  }

  @Test
  public void testQueryEmptyProjects() throws Exception {
    when(projectRepository.query()).thenReturn(Collections.<Project>emptyList());

    mockMvc.perform(get("/projects").principal(user))
      .andExpect(status().isOk())
      .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$", hasSize(0)));

    verify(projectRepository).query();
    verify(accountRepository).findAccountByUsername("gert");
  }

  @Test
  public void testQueryProjects() throws Exception {
    Project project = new Project(1 ,john, "name", "desc", new Trialverse("ns1"));
    Project project2 = new Project(2 ,paul, "otherName", "other description", new Trialverse("ns2"));
    ArrayList projects = new ArrayList();
    projects.add(project);
    projects.add(project2);
    when(projectRepository.query()).thenReturn(projects);

    mockMvc.perform(get("/projects").principal(user))
      .andExpect(status().isOk())
      .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
      .andExpect(jsonPath("$", hasSize(projects.size())))
      .andExpect(jsonPath("$[0].id", is(project.getId())))
      .andExpect(jsonPath("$[0].owner.id", is(project.getOwner().getId())))
      .andExpect(jsonPath("$[0].name", is(project.getName())))
      .andExpect(jsonPath("$[0].description", is(project.getDescription())))
      .andExpect(jsonPath("$[0].trialverse.name", is(project.getTrialverse().getName())));

    verify(projectRepository).query();
    verify(accountRepository).findAccountByUsername("gert");
  }

  @Test
  public void testQueryProjectsWithQueryString() throws Exception {
    Project project = new Project(2, paul, "test2", "desc", new Trialverse("ns1"));
    ArrayList projects = new ArrayList();
    projects.add(project);
    when(projectRepository.queryByOwnerId(paul.getId())).thenReturn(projects);

    mockMvc.perform(get("/projects?owner=2").principal(user))
            .andExpect(status().isOk())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$", hasSize(projects.size())))
            .andExpect(jsonPath("$[0].owner.id", is(project.getOwner().getId())));

    verify(projectRepository).queryByOwnerId(paul.getId());
    verify(accountRepository).findAccountByUsername("gert");
  }

  @Test
  public void testCreateProject() throws Exception {
    Project project = new Project(1, gert, "testname", "testdescription", new Trialverse("testnamespace"));
    String jsonContent = TestUtils.createJson(project);
    when(projectRepository.create(project.getOwner(), project.getName(), project.getDescription(), project.getTrialverse())).thenReturn(project);
    mockMvc.perform(post("/projects").principal(user).content(jsonContent).contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(WebConstants.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.name", is("testname")));
    verify(accountRepository).findAccountByUsername(gert.getUsername());
    verify(projectRepository).create(project.getOwner(), project.getName(), project.getDescription(), project.getTrialverse());
  }

}

