package com.tomar.app.web.rest;

import com.tomar.app.AdobeLatLangApp;
import com.tomar.app.domain.Entry;
import com.tomar.app.repository.EntryRepository;
import com.tomar.app.service.EntryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.net.URLEncoder;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the EntryResource REST controller.
 *
 * @see EntryResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AdobeLatLangApp.class)
@WebAppConfiguration
@IntegrationTest
public class EntryResourceIntTest {

    private static final String DEFAULT_ADDRESS = "AAAAA";
    private static final String UPDATED_ADDRESS = "BBBBB";
    private static final String DEFAULT_CITY = "AAAAA";
    private static final String UPDATED_CITY = "BBBBB";
    private static final String DEFAULT_COUNTRY = "AAAAA";
    private static final String UPDATED_COUNTRY = "BBBBB";
    private static final String DEFAULT_POSTAL_CODE = "AAAAA";
    private static final String UPDATED_POSTAL_CODE = "BBBBB";
    private static final String DEFAULT_STATE_PROVINCE = "AAAAA";
    private static final String UPDATED_STATE_PROVINCE = "BBBBB";
    private static final String DEFAULT_COUNTRY_1 = "AAAAA";
    private static final String UPDATED_COUNTRY_1 = "BBBBB";
    private static final String DEFAULT_GOOGLE_VERIFIED_ADDRESS = "AAAAA";
    private static final String UPDATED_GOOGLE_VERIFIED_ADDRESS = "BBBBB";
    private static final String DEFAULT_LATITUDE = "AAAAA";
    private static final String UPDATED_LATITUDE = "BBBBB";
    private static final String DEFAULT_LONGITUDE = "AAAAA";
    private static final String UPDATED_LONGITUDE = "BBBBB";

    @Inject
    private EntryRepository entryRepository;

    @Inject
    private EntryService entryService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restEntryMockMvc;

    private Entry entry;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        EntryResource entryResource = new EntryResource();
        ReflectionTestUtils.setField(entryResource, "entryService", entryService);
        this.restEntryMockMvc = MockMvcBuilders.standaloneSetup(entryResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        entry = new Entry();
        entry.setAddress(DEFAULT_ADDRESS);
        entry.setCity(DEFAULT_CITY);
        entry.setCountry(DEFAULT_COUNTRY);
        entry.setPostal_code(DEFAULT_POSTAL_CODE);
        entry.setState_province(DEFAULT_STATE_PROVINCE);
        entry.setCountry1(DEFAULT_COUNTRY_1);
        entry.setGoogle_verified_address(DEFAULT_GOOGLE_VERIFIED_ADDRESS);
        entry.setLatitude(DEFAULT_LATITUDE);
        entry.setLongitude(DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    public void createEntry() throws Exception {
        int databaseSizeBeforeCreate = entryRepository.findAll().size();

        // Create the Entry

        restEntryMockMvc.perform(post("/api/entries")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(entry)))
                .andExpect(status().isCreated());

        // Validate the Entry in the database
        List<Entry> entries = entryRepository.findAll();
        assertThat(entries).hasSize(databaseSizeBeforeCreate + 1);
        Entry testEntry = entries.get(entries.size() - 1);
        assertThat(testEntry.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testEntry.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testEntry.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testEntry.getPostal_code()).isEqualTo(DEFAULT_POSTAL_CODE);
        assertThat(testEntry.getState_province()).isEqualTo(DEFAULT_STATE_PROVINCE);
        assertThat(testEntry.getCountry1()).isEqualTo(DEFAULT_COUNTRY_1);
        assertThat(testEntry.getGoogle_verified_address()).isEqualTo(DEFAULT_GOOGLE_VERIFIED_ADDRESS);
        assertThat(testEntry.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testEntry.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
    }

    @Test
    @Transactional
    public void getAllEntries() throws Exception {
        // Initialize the database
        entryRepository.saveAndFlush(entry);

        // Get all the entries
        restEntryMockMvc.perform(get("/api/entries?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(entry.getId().intValue())))
                .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
                .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY.toString())))
                .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())))
                .andExpect(jsonPath("$.[*].postal_code").value(hasItem(DEFAULT_POSTAL_CODE.toString())))
                .andExpect(jsonPath("$.[*].state_province").value(hasItem(DEFAULT_STATE_PROVINCE.toString())))
                .andExpect(jsonPath("$.[*].country1").value(hasItem(DEFAULT_COUNTRY_1.toString())))
                .andExpect(jsonPath("$.[*].google_verified_address").value(hasItem(DEFAULT_GOOGLE_VERIFIED_ADDRESS.toString())))
                .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.toString())))
                .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.toString())));
    }
    
    //https://docs.google.com/spreadsheets/d/1O2HtFpwUhWsPHHcD61o8ZTLEWcjPPaGt-77N_b-jZU4/edit#gid=0
    @Test
    @Transactional
    public void getEntryUrl() throws Exception {
        // Initialize the database
      //  entryRepository.saveAndFlush(entry);

        // Get the entry
        restEntryMockMvc.perform(get("/api/urls/{id}", URLEncoder.encode("https://docs.google.com/spreadsheets/d/1DbCq0m9jS4H9bCu24anbPG-i7jBqD_V5u6YjLfyuRl4/edit#gid=0", "UTF-8")))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
      }
    
    @Test
    @Transactional
    public void getEntryUrl1() throws Exception {
        // Initialize the database
      //  entryRepository.saveAndFlush(entry);

        // Get the entry
        restEntryMockMvc.perform(post("/api/upload"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
      }


    @Test
    @Transactional
    public void getEntry() throws Exception {
        // Initialize the database
        entryRepository.saveAndFlush(entry);

        // Get the entry
        restEntryMockMvc.perform(get("/api/entries/{id}", entry.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(entry.getId().intValue()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS.toString()))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY.toString()))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY.toString()))
            .andExpect(jsonPath("$.postal_code").value(DEFAULT_POSTAL_CODE.toString()))
            .andExpect(jsonPath("$.state_province").value(DEFAULT_STATE_PROVINCE.toString()))
            .andExpect(jsonPath("$.country1").value(DEFAULT_COUNTRY_1.toString()))
            .andExpect(jsonPath("$.google_verified_address").value(DEFAULT_GOOGLE_VERIFIED_ADDRESS.toString()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.toString()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingEntry() throws Exception {
        // Get the entry
        restEntryMockMvc.perform(get("/api/entries/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEntry() throws Exception {
        // Initialize the database
        entryService.save(entry);

        int databaseSizeBeforeUpdate = entryRepository.findAll().size();

        // Update the entry
        Entry updatedEntry = new Entry();
        updatedEntry.setId(entry.getId());
        updatedEntry.setAddress(UPDATED_ADDRESS);
        updatedEntry.setCity(UPDATED_CITY);
        updatedEntry.setCountry(UPDATED_COUNTRY);
        updatedEntry.setPostal_code(UPDATED_POSTAL_CODE);
        updatedEntry.setState_province(UPDATED_STATE_PROVINCE);
        updatedEntry.setCountry1(UPDATED_COUNTRY_1);
        updatedEntry.setGoogle_verified_address(UPDATED_GOOGLE_VERIFIED_ADDRESS);
        updatedEntry.setLatitude(UPDATED_LATITUDE);
        updatedEntry.setLongitude(UPDATED_LONGITUDE);

        restEntryMockMvc.perform(put("/api/entries")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedEntry)))
                .andExpect(status().isOk());

        // Validate the Entry in the database
        List<Entry> entries = entryRepository.findAll();
        assertThat(entries).hasSize(databaseSizeBeforeUpdate);
        Entry testEntry = entries.get(entries.size() - 1);
        assertThat(testEntry.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testEntry.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testEntry.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testEntry.getPostal_code()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testEntry.getState_province()).isEqualTo(UPDATED_STATE_PROVINCE);
        assertThat(testEntry.getCountry1()).isEqualTo(UPDATED_COUNTRY_1);
        assertThat(testEntry.getGoogle_verified_address()).isEqualTo(UPDATED_GOOGLE_VERIFIED_ADDRESS);
        assertThat(testEntry.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testEntry.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    public void deleteEntry() throws Exception {
        // Initialize the database
        entryService.save(entry);

        int databaseSizeBeforeDelete = entryRepository.findAll().size();

        // Get the entry
        restEntryMockMvc.perform(delete("/api/entries/{id}", entry.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Entry> entries = entryRepository.findAll();
        assertThat(entries).hasSize(databaseSizeBeforeDelete - 1);
    }
}
