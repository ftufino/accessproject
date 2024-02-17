package it.dthink.access.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Test,String> {
    
}
