package is.core.domain.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import is.core.domain.entities.taskparameters.ImagePlotParameters;
import is.core.domain.entities.taskparameters.Parameters;
import is.core.domain.enums.ExplainerType;
import is.core.domain.enums.ExplanationError;
import is.core.domain.enums.PlotType;
import is.core.domain.enums.TaskStatus;
import is.core.domain.entities.files.Data;
import is.core.domain.entities.files.PersistedFile;
import is.core.domain.entities.files.Result;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The task class contains the instructions to start an explanation
 * Tasks are stored in a queue and will be executed one by one
 */
@Getter
@Setter
@Document("tasks")
public class ExplanationTask extends PersistedFile {

    private String aiBundleId;
    private String aiBundleName; //redundant name, since names can't be altered and is slightly more performant than having to retrieve every bundle every X seconds
    private List<Data> dataList;
    private List<Result> results;
    private ExplainerType explainerType;
    private PlotType plotType;
    private TaskStatus status;
    private List<ExplanationError> errors;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime timeScheduled;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime timeStarted;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime timeDone;
    private Long processDuration;
    private Parameters parameters;

    public ExplanationTask() {
        super(null);
        this.dataList = new ArrayList<>();
        this.results = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    public ExplanationTask(String aiBundleId, String aiBundleName, ExplainerType explainerType, PlotType plotType, TaskStatus status, LocalDateTime timeScheduled) {
        this();
        this.aiBundleId = aiBundleId;
        this.aiBundleName = aiBundleName;
        this.explainerType = explainerType;
        this.plotType = plotType;
        this.status = status;
        this.timeScheduled = timeScheduled;
    }

    public ExplanationTask(String aiBundleId, String aiBundleName, ExplainerType explainerType, PlotType plotType, TaskStatus status, LocalDateTime timeScheduled, Parameters parameters) {
        this(aiBundleId, aiBundleName, explainerType, plotType, status, timeScheduled);
        this.parameters = parameters;
    }

    public ExplanationTask(String aiBundleId, String aiBundleName, ExplainerType explainerType, PlotType plotType, TaskStatus status, LocalDateTime timeScheduled, int rankedOutputs, boolean normalize) {
        this(aiBundleId, aiBundleName, explainerType, plotType, status, timeScheduled);
        this.parameters = new ImagePlotParameters(rankedOutputs, normalize);
    }
}
