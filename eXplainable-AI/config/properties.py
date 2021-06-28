from config.api_properties import ApiProperties
from config.azure_storage_properties import AzureStorageProperties
from config.logging_properties import LoggingProperties
from config.shap_properties import ShapProperties
from config.testing_properties import TestingProperties


class Properties:
    api: ApiProperties = ApiProperties()
    logging: LoggingProperties = LoggingProperties()
    testing: TestingProperties = TestingProperties()
    storage: AzureStorageProperties = AzureStorageProperties()
    shap: ShapProperties = ShapProperties()