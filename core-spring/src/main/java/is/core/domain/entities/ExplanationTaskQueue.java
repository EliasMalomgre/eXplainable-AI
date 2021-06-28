package is.core.domain.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;

@Getter
@Setter
@Document("taskqueue")
public class ExplanationTaskQueue {
    @Id
    private String id;

    private LinkedList<ExplanationTask> taskQueue;

    public ExplanationTaskQueue() {
        //todo check priorityqueue for later on?
        this.taskQueue = new LinkedList<>();
    }
}
