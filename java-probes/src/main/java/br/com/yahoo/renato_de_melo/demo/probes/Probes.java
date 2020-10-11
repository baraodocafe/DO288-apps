package br.com.yahoo.renato_de_melo.demo.probes;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class Probes {
	private static Logger LOGGER = Logger.getLogger(Probes.class.getName());
	
	@Autowired private ApplicationContext context;
	@Autowired private ApplicationAvailability applicationAvailability;
	
	@GetMapping("reject")
	public RedirectView rejectTraffic() {
		AvailabilityChangeEvent.publish(context, ReadinessState.REFUSING_TRAFFIC);
		return new RedirectView("status");
	}
	@GetMapping("down")
	public RedirectView down() {
		AvailabilityChangeEvent.publish(context, LivenessState.BROKEN);
		return new RedirectView("status");
	}
	@GetMapping("accept")
	public RedirectView acceptTraffic() {
		AvailabilityChangeEvent.publish(context, ReadinessState.ACCEPTING_TRAFFIC);
		return new RedirectView("status");
	}
	@GetMapping("live")
	public RedirectView live() {
		AvailabilityChangeEvent.publish(context, LivenessState.CORRECT);
		return new RedirectView("status");
	}
    @EventListener
    public void onEvent(AvailabilityChangeEvent<LivenessState> event) {
        switch (event.getState()) {
        case BROKEN:
        	LOGGER.info("Probes is down");
            break;
        case CORRECT:
        	LOGGER.info("Probes is live");
        }
        this.logStatus();
    }
    @EventListener
    public void onEventReadiness(AvailabilityChangeEvent<ReadinessState> event) {
        switch (event.getState()) {
        case REFUSING_TRAFFIC:
        	LOGGER.info("Probes is rejecting traffic");
            break;
        case ACCEPTING_TRAFFIC:
        	LOGGER.info("Probes is accepting traffic");
        }
        this.logStatus();
    }
    @GetMapping("status")
    public String logStatus() {
    	String liveStatus = "UNKNOWN";
    	switch (applicationAvailability.getLivenessState()) {
	    	case CORRECT: {
	    		liveStatus = "Live";
	    		break;
	    	}
	    	case BROKEN: {
	    		liveStatus = "Down";
	    	}
    	}
    	String readyStatus = "UNKNOWN";
    	switch (applicationAvailability.getReadinessState()) {
	    	case ACCEPTING_TRAFFIC: {
	    		readyStatus = "Accepting Traffic";
	    		break;
	    	}
	    	case REFUSING_TRAFFIC: {
	    		readyStatus = "Rejecting Traffic";
	    	}
    	}
    	String response = String.format("Probes is %s and %s",liveStatus, readyStatus);
    	LOGGER.info(response);
    	
    	response = response + 
    			"<br><br><a href='/down'>Down</a>" +
    			"<br><a href='/live'>Live</a>" +
    			"<br><a href='/reject'>Reject</a>" +
    			"<br><a href='/accept'>Accept</a>";
    	
    	return response;
    }
}