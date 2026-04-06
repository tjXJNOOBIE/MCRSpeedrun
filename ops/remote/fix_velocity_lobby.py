from pathlib import Path
import re
import sys


def main() -> int:
    if len(sys.argv) != 3:
        raise SystemExit("Usage: fix_velocity_lobby.py <velocity.toml> <lobby-address>")

    config_path = Path(sys.argv[1])
    lobby_address = sys.argv[2]
    replacement = f'lobby = "{lobby_address}"'

    text = config_path.read_text()
    updated_text, substitutions = re.subn(r"^lobby = .*$", replacement, text, flags=re.MULTILINE)
    if substitutions != 1:
        raise SystemExit("Failed to locate lobby entry in velocity.toml")

    config_path.write_text(updated_text)
    print(replacement)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
