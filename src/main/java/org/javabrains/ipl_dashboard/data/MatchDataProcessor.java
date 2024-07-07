package org.javabrains.ipl_dashboard.data;

import java.time.LocalDate;

import org.javabrains.ipl_dashboard.model.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class MatchDataProcessor implements ItemProcessor<MatchInput, Match> {

	private static final Logger log = LoggerFactory.getLogger(MatchDataProcessor.class);

	@Override
	public Match process(final MatchInput matchInput) throws Exception {

		Match match = new Match();

		try {
			match.setId(Long.parseLong(matchInput.getId().trim()));
		} catch(NumberFormatException nfe) {
			log.error("error in parsing for matchInput.getId() : " + matchInput.getId());
			throw nfe;
		}
		
		match.setCity(matchInput.getCity());
		match.setDate(LocalDate.parse(matchInput.getDate()));
		
		match.setPlayerOfMatch(matchInput.getPlayer_of_match());
		match.setVenue(matchInput.getVenue());
		
		
		String firstInningsTeam, secondInninigsTeam;
		
		if(matchInput.getToss_decision().equals("bat")) {
			firstInningsTeam = matchInput.getToss_winner();
			secondInninigsTeam = matchInput.getToss_winner().equals(matchInput.getTeam1()) ? 
					matchInput.getTeam2() : matchInput.getTeam1();
		} else {
			secondInninigsTeam = matchInput.getToss_winner();
			firstInningsTeam = matchInput.getToss_winner().equals(matchInput.getTeam1()) ? 
					matchInput.getTeam2() : matchInput.getTeam1();
		}
		
		match.setTeam1(firstInningsTeam);
		match.setTeam2(secondInninigsTeam);
		match.setTossWinner(matchInput.getToss_winner());
		match.setTossDecision(matchInput.getToss_decision());
		match.setResult(matchInput.getResult());
		match.setResultMargin(matchInput.getResult_margin());
		match.setUmpire1(matchInput.getUmpire1());
		match.setUmpire2(matchInput.getUmpire2());
		
		return match;

	}

}