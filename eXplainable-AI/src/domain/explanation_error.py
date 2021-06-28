from enum import Enum, auto


class ExplanationError(Enum):
    LOW_BATCH_SIZE = auto()
    CPU_SWITCH = auto()
    CPU_FAILURE = auto()
    UNKNOWN = auto()
