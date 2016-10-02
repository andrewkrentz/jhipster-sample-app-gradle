package io.github.jhipster.sample.web.rest;

import io.github.jhipster.sample.JhipsterGradleSampleApplicationApp;
import io.github.jhipster.sample.domain.BankAccount;
import io.github.jhipster.sample.domain.User;
import io.github.jhipster.sample.repository.BankAccountRepository;
import io.github.jhipster.sample.repository.UserRepository;
import io.github.jhipster.sample.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * With the deprecated annotations the unit test now passes.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JhipsterGradleSampleApplicationApp.class)
@WebAppConfiguration
@IntegrationTest
public class WorkingBankAccountResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final BigDecimal DEFAULT_BALANCE = new BigDecimal(1);
    private static final BigDecimal UPDATED_BALANCE = new BigDecimal(2);

    @Inject
    private BankAccountRepository bankAccountRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    @Inject
    private UserRepository userRepository;

    @Mock
    private UserService mockUserService;

    private MockMvc restBankAccountMockMvc;

    private BankAccount bankAccount;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BankAccountResource bankAccountResource = new BankAccountResource();
        ReflectionTestUtils.setField(bankAccountResource, "bankAccountRepository", bankAccountRepository);
        ReflectionTestUtils.setField(bankAccountResource, "userService", mockUserService);
        this.restBankAccountMockMvc = MockMvcBuilders.standaloneSetup(bankAccountResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BankAccount createEntity(EntityManager em) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setName(DEFAULT_NAME);
        bankAccount.setBalance(DEFAULT_BALANCE);
        return bankAccount;
    }

    @Before
    public void initTest() {
        bankAccount = createEntity(em);
    }

    @Test
    @Transactional
    public void createBankAccount() throws Exception {

        // Get any user to test the integration test mocked service.
        User user = userRepository.findAll().get(0);
        when(mockUserService.getUserWithAuthorities()).thenReturn(user);

        int databaseSizeBeforeCreate = bankAccountRepository.findAll().size();

        // Create the BankAccount

        restBankAccountMockMvc.perform(post("/api/bank-accounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(bankAccount)))
            .andExpect(status().isCreated());

        // Validate the BankAccount in the database
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        assertThat(bankAccounts).hasSize(databaseSizeBeforeCreate + 1);
        BankAccount testBankAccount = bankAccounts.get(bankAccounts.size() - 1);
        assertThat(testBankAccount.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBankAccount.getBalance()).isEqualTo(DEFAULT_BALANCE);
    }
}
