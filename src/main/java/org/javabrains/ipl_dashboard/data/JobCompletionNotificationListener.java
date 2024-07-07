package org.javabrains.ipl_dashboard.data;

import org.javabrains.ipl_dashboard.model.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	private final JdbcTemplate jdbcTemplate;

	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Time to verify the results");

			jdbcTemplate.query(
					"SELECT id, city, date, player_of_match, venue, team1, team2, toss_winner, toss_decision, match_winner, result, result_margin, umpire1, umpire2 FROM match",
					new DataClassRowMapper<>(Match.class))
					.forEach(match -> log.info("Found <{{}}> in the database.", match));
					//.forEach(match -> System.out.println(match));
		}
	}
}