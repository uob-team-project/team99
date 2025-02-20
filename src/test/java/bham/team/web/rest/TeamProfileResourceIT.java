package bham.team.web.rest;

import static bham.team.domain.TeamProfileAsserts.*;
import static bham.team.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import bham.team.IntegrationTest;
import bham.team.domain.TeamProfile;
import bham.team.repository.TeamProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TeamProfileResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TeamProfileResourceIT {

    private static final Integer DEFAULT_TEAM_ID = 0;
    private static final Integer UPDATED_TEAM_ID = 1;

    private static final String DEFAULT_APP_LINK = "AAAAAAAAAA";
    private static final String UPDATED_APP_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_APP_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_APP_DESCRIPTION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_LOGO_PIC = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_LOGO_PIC = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_LOGO_PIC_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_LOGO_PIC_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_NICK_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NICK_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SLOGAN = "AAAAAAAAAA";
    private static final String UPDATED_SLOGAN = "BBBBBBBBBB";

    private static final Integer DEFAULT_VOTES = 1;
    private static final Integer UPDATED_VOTES = 2;

    private static final String ENTITY_API_URL = "/api/team-profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TeamProfileRepository teamProfileRepository;

    @Mock
    private TeamProfileRepository teamProfileRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTeamProfileMockMvc;

    private TeamProfile teamProfile;

    private TeamProfile insertedTeamProfile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeamProfile createEntity() {
        return new TeamProfile()
            .teamID(DEFAULT_TEAM_ID)
            .appLink(DEFAULT_APP_LINK)
            .appDescription(DEFAULT_APP_DESCRIPTION)
            .logoPic(DEFAULT_LOGO_PIC)
            .logoPicContentType(DEFAULT_LOGO_PIC_CONTENT_TYPE)
            .nickName(DEFAULT_NICK_NAME)
            .slogan(DEFAULT_SLOGAN)
            .votes(DEFAULT_VOTES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TeamProfile createUpdatedEntity() {
        return new TeamProfile()
            .teamID(UPDATED_TEAM_ID)
            .appLink(UPDATED_APP_LINK)
            .appDescription(UPDATED_APP_DESCRIPTION)
            .logoPic(UPDATED_LOGO_PIC)
            .logoPicContentType(UPDATED_LOGO_PIC_CONTENT_TYPE)
            .nickName(UPDATED_NICK_NAME)
            .slogan(UPDATED_SLOGAN)
            .votes(UPDATED_VOTES);
    }

    @BeforeEach
    public void initTest() {
        teamProfile = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTeamProfile != null) {
            teamProfileRepository.delete(insertedTeamProfile);
            insertedTeamProfile = null;
        }
    }

    @Test
    @Transactional
    void createTeamProfile() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TeamProfile
        var returnedTeamProfile = om.readValue(
            restTeamProfileMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teamProfile)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TeamProfile.class
        );

        // Validate the TeamProfile in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertTeamProfileUpdatableFieldsEquals(returnedTeamProfile, getPersistedTeamProfile(returnedTeamProfile));

        insertedTeamProfile = returnedTeamProfile;
    }

    @Test
    @Transactional
    void createTeamProfileWithExistingId() throws Exception {
        // Create the TeamProfile with an existing ID
        teamProfile.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTeamProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teamProfile)))
            .andExpect(status().isBadRequest());

        // Validate the TeamProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTeamIDIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        teamProfile.setTeamID(null);

        // Create the TeamProfile, which fails.

        restTeamProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teamProfile)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTeamProfiles() throws Exception {
        // Initialize the database
        insertedTeamProfile = teamProfileRepository.saveAndFlush(teamProfile);

        // Get all the teamProfileList
        restTeamProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(teamProfile.getId().intValue())))
            .andExpect(jsonPath("$.[*].teamID").value(hasItem(DEFAULT_TEAM_ID)))
            .andExpect(jsonPath("$.[*].appLink").value(hasItem(DEFAULT_APP_LINK)))
            .andExpect(jsonPath("$.[*].appDescription").value(hasItem(DEFAULT_APP_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].logoPicContentType").value(hasItem(DEFAULT_LOGO_PIC_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].logoPic").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_LOGO_PIC))))
            .andExpect(jsonPath("$.[*].nickName").value(hasItem(DEFAULT_NICK_NAME)))
            .andExpect(jsonPath("$.[*].slogan").value(hasItem(DEFAULT_SLOGAN)))
            .andExpect(jsonPath("$.[*].votes").value(hasItem(DEFAULT_VOTES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTeamProfilesWithEagerRelationshipsIsEnabled() throws Exception {
        when(teamProfileRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTeamProfileMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(teamProfileRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTeamProfilesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(teamProfileRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTeamProfileMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(teamProfileRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTeamProfile() throws Exception {
        // Initialize the database
        insertedTeamProfile = teamProfileRepository.saveAndFlush(teamProfile);

        // Get the teamProfile
        restTeamProfileMockMvc
            .perform(get(ENTITY_API_URL_ID, teamProfile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(teamProfile.getId().intValue()))
            .andExpect(jsonPath("$.teamID").value(DEFAULT_TEAM_ID))
            .andExpect(jsonPath("$.appLink").value(DEFAULT_APP_LINK))
            .andExpect(jsonPath("$.appDescription").value(DEFAULT_APP_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.logoPicContentType").value(DEFAULT_LOGO_PIC_CONTENT_TYPE))
            .andExpect(jsonPath("$.logoPic").value(Base64.getEncoder().encodeToString(DEFAULT_LOGO_PIC)))
            .andExpect(jsonPath("$.nickName").value(DEFAULT_NICK_NAME))
            .andExpect(jsonPath("$.slogan").value(DEFAULT_SLOGAN))
            .andExpect(jsonPath("$.votes").value(DEFAULT_VOTES));
    }

    @Test
    @Transactional
    void getNonExistingTeamProfile() throws Exception {
        // Get the teamProfile
        restTeamProfileMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTeamProfile() throws Exception {
        // Initialize the database
        insertedTeamProfile = teamProfileRepository.saveAndFlush(teamProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teamProfile
        TeamProfile updatedTeamProfile = teamProfileRepository.findById(teamProfile.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTeamProfile are not directly saved in db
        em.detach(updatedTeamProfile);
        updatedTeamProfile
            .teamID(UPDATED_TEAM_ID)
            .appLink(UPDATED_APP_LINK)
            .appDescription(UPDATED_APP_DESCRIPTION)
            .logoPic(UPDATED_LOGO_PIC)
            .logoPicContentType(UPDATED_LOGO_PIC_CONTENT_TYPE)
            .nickName(UPDATED_NICK_NAME)
            .slogan(UPDATED_SLOGAN)
            .votes(UPDATED_VOTES);

        restTeamProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTeamProfile.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedTeamProfile))
            )
            .andExpect(status().isOk());

        // Validate the TeamProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTeamProfileToMatchAllProperties(updatedTeamProfile);
    }

    @Test
    @Transactional
    void putNonExistingTeamProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teamProfile.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, teamProfile.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(teamProfile))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTeamProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teamProfile.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(teamProfile))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTeamProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teamProfile.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamProfileMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(teamProfile)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TeamProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTeamProfileWithPatch() throws Exception {
        // Initialize the database
        insertedTeamProfile = teamProfileRepository.saveAndFlush(teamProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teamProfile using partial update
        TeamProfile partialUpdatedTeamProfile = new TeamProfile();
        partialUpdatedTeamProfile.setId(teamProfile.getId());

        partialUpdatedTeamProfile.appLink(UPDATED_APP_LINK).nickName(UPDATED_NICK_NAME).votes(UPDATED_VOTES);

        restTeamProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeamProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTeamProfile))
            )
            .andExpect(status().isOk());

        // Validate the TeamProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeamProfileUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTeamProfile, teamProfile),
            getPersistedTeamProfile(teamProfile)
        );
    }

    @Test
    @Transactional
    void fullUpdateTeamProfileWithPatch() throws Exception {
        // Initialize the database
        insertedTeamProfile = teamProfileRepository.saveAndFlush(teamProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the teamProfile using partial update
        TeamProfile partialUpdatedTeamProfile = new TeamProfile();
        partialUpdatedTeamProfile.setId(teamProfile.getId());

        partialUpdatedTeamProfile
            .teamID(UPDATED_TEAM_ID)
            .appLink(UPDATED_APP_LINK)
            .appDescription(UPDATED_APP_DESCRIPTION)
            .logoPic(UPDATED_LOGO_PIC)
            .logoPicContentType(UPDATED_LOGO_PIC_CONTENT_TYPE)
            .nickName(UPDATED_NICK_NAME)
            .slogan(UPDATED_SLOGAN)
            .votes(UPDATED_VOTES);

        restTeamProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTeamProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTeamProfile))
            )
            .andExpect(status().isOk());

        // Validate the TeamProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeamProfileUpdatableFieldsEquals(partialUpdatedTeamProfile, getPersistedTeamProfile(partialUpdatedTeamProfile));
    }

    @Test
    @Transactional
    void patchNonExistingTeamProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teamProfile.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTeamProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, teamProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(teamProfile))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTeamProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teamProfile.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(teamProfile))
            )
            .andExpect(status().isBadRequest());

        // Validate the TeamProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTeamProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        teamProfile.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTeamProfileMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(teamProfile)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TeamProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTeamProfile() throws Exception {
        // Initialize the database
        insertedTeamProfile = teamProfileRepository.saveAndFlush(teamProfile);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the teamProfile
        restTeamProfileMockMvc
            .perform(delete(ENTITY_API_URL_ID, teamProfile.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return teamProfileRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected TeamProfile getPersistedTeamProfile(TeamProfile teamProfile) {
        return teamProfileRepository.findById(teamProfile.getId()).orElseThrow();
    }

    protected void assertPersistedTeamProfileToMatchAllProperties(TeamProfile expectedTeamProfile) {
        assertTeamProfileAllPropertiesEquals(expectedTeamProfile, getPersistedTeamProfile(expectedTeamProfile));
    }

    protected void assertPersistedTeamProfileToMatchUpdatableProperties(TeamProfile expectedTeamProfile) {
        assertTeamProfileAllUpdatablePropertiesEquals(expectedTeamProfile, getPersistedTeamProfile(expectedTeamProfile));
    }
}
