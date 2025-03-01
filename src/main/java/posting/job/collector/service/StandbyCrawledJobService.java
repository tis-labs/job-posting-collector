package posting.job.collector.service;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;
import posting.job.collector.command.StandbyCrawledJobCommand;
import posting.job.collector.domain.CrawledJobPosting;

@Service
@RequiredArgsConstructor
public class StandbyCrawledJobService {
    private final CommandGateway commandGateway;

    public void standby(CrawledJobPosting crawledJobPosting) {
        var standbyCrawledJobCommand = new StandbyCrawledJobCommand(
                crawledJobPosting.getCrawledJobId(),
                crawledJobPosting.getCrawledAt(),
                crawledJobPosting.getJobTitle(),
                crawledJobPosting.getJobIdentity(),
                crawledJobPosting.getJobCompany(),
                crawledJobPosting.getJobFamily(),
                crawledJobPosting.getJobType(),
                crawledJobPosting.getJobUrl(),
                crawledJobPosting.getJobOptionalInformation()
        );
        commandGateway.send(standbyCrawledJobCommand);
    }
}
