import os


class PathUtilities:

    @staticmethod
    def get_file_extension(file_name):
        """
        Gets the file extension from a file name

        :param file_name:   The filename from which the extension has to be extracted
        :return: the file extension
        """
        _, extension = os.path.splitext(file_name)
        return extension
