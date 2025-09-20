package com.chouket370.realtimefrauddetectionsystem.Repository;

import com.chouket370.realtimefrauddetectionsystem.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends MongoRepository<Customer,String> {
}
