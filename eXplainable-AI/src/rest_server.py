import logging
import os

from config.logging_properties import LoggingProperties
from src.rest.rest_controller import RestController

if __name__ == '__main__':
    if not os.path.exists(LoggingProperties().log_folder):
        os.makedirs(LoggingProperties().log_folder)
    logging.basicConfig(level=LoggingProperties().log_level,
                        filename=LoggingProperties().log_folder + LoggingProperties().log_file_name,
                        format=LoggingProperties().log_format, datefmt=LoggingProperties().log_date_format)

    logging.getLogger('azure.storage.blob').setLevel(LoggingProperties().log_azure_level)

    logging.info("Rest server started!")

    rest = RestController()
    rest.run()
