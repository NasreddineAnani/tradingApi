package ca.ulaval.glo4002.stocks.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.ulaval.glo4002.stocks.domain.companies.Company;
import ca.ulaval.glo4002.stocks.repositories.CompanyRepository;

@RepositoryRestController
@RequestMapping("/companies")
public class CompanyController {

  private final CompanyRepository repository;

  @Autowired
  public CompanyController(CompanyRepository repository) {
    this.repository = repository;
  }

  @GetMapping("")
  public @ResponseBody List<Company> getCompanies() {
    return repository.findAll();
  }
}
