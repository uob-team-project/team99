package bham.team.web.rest;

import bham.team.domain.Screenshot;
import bham.team.repository.ScreenshotRepository;
import bham.team.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link bham.team.domain.Screenshot}.
 */
@RestController
@RequestMapping("/api/screenshots")
@Transactional
public class ScreenshotResource {

    private static final Logger LOG = LoggerFactory.getLogger(ScreenshotResource.class);

    private static final String ENTITY_NAME = "screenshot";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ScreenshotRepository screenshotRepository;

    public ScreenshotResource(ScreenshotRepository screenshotRepository) {
        this.screenshotRepository = screenshotRepository;
    }

    /**
     * {@code POST  /screenshots} : Create a new screenshot.
     *
     * @param screenshot the screenshot to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new screenshot, or with status {@code 400 (Bad Request)} if the screenshot has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Screenshot> createScreenshot(@RequestBody Screenshot screenshot) throws URISyntaxException {
        LOG.debug("REST request to save Screenshot : {}", screenshot);
        if (screenshot.getId() != null) {
            throw new BadRequestAlertException("A new screenshot cannot already have an ID", ENTITY_NAME, "idexists");
        }
        screenshot = screenshotRepository.save(screenshot);
        return ResponseEntity.created(new URI("/api/screenshots/" + screenshot.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, screenshot.getId().toString()))
            .body(screenshot);
    }

    /**
     * {@code PUT  /screenshots/:id} : Updates an existing screenshot.
     *
     * @param id the id of the screenshot to save.
     * @param screenshot the screenshot to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated screenshot,
     * or with status {@code 400 (Bad Request)} if the screenshot is not valid,
     * or with status {@code 500 (Internal Server Error)} if the screenshot couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Screenshot> updateScreenshot(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Screenshot screenshot
    ) throws URISyntaxException {
        LOG.debug("REST request to update Screenshot : {}, {}", id, screenshot);
        if (screenshot.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, screenshot.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!screenshotRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        screenshot = screenshotRepository.save(screenshot);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, screenshot.getId().toString()))
            .body(screenshot);
    }

    /**
     * {@code PATCH  /screenshots/:id} : Partial updates given fields of an existing screenshot, field will ignore if it is null
     *
     * @param id the id of the screenshot to save.
     * @param screenshot the screenshot to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated screenshot,
     * or with status {@code 400 (Bad Request)} if the screenshot is not valid,
     * or with status {@code 404 (Not Found)} if the screenshot is not found,
     * or with status {@code 500 (Internal Server Error)} if the screenshot couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Screenshot> partialUpdateScreenshot(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Screenshot screenshot
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Screenshot partially : {}, {}", id, screenshot);
        if (screenshot.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, screenshot.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!screenshotRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Screenshot> result = screenshotRepository
            .findById(screenshot.getId())
            .map(existingScreenshot -> {
                if (screenshot.getPic() != null) {
                    existingScreenshot.setPic(screenshot.getPic());
                }
                if (screenshot.getPicContentType() != null) {
                    existingScreenshot.setPicContentType(screenshot.getPicContentType());
                }
                if (screenshot.getCaption() != null) {
                    existingScreenshot.setCaption(screenshot.getCaption());
                }
                if (screenshot.getMilestone() != null) {
                    existingScreenshot.setMilestone(screenshot.getMilestone());
                }

                return existingScreenshot;
            })
            .map(screenshotRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, screenshot.getId().toString())
        );
    }

    /**
     * {@code GET  /screenshots} : get all the screenshots.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of screenshots in body.
     */
    @GetMapping("")
    public List<Screenshot> getAllScreenshots() {
        LOG.debug("REST request to get all Screenshots");
        return screenshotRepository.findAll();
    }

    /**
     * {@code GET  /screenshots/:id} : get the "id" screenshot.
     *
     * @param id the id of the screenshot to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the screenshot, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Screenshot> getScreenshot(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Screenshot : {}", id);
        Optional<Screenshot> screenshot = screenshotRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(screenshot);
    }

    /**
     * {@code DELETE  /screenshots/:id} : delete the "id" screenshot.
     *
     * @param id the id of the screenshot to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScreenshot(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Screenshot : {}", id);
        screenshotRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
