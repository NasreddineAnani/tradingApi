package ca.ulaval.glo4002.stocks.repositories;

import java.util.List;

import org.springframework.data.repository.Repository;

import ca.ulaval.glo4002.stocks.domain.companies.Company;
import org.springframework.data.rest.core.annotation.RestResource;

public interface CompanyRepository extends Repository<Company, Integer> {
  @RestResource(exported = false)
  Company findOne(Integer id);

  @RestResource(exported = false)
  List<Company> findAll();
}
