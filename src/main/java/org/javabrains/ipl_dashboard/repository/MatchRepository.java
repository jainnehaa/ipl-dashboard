package org.javabrains.ipl_dashboard.repository;

import org.javabrains.ipl_dashboard.model.Match;
import org.springframework.data.repository.CrudRepository;

public interface MatchRepository extends CrudRepository<Match, Long>  {

}
