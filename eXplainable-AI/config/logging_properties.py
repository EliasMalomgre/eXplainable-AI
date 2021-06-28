import logging


class LoggingProperties:
    log_level = logging.INFO
    log_folder: str = 'logs/'
    log_file_name: str = 'log_file.log'
    log_format: str = '| %(asctime)s | %(levelname)s | %(message)s |'
    log_date_format: str = '%d-%m-%y %H:%M:%S'

    log_azure_level = logging.ERROR
