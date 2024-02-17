package it.dthink.access.controller;

import java.beans.PropertyDescriptor;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

import it.dthink.access.model.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import it.dthink.access.model.Test;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
@Slf4j
public class TestController {

    @Autowired
    private TestRepository testRepository;

    @PersistenceContext
    EntityManager entityManager;

    @GetMapping(value = "/test", headers = "Accept=application/json")
    public List<Test> getAll() {
        return testRepository.findAll();
    }

    @GetMapping(value = "/test/{id}", headers = "Accept=application/json")
    public ResponseEntity<Test> getId(@PathVariable String id) throws NoSuchFieldException {
        Optional<Test> test = testRepository.findById(id);

        if (!test.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(test.get());
    }

    @PostMapping("/test")
    public ResponseEntity<Test> save(@RequestBody @Valid Test newTest) {

        Optional<Test> t = testRepository.findById(newTest.getId());
        if (t.isPresent()) {
            log.warn("ID {} is already present", t.get().getId());
            return ResponseEntity.status(409).build();
        }

        Test savedTest = testRepository.save(newTest);

        URI newTestLocation = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedTest.getId())
                .toUri();

        log.info("added: '" + savedTest);
        return ResponseEntity.created(newTestLocation).build();
    }

    @PutMapping("/test")
    public ResponseEntity<Object> update(@RequestBody @Valid Test updatedTest) {

        String id = updatedTest.getId();
        Optional<Test> oldTest = testRepository.findById(id);

        if (!oldTest.isPresent()) {
            log.info("id {} not found", id);
            return ResponseEntity.notFound().build();
        }

        merge(oldTest.get(), updatedTest);
        updatedTest.setId(id);

        testRepository.save(updatedTest);
        log.info("ID {} updated", id);

        return ResponseEntity.noContent().build();
    }

    private void merge(Test old, Test newTest) {
        
        BeanWrapper oldProperties = new BeanWrapperImpl(old),
                    newProperties = new BeanWrapperImpl(newTest);
        PropertyDescriptor[] pds = oldProperties.getPropertyDescriptors();
        for(PropertyDescriptor pd : pds) {
            String propertyName = pd.getName();
            if(propertyName.equalsIgnoreCase("class"))
                continue;
            Object oldPropertyValue = oldProperties.getPropertyValue(propertyName);
            Object newPropertyValue = newProperties.getPropertyValue(propertyName);

            if(newPropertyValue != null) {
                newProperties.setPropertyValue(propertyName, newPropertyValue);
            } else {
                newProperties.setPropertyValue(propertyName, oldPropertyValue);
            }
        }
    }
    
    @DeleteMapping("/test/{id}")
    public ResponseEntity<Test> deletePart(@PathVariable String id) {
        Optional<Test> partOptional = testRepository.findById(id);

        if (partOptional.isPresent()) {
            testRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            log.info("ID {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

}
