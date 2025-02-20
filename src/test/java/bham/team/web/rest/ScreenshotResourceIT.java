package bham.team.web.rest;

import static bham.team.domain.ScreenshotAsserts.*;
import static bham.team.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import bham.team.IntegrationTest;
import bham.team.domain.Screenshot;
import bham.team.domain.enumeration.Milestone;
import bham.team.repository.ScreenshotRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ScreenshotResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ScreenshotResourceIT {

    private static final byte[] DEFAULT_PIC = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PIC = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PIC_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PIC_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_CAPTION = "AAAAAAAAAA";
    private static final String UPDATED_CAPTION = "BBBBBBBBBB";

    private static final Milestone DEFAULT_MILESTONE = Milestone.M1;
    private static final Milestone UPDATED_MILESTONE = Milestone.M2;

    private static final String ENTITY_API_URL = "/api/screenshots";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ScreenshotRepository screenshotRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restScreenshotMockMvc;

    private Screenshot screenshot;

    private Screenshot insertedScreenshot;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Screenshot createEntity() {
        return new Screenshot()
            .pic(DEFAULT_PIC)
            .picContentType(DEFAULT_PIC_CONTENT_TYPE)
            .caption(DEFAULT_CAPTION)
            .milestone(DEFAULT_MILESTONE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Screenshot createUpdatedEntity() {
        return new Screenshot()
            .pic(UPDATED_PIC)
            .picContentType(UPDATED_PIC_CONTENT_TYPE)
            .caption(UPDATED_CAPTION)
            .milestone(UPDATED_MILESTONE);
    }

    @BeforeEach
    public void initTest() {
        screenshot = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedScreenshot != null) {
            screenshotRepository.delete(insertedScreenshot);
            insertedScreenshot = null;
        }
    }

    @Test
    @Transactional
    void createScreenshot() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Screenshot
        var returnedScreenshot = om.readValue(
            restScreenshotMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(screenshot)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Screenshot.class
        );

        // Validate the Screenshot in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertScreenshotUpdatableFieldsEquals(returnedScreenshot, getPersistedScreenshot(returnedScreenshot));

        insertedScreenshot = returnedScreenshot;
    }

    @Test
    @Transactional
    void createScreenshotWithExistingId() throws Exception {
        // Create the Screenshot with an existing ID
        screenshot.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restScreenshotMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(screenshot)))
            .andExpect(status().isBadRequest());

        // Validate the Screenshot in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllScreenshots() throws Exception {
        // Initialize the database
        insertedScreenshot = screenshotRepository.saveAndFlush(screenshot);

        // Get all the screenshotList
        restScreenshotMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(screenshot.getId().intValue())))
            .andExpect(jsonPath("$.[*].picContentType").value(hasItem(DEFAULT_PIC_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].pic").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_PIC))))
            .andExpect(jsonPath("$.[*].caption").value(hasItem(DEFAULT_CAPTION)))
            .andExpect(jsonPath("$.[*].milestone").value(hasItem(DEFAULT_MILESTONE.toString())));
    }

    @Test
    @Transactional
    void getScreenshot() throws Exception {
        // Initialize the database
        insertedScreenshot = screenshotRepository.saveAndFlush(screenshot);

        // Get the screenshot
        restScreenshotMockMvc
            .perform(get(ENTITY_API_URL_ID, screenshot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(screenshot.getId().intValue()))
            .andExpect(jsonPath("$.picContentType").value(DEFAULT_PIC_CONTENT_TYPE))
            .andExpect(jsonPath("$.pic").value(Base64.getEncoder().encodeToString(DEFAULT_PIC)))
            .andExpect(jsonPath("$.caption").value(DEFAULT_CAPTION))
            .andExpect(jsonPath("$.milestone").value(DEFAULT_MILESTONE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingScreenshot() throws Exception {
        // Get the screenshot
        restScreenshotMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingScreenshot() throws Exception {
        // Initialize the database
        insertedScreenshot = screenshotRepository.saveAndFlush(screenshot);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the screenshot
        Screenshot updatedScreenshot = screenshotRepository.findById(screenshot.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedScreenshot are not directly saved in db
        em.detach(updatedScreenshot);
        updatedScreenshot.pic(UPDATED_PIC).picContentType(UPDATED_PIC_CONTENT_TYPE).caption(UPDATED_CAPTION).milestone(UPDATED_MILESTONE);

        restScreenshotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedScreenshot.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedScreenshot))
            )
            .andExpect(status().isOk());

        // Validate the Screenshot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedScreenshotToMatchAllProperties(updatedScreenshot);
    }

    @Test
    @Transactional
    void putNonExistingScreenshot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        screenshot.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScreenshotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, screenshot.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(screenshot))
            )
            .andExpect(status().isBadRequest());

        // Validate the Screenshot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchScreenshot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        screenshot.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScreenshotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(screenshot))
            )
            .andExpect(status().isBadRequest());

        // Validate the Screenshot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamScreenshot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        screenshot.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScreenshotMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(screenshot)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Screenshot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateScreenshotWithPatch() throws Exception {
        // Initialize the database
        insertedScreenshot = screenshotRepository.saveAndFlush(screenshot);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the screenshot using partial update
        Screenshot partialUpdatedScreenshot = new Screenshot();
        partialUpdatedScreenshot.setId(screenshot.getId());

        partialUpdatedScreenshot.milestone(UPDATED_MILESTONE);

        restScreenshotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScreenshot.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScreenshot))
            )
            .andExpect(status().isOk());

        // Validate the Screenshot in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScreenshotUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedScreenshot, screenshot),
            getPersistedScreenshot(screenshot)
        );
    }

    @Test
    @Transactional
    void fullUpdateScreenshotWithPatch() throws Exception {
        // Initialize the database
        insertedScreenshot = screenshotRepository.saveAndFlush(screenshot);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the screenshot using partial update
        Screenshot partialUpdatedScreenshot = new Screenshot();
        partialUpdatedScreenshot.setId(screenshot.getId());

        partialUpdatedScreenshot
            .pic(UPDATED_PIC)
            .picContentType(UPDATED_PIC_CONTENT_TYPE)
            .caption(UPDATED_CAPTION)
            .milestone(UPDATED_MILESTONE);

        restScreenshotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScreenshot.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScreenshot))
            )
            .andExpect(status().isOk());

        // Validate the Screenshot in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScreenshotUpdatableFieldsEquals(partialUpdatedScreenshot, getPersistedScreenshot(partialUpdatedScreenshot));
    }

    @Test
    @Transactional
    void patchNonExistingScreenshot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        screenshot.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScreenshotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, screenshot.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(screenshot))
            )
            .andExpect(status().isBadRequest());

        // Validate the Screenshot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchScreenshot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        screenshot.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScreenshotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(screenshot))
            )
            .andExpect(status().isBadRequest());

        // Validate the Screenshot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamScreenshot() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        screenshot.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScreenshotMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(screenshot)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Screenshot in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteScreenshot() throws Exception {
        // Initialize the database
        insertedScreenshot = screenshotRepository.saveAndFlush(screenshot);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the screenshot
        restScreenshotMockMvc
            .perform(delete(ENTITY_API_URL_ID, screenshot.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return screenshotRepository.count();
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

    protected Screenshot getPersistedScreenshot(Screenshot screenshot) {
        return screenshotRepository.findById(screenshot.getId()).orElseThrow();
    }

    protected void assertPersistedScreenshotToMatchAllProperties(Screenshot expectedScreenshot) {
        assertScreenshotAllPropertiesEquals(expectedScreenshot, getPersistedScreenshot(expectedScreenshot));
    }

    protected void assertPersistedScreenshotToMatchUpdatableProperties(Screenshot expectedScreenshot) {
        assertScreenshotAllUpdatablePropertiesEquals(expectedScreenshot, getPersistedScreenshot(expectedScreenshot));
    }
}
