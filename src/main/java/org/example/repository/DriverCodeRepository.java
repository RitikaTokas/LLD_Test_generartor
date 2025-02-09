package org.example.repository;

import org.example.models.DriverCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverCodeRepository extends MongoRepository<DriverCode, String> {
}
