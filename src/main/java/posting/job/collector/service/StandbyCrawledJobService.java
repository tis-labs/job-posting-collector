package posting.job.collector.service;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;
import posting.job.collector.command.StandbyCrawledJobCommand;
import posting.job.collector.domain.TempJobPosting;

@Service
@RequiredArgsConstructor
public class StandbyCrawledJobService {
    private final CommandGateway commandGateway;

    public void standby(TempJobPosting tempJobPosting) {
        var standbyCrawledJobCommand = new StandbyCrawledJobCommand(
                tempJobPosting.jobId(),
                tempJobPosting.jobTitle(),
                tempJobPosting.crawledAt()
        );
        commandGateway.send(standbyCrawledJobCommand);
    }
}
