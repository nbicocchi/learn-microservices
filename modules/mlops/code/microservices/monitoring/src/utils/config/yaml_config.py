import yaml


def yaml_config(config_file_path, debug=False):
    with open(config_file_path, "r") as stream:
        config = yaml.safe_load(stream)

    if debug:
        for k, v in config.items():
            print("{}: {}".format(k, v))

    return config
