package org.javabrains.ipl_dashboard.data;

import javax.sql.DataSource;

import org.javabrains.ipl_dashboard.model.Match;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

	private final String[] HEADERS = new String[] { "id", "season", "city", "date", "match_type", "player_of_match",
			"venue", "team1", "team2", "toss_winner", "toss_decision", "winner", "result", "result_margin",
			"target_runs", "target_overs", "super_over", "method", "umpire1", "umpire2" };

	@Bean
	public FlatFileItemReader<MatchInput> reader() {
		return new FlatFileItemReaderBuilder<MatchInput>()
				.name("MatchItemReader")
				.resource(new ClassPathResource("match-data.csv"))
				.delimited()
				.names(HEADERS)
				.targetType(MatchInput.class)
				.linesToSkip(1) //to skip header
				.build();
	}

	@Bean
	public MatchDataProcessor processor() {
		return new MatchDataProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Match> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Match>()
				.sql("INSERT INTO match (id, city, date, player_of_match, venue, team1, team2, toss_winner, toss_decision, match_winner, result, result_margin, umpire1, umpire2) "
						+ "VALUES (:id, :city, :date, :playerOfMatch, :venue, :team1, :team2, :tossWinner, :tossDecision, :matchWinner, :result, :resultMargin, :umpire1, :umpire2)")
				.dataSource(dataSource)
				.beanMapped()
				.build();
	}

	@Bean
	public Job importUserJob(JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener) {
		return new JobBuilder("importUserJob", jobRepository)
				.listener(listener)
				.start(step1)
				.build();
	}

	@Bean
	public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
			 JdbcBatchItemWriter<Match> writer) {
		return new StepBuilder("step1", jobRepository)
				.<MatchInput, Match>chunk(3, transactionManager)
				.reader(reader()) // beans can be used, as defined in same class instead of method arguments
				.processor(processor()) // beans can be used, as defined in same class instead of method arguments
				.writer(writer)
				.build();
	}
	
//	@Bean
//	public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
//			FlatFileItemReader<MatchInput> reader, MatchDataProcessor processor, JdbcBatchItemWriter<Match> writer) {
//		return new StepBuilder("step1", jobRepository)
//				.<MatchInput, Match>chunk(3, transactionManager)
//				.reader(reader)
//				.processor(processor)
//				.writer(writer)
//				// .faultTolerant() // allowing spring batch to skip line
//				// .skipLimit(1000) // skip line limit
//				// .skip(CustomException.class) // skip lines when this exception is thrown
//				.build();
//	}

	@Bean
	public PlatformTransactionManager transactionManager(DataSource dataSource) {
	    return new DataSourceTransactionManager(dataSource);
	}
}
