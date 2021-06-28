import logging

from config.logging_properties import LoggingProperties

if __name__ == '__main__':
    logging.basicConfig(level=LoggingProperties().log_level,
                        filename=LoggingProperties().log_folder + LoggingProperties().log_file_name,
                        format=LoggingProperties().log_format, datefmt=LoggingProperties().log_date_format)