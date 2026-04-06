from pathlib import Path
import subprocess
import sys


def load_mysql_config(config_path: Path) -> dict[str, str]:
    config: dict[str, str] = {}
    current = None
    for raw in config_path.read_text().splitlines():
        line = raw.rstrip()
        if not line.strip():
            continue
        if not line.startswith(" "):
            current = line[:-1].strip() if line.endswith(":") else None
            continue
        if current != "mysql" or ":" not in line:
            continue
        key, value = line.strip().split(":", 1)
        value = value.strip().strip("'\"")
        config[key.strip()] = value
    return config


def main() -> int:
    if len(sys.argv) != 3:
        raise SystemExit("Usage: bootstrap_proxy_rank_schema.py <database.yml> <schema.sql>")

    config_path = Path(sys.argv[1])
    schema_path = Path(sys.argv[2])
    config = load_mysql_config(config_path)

    subprocess.run(
        [
            "mysql",
            "-h" + config["host"],
            "-u" + config["username"],
            "-p" + config["password"],
            "-D",
            config["database"],
        ],
        input=schema_path.read_text(),
        text=True,
        check=True,
    )

    print("Proxy rank schema bootstrapped.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
