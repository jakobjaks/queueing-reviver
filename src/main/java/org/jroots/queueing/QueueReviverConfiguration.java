package org.jroots.queueing;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.*;
import javax.validation.constraints.*;
import javax.validation.constraints.NotEmpty;

public class QueueReviverConfiguration extends Configuration {

    @NotEmpty
    private String exitDlQueueUrl;

    @NotEmpty
    private String incomingDlQueueUrl;

    @NotEmpty
    private String incomingQueueUrl;

    public String getExitDlQueueUrl() {
        return exitDlQueueUrl;
    }

    public void setExitDlQueueUrl(String exitDlQueueUrl) {
        this.exitDlQueueUrl = exitDlQueueUrl;
    }

    public String getIncomingDlQueueUrl() {
        return incomingDlQueueUrl;
    }

    public void setIncomingDlQueueUrl(String incomingDlQueueUrl) {
        this.incomingDlQueueUrl = incomingDlQueueUrl;
    }

    public String getIncomingQueueUrl() {
        return incomingQueueUrl;
    }

    public void setIncomingQueueUrl(String incomingQueueUrl) {
        this.incomingQueueUrl = incomingQueueUrl;
    }
}
