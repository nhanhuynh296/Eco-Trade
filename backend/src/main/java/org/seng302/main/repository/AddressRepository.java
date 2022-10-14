package org.seng302.main.repository;

import org.seng302.main.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public
interface AddressRepository extends JpaRepository<Address, Long> {
}
