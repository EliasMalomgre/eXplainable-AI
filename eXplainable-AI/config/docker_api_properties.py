class ApiProperties:
    api_server_host: str = "0.0.0.0"
    api_server_port: int = 4080
    api_server_debug: bool = True
    api_temp_storage = "../temp_storage/"

    api_model_prefix = "/model/"
    api_explanation_prefix = "/explanation/"
    api_test_prefix = "/test/"

    api_spring_core_base_url: str = "http://java-backend:7080/"
